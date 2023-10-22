package com.cy.literouter

import com.cy.literouter.anno.Router
import com.cy.literouter.constant.GENERATE_CLASS
import com.cy.literouter.constant.GENERATE_METHOD_GET_ROUTER_MAP
import com.cy.literouter.constant.GENERATE_METHOD_INIT
import com.cy.literouter.constant.GENERATE_PACKAGE
import com.cy.literouter.constant.GLOBAL_ROUTER_MAP
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class RouterProcessor : AbstractProcessor() {

    companion object {
        private const val TAG = "RouterProcessor"
        private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        private const val KAPT_KOTLIN_OPTION_HOST = "Host"
        private val CLASS_STAR = Class::class.asClassName().parameterizedBy(STAR)
        private val HASH_MAP = HashMap::class.asClassName()
            .parameterizedBy(String::class.asClassName(), CLASS_STAR)
    }


    private lateinit var mFiler: Filer
    private lateinit var mEleUtils: Elements
    private lateinit var typeUtils: Types
    private lateinit var mMessage: Messager

    override fun init(processingEnv: ProcessingEnvironment)  {
        log("process init")
        super.init(processingEnv)
        mEleUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
        mFiler = processingEnv.filer
        mMessage = processingEnv.messager
    }

    /**
     * kotlinPoet : https://square.github.io/kotlinpoet/#l-for-literals
     */
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        log("process start")
        if (annotations == null || annotations.isEmpty()) return false
        val ktGeneratedDir =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return false
        val codeBuilder = CodeBlock.Builder()
        log(processingEnv.options.keys)
        var host = processingEnv.options[KAPT_KOTLIN_OPTION_HOST]
        /*if (host.isNullOrBlank()) {
            log(
                """
                请在模块的 defaultConfig {} 中配置Host，避免路由表太多影响查询效率：
                javaCompileOptions {
                    annotationProcessorOptions {
                        arguments = ["Host": "app"]
                    }
                }
                """.trimIndent()
            )
            host = ""
        } else {
            log("发现Host:$host")
        }*/

        roundEnv.getElementsAnnotatedWith(Router::class.java)
            .forEach { element ->
                logElement(element)
                val annotation = element.getAnnotation(Router::class.java)
                val url = annotation.path
                val className = element.simpleName.toString()
                val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
                val target = "$packageName.$className"
                codeBuilder.addStatement(
                    "%L[%S] = %L::class.java",
                    GLOBAL_ROUTER_MAP,
                    url,
                    ClassName.bestGuess(target)
                )
            }

        val hasInitProperty = "hasInit"
        val initialFunc = FunSpec.builder(GENERATE_METHOD_INIT)
            .addModifiers(KModifier.PRIVATE)
            .addStatement("$hasInitProperty = true")
            .addCode(codeBuilder.build())
            .build()
        val routerInitClass = TypeSpec.objectBuilder(GENERATE_CLASS)
            .addProperty(
                PropertySpec.builder(GLOBAL_ROUTER_MAP, HASH_MAP)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("%T()", HASH_MAP)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(hasInitProperty, Boolean::class, KModifier.PRIVATE)
                    .mutable()
                    .initializer("false")
                    .build()
            )
            .addFunction(initialFunc)
            .addFunction(
                FunSpec.builder(GENERATE_METHOD_GET_ROUTER_MAP)
                    .addStatement("if ($hasInitProperty) return %L", GLOBAL_ROUTER_MAP)
                    .addStatement("%N()", initialFunc)
                    .addStatement("return %L", GLOBAL_ROUTER_MAP)
                    .returns(HASH_MAP)
                    .build()
            )
            .build()
        FileSpec.builder(GENERATE_PACKAGE, GENERATE_CLASS)
            .addType(routerInitClass)
            .build()
            .writeTo(mFiler)
        return true
    }

    /**
     * 这里必须指定，这个注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称
     * 也可以 @SupportedAnnotationTypes("")
     * @return 注解器所支持的注解类型集合，如果没有这样的类型，则返回一个空集合
     */
    override fun getSupportedAnnotationTypes(): Set<String> {
        return ImmutableSet.of(Router::class.java.canonicalName);
    }

    /**
     * 指定使用的Java版本，通常这里返回SourceVersion.latestSupported()，默认返回SourceVersion.RELEASE_6
     *
     * @return 使用的Java版本
     */
    override fun getSupportedSourceVersion(): SourceVersion? {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(KAPT_KOTLIN_GENERATED_OPTION_NAME, KAPT_KOTLIN_OPTION_HOST)
    }

    private fun logElement(element: Element) {
        log("---------- 开始打印 element $element")
        log("element.getKind():" + element.kind)
        log("element.getSimpleName():" + element.simpleName)
        log("element.getClass():" + element.javaClass)
        log("mEleUtils.getPackageOf(element):" + mEleUtils.getPackageOf(element))
        log("mEleUtils.getModuleOf(element):" + mEleUtils.getModuleOf(element))
        log("mEleUtils.getDocComment(element):" + mEleUtils.getDocComment(element))
        log("mEleUtils.getOrigin(element):" + mEleUtils.getOrigin(element))
        if (element is TypeElement) {
            log("mEleUtils.getBinaryName(element):" + mEleUtils.getBinaryName(element))
        }
    }

    private fun log(msg: Any) {
        if (!openLog) {
            return
        }
        println("$TAG:$msg")
    }

    private val openLog = true
}
package com.cy.literouter

import android.app.Application
import android.content.Context
import android.content.Intent
import com.cy.literouter.constant.GENERATE_CLASS
import com.cy.literouter.constant.GENERATE_METHOD_GET_ROUTER_MAP
import com.cy.literouter.constant.GENERATE_PACKAGE
import com.cy.literouter.uitl.LogHelper

object LiteRouter {

    private const val TAG = "LiteRouter"

    private var mRouterMap: HashMap<String, Class<*>>? = null
    private var app: Application? = null
    private var idDebug: Boolean = false
    fun init(context: Application, debug: Boolean) {
        idDebug = debug
        app = context
    }

    private fun initRouterMap() {
        LogHelper.tip(TAG, "开始初始化路由表...")
        try {
            val routerInitClass = Class.forName("$GENERATE_PACKAGE.$GENERATE_CLASS")
            routerInitClass.kotlin
            routerInitClass.declaredMethods.forEach {
                println("kotlin method:" + it.name)
                if (it.name == GENERATE_METHOD_GET_ROUTER_MAP) {
                    val result = it.invoke(routerInitClass.kotlin.objectInstance)
                    mRouterMap = result as HashMap<String, Class<*>>
                }
            }
            if (mRouterMap == null) {
                println("routerInitClass:$routerInitClass")
                throw NullPointerException("请检查混淆是否配置成功。\n未找到类#方法：$GENERATE_PACKAGE.$GENERATE_CLASS#${GENERATE_METHOD_GET_ROUTER_MAP}。")
            }
            LogHelper.tip(TAG, "路由表初始化完成，长度为：${mRouterMap?.size}")
        } catch (e: Exception) {
            LogHelper.errorTip(TAG, "路由表初始化失败，请查看日志")
            e.printStackTrace()
        }
    }

    private fun checkRouterMapInit(): HashMap<String, Class<*>>? {
        if (mRouterMap == null) {
            initRouterMap()
        }
        return mRouterMap
    }

    fun routeTo(context: Context, path: String) = context.LiteRouteTo(path)


    fun Context.LiteRouteTo(path: String) {
        val map = checkRouterMapInit()
        if (map == null) {
            LogHelper.tip(TAG, "路由表初始化失败，终止跳转")
            return
        }
        if (map.isEmpty()) {
            LogHelper.tip(TAG, "路由表中路由为空，终止跳转")
        }
        if (!map.containsKey(path) || map[path] == null) {
            LogHelper.tip(TAG, "路由未命中：$path ，终止跳转")
            return
        }
        val activityClazz = map[path]
        val intent = Intent(this, activityClazz)
        startActivity(intent)
    }

    fun getContext() = app
    fun isDebug() = idDebug

}
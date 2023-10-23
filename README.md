# LiteRouter
[![](https://jitpack.io/v/caoyangim/LiteRouterDemo.svg)](https://jitpack.io/#caoyangim/LiteRouterDemo)
## 依赖
``` gradle
// project build.gradle
repositories {
  maven { url 'https://jitpack.io' }
}
```
``` gradle
// module build.gradle
kapt 'com.github.caoyangim.LiteRouterDemo:liteRouter-compile:v1.1.2'
implementation 'com.github.caoyangim.LiteRouterDemo:liteRouter:v1.1.2'
```
## 使用
``` kotlin
@Router("/main")
class MainActivity : AppCompatActivity(){...}

// jump
LiteRouter.routeTo(context,"/main")
// 方式二
liteRouteTo("/main")

```
## 混淆
```
-keep class com.cy.literouter.generated.RouterInit {
    <fields>;
    java.util.HashMap getRouterMap();
}
```

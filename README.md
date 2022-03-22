# AsmDemo

提供asm和javassist两种方式，默认使用asm，通过下面开关控制
privacyExtension{
    //使用javassist，需添加javassist包
    useAsm=false
}
javassist写法来自https://github.com/AlvinScrp/PrivacyChecker

因为不是通过buildSrc，所以需要本地打包插件./gradlew uploadArchives，一开始如果找不到插件，先注释apply plugin: 'PrivacyCheckPlugin'再打包。
如果移植，需要将PrivacyConfig里面的一些包名信息改掉。
asm hook原理看
https://juejin.cn/post/7077536626276040734

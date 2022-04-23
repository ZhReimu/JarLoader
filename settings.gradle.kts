pluginManagement {
    repositories {
        maven {
            name = "阿里云 Gradle 插件镜像"
            setUrl("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven {
            name = "阿里云 Spring 插件镜像"
            setUrl("https://maven.aliyun.com/repository/spring-plugin")
        }
        mavenCentral()
    }
}
rootProject.name = "JarLoader"


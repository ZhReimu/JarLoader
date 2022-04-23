import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    application
}

group = "com.mrx"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "阿里云镜像"
        setUrl("https://maven.aliyun.com/repository/central")
    }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.mrx.Main")
}
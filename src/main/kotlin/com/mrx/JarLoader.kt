package com.mrx

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarInputStream

/**
 * @author Mr.X
 * @since 2022-04-23-0023
 **/
class JarLoader private constructor(private val jars: Array<URL>) {

    private val classLoader = URLClassLoader(jars)

    object Loader {

        private val jars = ArrayList<URL>()

        fun loadJar(jarPath: String): Loader {
            return loadJar(File(jarPath))
        }

        fun loadJar(jarFile: File): Loader {
            jars.add(jarFile.toURI().toURL())
            return this
        }

        fun load(): JarLoader {
            if (jars.isEmpty()) {
                throw IllegalStateException("请先使用 loadJar 方法加载至少一个 jar!")
            }
            return JarLoader(jars.toTypedArray())
        }
    }

    /**
     * 加载 jar 里的指定 class
     * @param clazz String class 名
     * @return Class<*> class 对象
     */
    fun loadClass(clazz: String): Class<*> {
        return classLoader.loadClass(clazz)
    }

    /**
     * 效果与另一个重载方法相同
     */
    fun getInstanceOfClass(
        clazz: String,
        constructorArgsType: Class<*>? = null,
        constructorArgs: Array<*>? = null
    ) = getInstanceOfClass(loadClass(clazz), constructorArgsType, constructorArgs)


    /**
     * 根据指定方法参数获取对应 class 实例
     * @param clazz Class<*> 要获取实例的 class
     * @param constructorArgsType Class<*>? 构造方法参数类型
     * @param constructorArgs Array<*>? 构造方法参数
     * @return Any class 实例
     */
    fun getInstanceOfClass(
        clazz: Class<*>,
        constructorArgsType: Class<*>? = null,
        constructorArgs: Array<*>? = null
    ): Any {
        return if (constructorArgs == null) {
            clazz.getDeclaredConstructor().apply {
                isAccessible = true
            }.newInstance()
        } else {
            clazz.getDeclaredConstructor(constructorArgsType).apply {
                isAccessible = true
            }.newInstance(constructorArgs)
        }
    }

    inline fun <reified T> getInstanceAndCastTo(
        clazz: String,
        target: Class<T>,
        constructorArgsType: Class<*>? = null,
        constructorArgs: Array<*>? = null
    ): T {
        val instance = getInstanceOfClass(clazz, constructorArgsType, constructorArgs)
        if (instance is T) {
            return getInstanceOfClass(loadClass(clazz), constructorArgsType, constructorArgs) as T
        }
        throw ClassCastException("造型失败, ${instance.javaClass} 无法被造型为 $target")
    }

    /**
     * 执行指定 class 中的指定 方法, 方法 参数类型为 argsType, 参数内容为 args
     * @param clazz String 方法 所在 class
     * @param method String 方法 名
     * @param methodArgsType Class<*>? 方法 参数类型
     * @param methodArgs Array<out Any> 方法 参数内容
     * @param constructorArgsType Class<*>? 构造方法 参数类型
     * @param constructorArgs Array<out Any> 构造方法 参数内容
     * @return Any? method 返回值
     */
    fun invokeMethodInClass(
        clazz: String,
        method: String,
        methodArgsType: Class<*>? = null,
        methodArgs: Array<*>? = null,
        constructorArgsType: Class<*>? = null,
        constructorArgs: Array<*>? = null
    ): Any? {
        // 获取 class 对象
        val classInJar = loadClass(clazz)
        // 使用默认构造方法生成 clazz 对象的实例
        val classInstance = getInstanceOfClass(classInJar, constructorArgsType, constructorArgs)
        // 反射执行其中的 method 方法, 只会获取 public 方法
        return if (methodArgsType == null) {
            classInJar.getMethod(method).invoke(classInstance, methodArgs)
        } else {
            classInJar.getMethod(method, methodArgsType).invoke(classInstance, methodArgs)
        }
    }

    /**
     * 执行指定 class 的 main 方法
     * @param clazz String main 方法所在 class
     * @return Any? main 方法返回值
     */
    fun invokeMainMethod(clazz: String): Any? {
        return invokeMethodInClass(clazz, "main", arrayOf<String>()::class.java, emptyArray<String>())
    }

    /**
     * 调用指定类中的无参构造方法
     * @param clazz String 方法所在类
     * @param method String 方法名
     * @return Any? 方法返回值
     */
    fun invokeMethodWithNoArgs(clazz: String, method: String): Any? {
        return invokeMethodInClass(clazz, method)
    }

    /**
     * 执行 jar 里的 main 方法, 只有当只加载了 1 个 jar 时才能使用
     * @return Any? 方法返回值
     */
    fun invokeMainInJar(): Any? {
        if (jars.size > 1) {
            throw IllegalStateException("jar 个数过多! 当前加载的 jar 个数为 ${jars.size}")
        }
        // 读取 jar 文件
        val jarInputStream = JarInputStream(jars[0].openStream())

        val attrs = jarInputStream.manifest?.mainAttributes
        attrs ?: throw IllegalStateException("读取 jar 的 META-INF/MANIFEST.MF 文件失败")
        try {
            return invokeMainMethod(jarInputStream.manifest.mainAttributes.getValue("Main-Class"))
        } catch (e: IllegalStateException) {
            throw IllegalArgumentException("找不到 Main-Class, 请检查你的 jar 里的 META-INF/MANIFEST.MF 文件内容!")
        }
    }

}
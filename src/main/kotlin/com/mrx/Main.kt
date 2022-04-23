package com.mrx

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val loader = JarLoader.Loader
            .loadJar("MXShell-1.0-all.jar")
            .loadJar("列车时刻表.jar")
            .load()
        with(loader) {
            invokeMainMethod("com.mrx.train.Main")
            invokeMainMethod("com.mrx.mxshell.Main")
        }
    }

}
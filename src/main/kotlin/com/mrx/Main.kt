package com.mrx

import javax.swing.JFrame

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val loader = JarLoader.Loader
            .loadJar("MXShell-1.0-all.jar")
            .loadJar("列车时刻表.jar")
            .load()
        val frame = loader.getInstanceAndCastTo("com.mrx.train.ui.ServerUI", JFrame::class.java)
        frame.isVisible = false
    }

}
package com.mrx

import javax.swing.table.DefaultTableModel

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val loader = JarLoader.Loader
            .loadJar("MXShell-1.0-all.jar")
            .loadJar("列车时刻表.jar")
            .load()
        val model = loader.getInstanceAndCastTo(
            "com.mrx.train.interfaces.MyTableModel",
            DefaultTableModel::class.java,
            arrayOf<String>()::class.java,
            arrayOf("test", "测试")
        )
        println(model.getColumnName(0))
    }

}
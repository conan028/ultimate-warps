package com.conan.mods.warps.fabric.util

import com.conan.mods.warps.fabric.config.baseconfig.BaseConfig.gson
import java.io.File

object ConfigUtil {
    inline fun <reified T> initializeConfig(configFile: File, defaultConfig: T): T {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            configFile.writeText(gson.toJson(defaultConfig))
        }
        return gson.fromJson(configFile.reader(), T::class.java)
    }

    inline fun <reified T> reloadConfig(file: File, defaultConfig: T): T {
        val configString = file.readText()
        return try {
            gson.fromJson(configString, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultConfig
        }
    }
}
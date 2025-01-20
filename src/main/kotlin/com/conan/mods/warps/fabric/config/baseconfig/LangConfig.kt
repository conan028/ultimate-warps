package com.conan.mods.warps.fabric.config.baseconfig

import com.conan.mods.warps.fabric.UltimateWarps.LOGGER
import com.conan.mods.warps.fabric.config.ConfigHandler.config
import com.conan.mods.warps.fabric.config.baseconfig.BaseConfig.gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.nio.file.Files

object LangConfig {

    private var langFile = File("config/UltimateWarps/lang/${config.config.lang}.json")
    private var langMap: MutableMap<String, String> = mutableMapOf()

    init {
        ensureLangFileExists()
        loadLangMap()
    }

    private fun ensureLangFileExists() {
        if (!langFile.exists()) {
            LOGGER.info("[Ultimate Warps] Lang file not found, creating default file at ${langFile.path}")

            val resourcePath = this.javaClass.classLoader.getResourceAsStream("data/ultimate_warps/lang/en_us.json")

            langFile.parentFile.mkdirs()

            if (resourcePath != null) {
                Files.copy(resourcePath, langFile.toPath())
            }
        }
    }

    private fun loadLangMap() {
        langMap = gson.fromJson(langFile.reader(), object : TypeToken<MutableMap<String, String>>() {}.type) ?: mutableMapOf()
    }

    fun reload() {
        langFile = File("config/UltimateWarps/lang/${config.config.lang}.json")
        ensureLangFileExists()
        loadLangMap()
        LOGGER.info("[Ultimate Warps] Language configuration reloaded.")
    }

    fun lang(text: String): String = langMap.getOrDefault(
        text,
        "<gray>[<white>Ultimate Warps</white>] <dark_gray>» <gray>Language property for <yellow>$text</yellow> not found."
    )
}

package com.conan.mods.warps.fabric.config.baseconfig

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object BaseConfig {

    val gson: Gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

    private val configFile = File("config/UltimateWarps/config.json")

    var config = Config()

    init {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            configFile.writeText(gson.toJson(config))
        }

        this.reload()
    }

    fun reload() {
        val configString = configFile.readText()
        val updatedConfig = gson.fromJson(configString, Config::class.java)
        config = updatedConfig
        configFile.writeText(gson.toJson(config))
    }

    data class Config (
        val version: Double = 1.0,
        val lang: String = "en_us",
        val playerWarps: PlayerWarpConfig = PlayerWarpConfig(),
        val economy: EconomyConfig = EconomyConfig()
    )

    data class PlayerWarpConfig (
        val maxWarps: Int = 3,
        val maxLength: Int = 10,
        val lore: List<String> = listOf(
            "<dark_gray>Owner: <white>%owner%",
            "<dark_gray>Visits: <white>%visits%",
            " ",
            "<dark_gray>Rates: <white>%rates%",
            "<dark_gray>Average Rate: <white>%average_rating%"
        ),
        val blackList: MutableSet<String> = mutableSetOf()
    )

    data class EconomyConfig (
        val isEnabled: Boolean = false,
        val warpCost: Double = 1000.0,
        val returnMoneyOnDeletion: Boolean = true
    )

}
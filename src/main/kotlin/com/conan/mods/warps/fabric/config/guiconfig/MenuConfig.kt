package com.conan.mods.warps.fabric.config.guiconfig

import com.conan.mods.warps.fabric.models.MenuItem
import com.conan.mods.warps.fabric.util.ConfigUtil.initializeConfig
import com.conan.mods.warps.fabric.util.ConfigUtil.reloadConfig
import java.io.File

object MenuConfig {

    private val configNames = listOf(
        "generic_menu",
        "server_menu",
        "player_menu",
        "category_menu"
    )

    private val configFiles = configNames.map {
        File("config/UltimateWarps/menu/${it}_config.json")
    }

    var genericWarpConfig: GenericWarpConfig
    var serverWarpConfig: ServerWarpConfig
    var playerWarpConfig: PlayerWarpConfig
    var categoryConfig: CategoryConfig

    init {
        genericWarpConfig = initializeConfig(configFiles[0], GenericWarpConfig())
        serverWarpConfig = initializeConfig(configFiles[1], ServerWarpConfig())
        playerWarpConfig = initializeConfig(configFiles[2], PlayerWarpConfig())
        categoryConfig = initializeConfig(configFiles[3], CategoryConfig())
    }

    fun reload() {
        genericWarpConfig = reloadConfig(configFiles[0], GenericWarpConfig())
        serverWarpConfig = reloadConfig(configFiles[1], ServerWarpConfig())
        playerWarpConfig = reloadConfig(configFiles[2], PlayerWarpConfig())
        categoryConfig = reloadConfig(configFiles[3], CategoryConfig())
    }

    data class GenericWarpConfig(
        val title: String = "<color:#cc3e1b>Ultimate Warps",
        val fillItem: MenuItem = MenuItem("<gray> ", "gray_stained_glass_pane", null),
        val closeItem: MenuItem = MenuItem("<red>Close", "barrier", 22),
        val barItem: MenuItem = MenuItem("<gray> ", "black_stained_glass_pane", null),
        val serverWarpsItem: MenuItem = MenuItem("<green>Server Warps", "green_concrete", 11),
        val playerWarpItems: MenuItem = MenuItem("<green>Player Warps", "green_concrete", 15)
    )

    data class ServerWarpConfig(
        val title: String = "<color:#cc3e1b>Server Warps",
        val fillItem: MenuItem = MenuItem("<gray> ", "gray_stained_glass_pane", null),
        val backItem: MenuItem = MenuItem("<red>Back", "barrier", 49),
        val barItem: MenuItem = MenuItem("<gray> ", "black_stained_glass_pane", null),
        val backPageItem: MenuItem = MenuItem("<green>Back", "arrow", 45),
        val nextPageItem: MenuItem = MenuItem("<green>Next", "arrow", 53)
    )

    data class PlayerWarpConfig(
        val title: String = "<color:#cc3e1b>Player Warps",
        val fillItem: MenuItem = MenuItem("<gray> ", "gray_stained_glass_pane", null),
        val backItem: MenuItem = MenuItem("<red>Back", "barrier", 49),
        val barItem: MenuItem = MenuItem("<gray> ", "black_stained_glass_pane", null),
        val backPageItem: MenuItem = MenuItem("<green>Back", "arrow", 45),
        val nextPageItem: MenuItem = MenuItem("<green>Next", "arrow", 53),
        val filterItem: MenuItem = MenuItem("<gold>Filter", "paper", 47),
    )

    data class CategoryConfig(
        val title: String = "<color:#cc3e1b>Categories",
        val fillItem: MenuItem = MenuItem("<gray> ", "gray_stained_glass_pane", null),
        val backItem: MenuItem = MenuItem("<red>Back", "barrier", 49),
        val barItem: MenuItem = MenuItem("<gray> ", "black_stained_glass_pane", null),
        val backPageItem: MenuItem = MenuItem("<green>Back", "arrow", 45),
        val nextPageItem: MenuItem = MenuItem("<green>Next", "arrow", 53),
        val blackList: MutableList<String> = mutableListOf()
    )

}

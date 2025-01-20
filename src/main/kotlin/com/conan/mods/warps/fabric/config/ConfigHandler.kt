package com.conan.mods.warps.fabric.config

import com.conan.mods.warps.fabric.config.baseconfig.BaseConfig
import com.conan.mods.warps.fabric.config.baseconfig.LangConfig
import com.conan.mods.warps.fabric.config.baseconfig.ReadMe
import com.conan.mods.warps.fabric.config.dbconfig.DatabaseConfig
import com.conan.mods.warps.fabric.config.guiconfig.MenuConfig
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton

object ConfigHandler {

    //    Config Files
    var config = BaseConfig
    var menuConfig = MenuConfig
    var langConfig = LangConfig
    var dbConfig = DatabaseConfig

    init {
        DatabaseHandlerSingleton
        ReadMe
    }
}
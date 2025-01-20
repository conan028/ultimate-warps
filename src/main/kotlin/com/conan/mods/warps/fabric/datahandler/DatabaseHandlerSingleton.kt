package com.conan.mods.warps.fabric.datahandler

import com.conan.mods.warps.fabric.config.ConfigHandler
import com.conan.mods.warps.fabric.enums.DataStoreType

object DatabaseHandlerSingleton {

    var dbHandler: DatabaseHandler? = null

    init {
        this.reload()
    }

    fun reload() {
        dbHandler = when (ConfigHandler.dbConfig.dbConfig.dataStore) {
            DataStoreType.JSON -> JsonDBHandler()
            DataStoreType.MONGODB -> MongoDBHandler()
        }
    }
}
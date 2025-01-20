package com.conan.mods.warps.fabric.config.dbconfig

import com.conan.mods.warps.fabric.config.baseconfig.BaseConfig.gson
import com.conan.mods.warps.fabric.enums.DataStoreType
import java.io.File

object DatabaseConfig {

    private val dbConfigFile = File("config/UltimateWarps/datastore/db_settings.json")

    var dbConfig = DBConfig()

    init {
        if (!dbConfigFile.exists()) {
            dbConfigFile.parentFile.mkdirs()
            dbConfigFile.createNewFile()
            dbConfigFile.writeText(gson.toJson(dbConfig))
        }

        this.reload()
    }

    fun reload() {
        val configString = dbConfigFile.readText()
        val updatedConfig = gson.fromJson(configString, DBConfig::class.java)
        dbConfig = updatedConfig
        dbConfigFile.writeText(gson.toJson(dbConfig))
    }

    data class DBConfig(
        val dataStore: DataStoreType = DataStoreType.JSON,
        val mongoDB: MongoDBConfig = MongoDBConfig()
    )

    data class MongoDBConfig(
        val connectionString: String = "mongodb://localhost:27017/",
        val database: String = "warps",
        val playerWarpCollection: String = "player_warp_collection",
        val serverWarpCollection: String = "server_warp_collection"
    )

}
package com.conan.mods.warps.fabric.datahandler

import com.conan.mods.warps.fabric.config.ConfigHandler.dbConfig
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.models.Warp
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters


class MongoDBHandler : DatabaseHandler {

    private val mongoClientSettings = MongoClientSettings
        .builder()
        .applyConnectionString(ConnectionString(dbConfig.dbConfig.mongoDB.connectionString))
        .build()

    private val mongoClient = MongoClients.create(mongoClientSettings)
    private val database = mongoClient.getDatabase(dbConfig.dbConfig.mongoDB.database)

    private val playerWarpCollection: MongoCollection<Warp> = database.getCollection(dbConfig.dbConfig.mongoDB.playerWarpCollection, Warp::class.java)
    private val serverWarpCollection: MongoCollection<Warp> = database.getCollection(dbConfig.dbConfig.mongoDB.serverWarpCollection, Warp::class.java)

    override fun getWarps(type: WarpType): MutableList<Warp> {
        val warps = when (type) {
            WarpType.SERVER -> serverWarpCollection.find().toMutableList()
            WarpType.PLAYER -> playerWarpCollection.find().toMutableList()
        }

        return warps
    }

    override fun getWarpsByUUID(uuid: String): List<Warp> {
        return playerWarpCollection.find(Filters.eq("owner", uuid)).toList()
    }

    override fun getWarpByName(name: String, type: WarpType): Warp? {
        val warps = when (type) {
            WarpType.SERVER -> serverWarpCollection.find(Filters.eq("name", name)).first()
            WarpType.PLAYER -> playerWarpCollection.find(Filters.eq("name", name)).first()
        }

        return warps
    }

    override fun addWarp(warp: Warp, type: WarpType) {
        when (type) {
            WarpType.SERVER -> {
                serverWarpCollection.insertOne(warp).wasAcknowledged()
            }
            WarpType.PLAYER -> {
                playerWarpCollection.insertOne(warp).wasAcknowledged()
            }
        }
    }

    override fun deleteWarp(warp: Warp, type: WarpType) {
        when (type) {
            WarpType.SERVER -> {
                val filter = Filters.eq("name", warp.name)
                serverWarpCollection.findOneAndDelete(filter)
            }
            WarpType.PLAYER -> {
                val filter = Filters.eq("name", warp.name)
                playerWarpCollection.findOneAndDelete(filter)
            }
        }
    }

    override fun updateWarp(warp: Warp, type: WarpType) {
        when (type) {
            WarpType.SERVER -> {
                serverWarpCollection.findOneAndReplace(Filters.eq("name", warp.name), warp)
            }
            WarpType.PLAYER -> {
                playerWarpCollection.findOneAndReplace(Filters.eq("name", warp.name), warp)
            }
        }
    }
}
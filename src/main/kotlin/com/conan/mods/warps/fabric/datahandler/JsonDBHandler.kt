package com.conan.mods.warps.fabric.datahandler

import com.conan.mods.warps.fabric.commands.admin.OldWarp
import com.conan.mods.warps.fabric.config.baseconfig.BaseConfig.gson
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.models.OwnerInfo
import com.conan.mods.warps.fabric.models.Warp
import com.conan.mods.warps.fabric.models.WarpCoordinates
import com.conan.mods.warps.fabric.models.WarpStats
import com.google.gson.reflect.TypeToken
import net.minecraft.util.math.BlockPos
import java.io.File

class JsonDBHandler : DatabaseHandler {

    private val playerWarpFile = File("config/UltimateWarps/datastore/data/player_warps.json")
    private var playerWarps: MutableList<Warp> = mutableListOf()

    private val serverWarpFile = File("config/UltimateWarps/datastore/data/server_warps.json")
    private var serverWarps: MutableList<Warp> = mutableListOf()

    init {
        if (!playerWarpFile.exists()) {
            playerWarpFile.parentFile.mkdirs()
            playerWarpFile.createNewFile()
            playerWarpFile.writeText(gson.toJson(playerWarps))
        }

        playerWarps = gson.fromJson(playerWarpFile.reader(), object : TypeToken<MutableList<Warp>>(){}.type)

        if (!serverWarpFile.exists()) {
            serverWarpFile.parentFile.mkdirs()
            serverWarpFile.createNewFile()
            serverWarpFile.writeText(gson.toJson(playerWarps))
        }

        serverWarps = gson.fromJson(serverWarpFile.reader(), object : TypeToken<MutableList<Warp>>(){}.type)
    }


    fun fixOldData() {
        val oldWarpList = gson.fromJson<MutableList<OldWarp>>(playerWarpFile.reader(), object : TypeToken<MutableList<OldWarp>>() {}.type)

        playerWarps.clear()

        oldWarpList.forEach { oldWarp ->
            val coordinates = BlockPos.fromLong(oldWarp.coords)
            playerWarps.add(
                Warp(
                    oldWarp.name,
                    oldWarp.category,
                    OwnerInfo(
                        oldWarp.playerName,
                        oldWarp.playerUUID,
                    ),
                    WarpStats(),
                    WarpCoordinates(
                        oldWarp.dimension,
                        coordinates.x.toDouble(),
                        coordinates.y.toDouble(),
                        coordinates.z.toDouble(),
                        0f,
                        0f,
                    )
                )
            )
        }

        savePlayerWarps()
    }

    override fun getWarps(type: WarpType): MutableList<Warp> {
        val warps = when (type) {
            WarpType.PLAYER -> playerWarps
            WarpType.SERVER -> serverWarps
        }
        return warps
    }

    override fun getWarpsByUUID(uuid: String): List<Warp> {
        return playerWarps.filter { it.ownerInfo?.owner == uuid }
    }

    override fun getWarpByName(name: String, type: WarpType) : Warp? {
        val warp = when (type) {
            WarpType.PLAYER -> this.playerWarps.find { it.name == name }
            WarpType.SERVER -> this.serverWarps.find { it.name == name }
        }
        return warp
    }

    override fun addWarp(warp: Warp, type: WarpType) {
        when (type) {
            WarpType.PLAYER -> {
                playerWarps.add(warp)
                savePlayerWarps()
            }
            WarpType.SERVER -> {
                serverWarps.add(warp)
                saveServerWarps()
            }
        }
    }

    override fun deleteWarp(warp: Warp, type: WarpType) {
        when (type) {
            WarpType.PLAYER -> {
                playerWarps.remove(warp)
                savePlayerWarps()
            }
            WarpType.SERVER -> {
                serverWarps.remove(warp)
                saveServerWarps()
            }
        }
    }

    override fun updateWarp(warp: Warp, type: WarpType) {
        when (type) {
            WarpType.PLAYER -> {
                val index = playerWarps.indexOfFirst { it.name == warp.name }
                if (index != -1) {
                    playerWarps[index] = warp
                    savePlayerWarps()
                }
            }
            WarpType.SERVER -> {
                val index = serverWarps.indexOfFirst { it.name == warp.name }
                if (index != -1) {
                    serverWarps[index] = warp
                    saveServerWarps()
                }
            }
        }
    }

    private fun savePlayerWarps() {
        playerWarpFile.writeText(gson.toJson(playerWarps))
    }

    private fun saveServerWarps() {
        serverWarpFile.writeText(gson.toJson(serverWarps))
    }

}
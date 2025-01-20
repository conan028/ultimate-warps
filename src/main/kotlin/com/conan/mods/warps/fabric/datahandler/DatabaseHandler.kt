package com.conan.mods.warps.fabric.datahandler

import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.models.Warp

interface DatabaseHandler {

    fun getWarps(type: WarpType) : MutableList<Warp>
    fun getWarpsByUUID(uuid: String) : List<Warp>
    fun getWarpByName(name: String, type: WarpType) : Warp?
    fun addWarp(warp: Warp, type: WarpType)
    fun deleteWarp(warp: Warp, type: WarpType)
    fun updateWarp(warp: Warp, type: WarpType)

}
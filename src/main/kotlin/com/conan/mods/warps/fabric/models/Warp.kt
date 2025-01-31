package com.conan.mods.warps.fabric.models

import com.conan.mods.warps.fabric.config.ConfigHandler.config
import com.conan.mods.warps.fabric.UltimateWarps.server
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.util.DataKeys
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PM.executeTaskOffMain
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

data class Warp(
    val name: String,
    val category: String = "chest",
    val ownerInfo: OwnerInfo? = null,
    val stats: WarpStats? = null,
    val coordinates: WarpCoordinates
) {

    fun returnAverageRating() : Int {
        val rates = this.stats?.rates?.mapNotNull { it.value }?.sumOf { it } ?: 0
        val rateSize = this.stats?.rates?.size?.coerceAtLeast(1) ?: 1
        return rates / rateSize
    }

    fun teleportPlayer(player: ServerPlayerEntity) {
        val dimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.tryParse(this.coordinates.dimension))
        val world = server!!.getWorld(dimension)

        if (player.uuidAsString != this.ownerInfo?.owner) {
            stats?.let {
                val updatedStats = this.stats.let { stats ->
                    stats.copy(visits = stats.visits + 1)
                }

                val updatedWarp = this.copy(stats = updatedStats)

                executeTaskOffMain {
                    updatedWarp.let { dbHandler!!.updateWarp(it, WarpType.PLAYER) }
                }
            }
        }

        player.teleport(
            world,
            this.coordinates.x,
            this.coordinates.y,
            this.coordinates.z,
            this.coordinates.yaw,
            this.coordinates.pitch,
        )
    }

    fun toItemStack(): ItemStack {
        val item = Registries.ITEM.get(Identifier.tryParse(this.category))
        val itemStack = ItemStack(item)

        itemStack.set(DataComponentTypes.CUSTOM_NAME, PM.returnStyledText(this.name.capitalize()))

        val compound = NbtCompound()

        compound.putString(DataKeys.WARP_NAME, this.name)
        compound.putDouble(DataKeys.WARP_X, this.coordinates.x)
        compound.putDouble(DataKeys.WARP_Y, this.coordinates.y)
        compound.putDouble(DataKeys.WARP_Z, this.coordinates.z)
        compound.putFloat(DataKeys.WARP_YAW, this.coordinates.yaw)
        compound.putFloat(DataKeys.WARP_PITCH, this.coordinates.pitch)
        compound.putString(DataKeys.WARP_DIMENSION, this.coordinates.dimension)

        ownerInfo?.let {
            compound.putString(DataKeys.WARP_OWNER, it.owner)
            compound.putString(DataKeys.WARP_OWNER_NAME, it.ownerName)
        }

        stats?.let {
            compound.putInt(DataKeys.WARP_VISITS, it.visits)
            val ratesCompound = NbtCompound()
            it.rates.forEach { (key, value) -> ratesCompound.putInt(key, value) }
            compound.put(DataKeys.WARP_RATES, ratesCompound)

            val modifiedLore = config.config.playerWarps.lore.map { line ->
                line.replace("%owner%", this.ownerInfo?.ownerName ?: "Unknown")
                    .replace("%visits%", "${this.stats.visits}")
                    .replace("%rates%", "${this.stats.rates.size}")
                    .replace("%average_rating%", "${this.returnAverageRating()}")
            }

            PM.setLore(itemStack, modifiedLore)
        }

        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound))

        return itemStack
    }
}

data class WarpStats(
    val visits: Int = 0,
    val rates: MutableMap<String, Int> = mutableMapOf()
)

data class OwnerInfo(
    val owner: String,
    val ownerName: String
)

data class WarpCoordinates(
    val dimension: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
)

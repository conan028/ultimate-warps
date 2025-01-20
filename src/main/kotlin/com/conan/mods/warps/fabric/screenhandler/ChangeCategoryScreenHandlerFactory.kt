package com.conan.mods.warps.fabric.screenhandler

import com.conan.mods.warps.fabric.config.ConfigHandler.menuConfig
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.models.Warp
import com.conan.mods.warps.fabric.util.PM
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity

class ChaneCategoryScreenHandlerFactory (
    player: ServerPlayerEntity,
    syncId: Int,
    private val warp: Warp,
    private val type: WarpType
) : GenericContainerScreenHandler(
    ScreenHandlerType.GENERIC_9X6,
    syncId,
    player.inventory,
    SimpleInventory(9 * 6),
    6
) {

    private var currentIndex = 0

    init {
        populateInventory(currentIndex)
    }

    private fun populateInventory(cIndex: Int) {
        for (i in 0 until inventory.size()) {
            inventory.setStack(i, menuConfig.categoryConfig.fillItem.returnWarpItem())
        }

        for (i in 45 until inventory.size()) {
            inventory.setStack(i, menuConfig.categoryConfig.barItem.returnWarpItem())
        }

        val registryList = (Registries.ITEM + Registries.BLOCK)
            .filterNot { it.asItem() == Items.AIR }
            .filterNot { it.asItem().defaultStack.registryEntry.key.get().value.toString() in menuConfig.categoryConfig.blackList }
        val stackList = registryList.mapNotNull { ItemStack(it.asItem()) }.drop(currentIndex)
        for ((index, stack) in stackList.withIndex()) {
            if (index < 45) {
                inventory.setStack(index, stack)
            }
        }

        menuConfig.categoryConfig.backItem.slot?.let {
            inventory.setStack(
                it,
                menuConfig.categoryConfig.backItem.returnWarpItem()
            )
        }

        val number = (stackList.size / 45) * 100
        if (number >= 100) {
            menuConfig.categoryConfig.nextPageItem.slot
                ?.let {
                    inventory.setStack(it, menuConfig.categoryConfig.nextPageItem.returnWarpItem())
                }
        }

        if (cIndex > 0) {
            menuConfig.categoryConfig.backPageItem.slot
                ?.let {
                    inventory.setStack(it, menuConfig.categoryConfig.backPageItem.returnWarpItem())
                }
        }

    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, playerEntity: PlayerEntity) {
        val clickedSlot = inventory.getStack(slotIndex)

        val player = playerEntity as? ServerPlayerEntity ?: return

        if (slotIndex in 0 until 45) {
            val newWarp = warp.copy(
                category = clickedSlot.registryEntry.key.get().value.toString()
            )

            dbHandler!!.updateWarp(newWarp, type)

            when (type) {
                WarpType.PLAYER -> openPlayerWarpScreenHandlerFactory(player)
                WarpType.SERVER -> openServerWarpScreenHandlerFactory(player)
            }
        }

        when (slotIndex) {
            menuConfig.categoryConfig.nextPageItem.slot -> {
                if (clickedSlot.isOf(menuConfig.categoryConfig.nextPageItem.returnWarpItem().item)) {
                    currentIndex += 45
                    populateInventory(currentIndex)
                }
            }
            menuConfig.categoryConfig.backPageItem.slot -> {
                if (clickedSlot.isOf(menuConfig.categoryConfig.backPageItem.returnWarpItem().item)) {
                    currentIndex -= 45
                    populateInventory(currentIndex)
                }
            }
            menuConfig.categoryConfig.backItem.slot -> openGenericWarpScreenHandlerFactory(player)
        }
    }

}

fun openChangeCategoryScreenHandlerFactory(
    player: ServerPlayerEntity,
    warp: Warp,
    type: WarpType
) {
    player.openHandledScreen(
        SimpleNamedScreenHandlerFactory(
            { syncId, _, _ ->
                ChaneCategoryScreenHandlerFactory(
                    player,
                    syncId,
                    warp,
                    type
                )
            },PM.returnStyledText(menuConfig.categoryConfig.title)
        )
    )
}
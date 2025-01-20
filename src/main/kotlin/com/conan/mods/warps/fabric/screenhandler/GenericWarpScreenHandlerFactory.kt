package com.conan.mods.warps.fabric.screenhandler

import com.conan.mods.warps.fabric.config.ConfigHandler.menuConfig
import com.conan.mods.warps.fabric.util.PM
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity

class GenericWarpScreenHandlerFactory (
    player: ServerPlayerEntity,
    syncId: Int,
) : GenericContainerScreenHandler(
    ScreenHandlerType.GENERIC_9X3,
    syncId,
    player.inventory,
    SimpleInventory(9 * 3),
    3
) {

    init {
        populateInventory()
    }

    private fun populateInventory() {
        for (i in 0 until inventory.size()) {
            inventory.setStack(i, menuConfig.genericWarpConfig.fillItem.returnWarpItem())
        }

        for (i in 18 until inventory.size()) {
            inventory.setStack(i, menuConfig.genericWarpConfig.barItem.returnWarpItem())
        }

        menuConfig.genericWarpConfig.serverWarpsItem.slot?.let {
            inventory.setStack(
                it,
                menuConfig.genericWarpConfig.serverWarpsItem.returnWarpItem()
            )
        }

        menuConfig.genericWarpConfig.playerWarpItems.slot?.let {
            inventory.setStack(
                it,
                menuConfig.genericWarpConfig.playerWarpItems.returnWarpItem()
            )
        }

        menuConfig.genericWarpConfig.closeItem.slot?.let {
            inventory.setStack(
                it,
                menuConfig.genericWarpConfig.closeItem.returnWarpItem()
            )
        }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, playerEntity: PlayerEntity) {
        val player = playerEntity as? ServerPlayerEntity ?: return

        when (slotIndex) {
            menuConfig.genericWarpConfig.serverWarpsItem.slot -> openServerWarpScreenHandlerFactory(player)
            menuConfig.genericWarpConfig.playerWarpItems.slot -> openPlayerWarpScreenHandlerFactory(player)
            menuConfig.genericWarpConfig.closeItem.slot -> player.closeHandledScreen()
            else -> return
        }
    }

}

fun openGenericWarpScreenHandlerFactory(
    player: ServerPlayerEntity
) {
    player.openHandledScreen(
        SimpleNamedScreenHandlerFactory(
            { syncId, _, _ ->
                GenericWarpScreenHandlerFactory(
                    player,
                    syncId
                )
            },PM.returnStyledText(menuConfig.genericWarpConfig.title)
        )
    )
}
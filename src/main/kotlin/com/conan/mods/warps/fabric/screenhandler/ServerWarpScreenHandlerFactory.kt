package com.conan.mods.warps.fabric.screenhandler

import com.conan.mods.warps.fabric.config.ConfigHandler.menuConfig
import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.permissions.UWPermissions
import com.conan.mods.warps.fabric.util.DataKeys
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PermUtil
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity

class ServerWarpScreenHandlerFactory (
    player: ServerPlayerEntity,
    syncId: Int,
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
            inventory.setStack(i, menuConfig.serverWarpConfig.fillItem.returnWarpItem())
        }

        for (i in 45 until inventory.size()) {
            inventory.setStack(i, menuConfig.serverWarpConfig.barItem.returnWarpItem())
        }

        val serverWarps = dbHandler!!.getWarps(WarpType.SERVER).drop(cIndex)
        for ((index, warp) in serverWarps.withIndex()) {
            val warpItem = warp.toItemStack()

            if (index < 45) {
                inventory.setStack(index, warpItem)
            }
        }

        menuConfig.serverWarpConfig.backItem.slot?.let {
            inventory.setStack(
                it,
                menuConfig.serverWarpConfig.backItem.returnWarpItem()
            )
        }

        val number = (serverWarps.size / 45) * 100
        if (number >= 100) {
            menuConfig.serverWarpConfig.nextPageItem.slot
                ?.let {
                    inventory.setStack(it, menuConfig.serverWarpConfig.nextPageItem.returnWarpItem())
                }
        }

        if (cIndex > 0) {
            menuConfig.serverWarpConfig.backPageItem.slot
                ?.let {
                    inventory.setStack(it, menuConfig.serverWarpConfig.backPageItem.returnWarpItem())
                }
        }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, playerEntity: PlayerEntity) {
        val clickedSlot = inventory.getStack(slotIndex)

        val player = playerEntity as? ServerPlayerEntity ?: return

        val data = clickedSlot.get(DataComponentTypes.CUSTOM_DATA)

        if (slotIndex in 0 until 45) {
            if (data != null && data.contains(DataKeys.WARP_NAME)) {
                val name = data.copyNbt().getString(DataKeys.WARP_NAME)
                val warp = dbHandler!!.getWarpByName(name, WarpType.SERVER)

                // Right Click
                if (button == 1) {
                    if (!player.hasPermissionLevel(2) || !PermUtil.commandRequiresPermission(player.commandSource, UWPermissions.WARP_ADMIN)) return

                    if (warp == null) {
                        PM.sendText(player, "<red>Something went wrong while fetching your warp.")
                        player.closeHandledScreen()
                        return
                    }
                    openChangeCategoryScreenHandlerFactory(player, warp, WarpType.SERVER)
                } else {
                    // Left Click
                    warp?.teleportPlayer(player)
                    PM.sendText(player, lang("ultimate_warps.player_warps.success.teleport")
                        .replace("%warp_name%", name)
                    )
                    player.closeHandledScreen()
                }
            }
        }

        when (slotIndex) {
            menuConfig.serverWarpConfig.nextPageItem.slot -> {
                if (clickedSlot.isOf(menuConfig.serverWarpConfig.nextPageItem.returnWarpItem().item)) {
                    currentIndex += 45
                    populateInventory(currentIndex)
                }
            }
            menuConfig.serverWarpConfig.backPageItem.slot -> {
                if (clickedSlot.isOf(menuConfig.serverWarpConfig.backPageItem.returnWarpItem().item)) {
                    currentIndex -= 45
                    populateInventory(currentIndex)
                }
            }
            menuConfig.serverWarpConfig.backItem.slot -> openGenericWarpScreenHandlerFactory(player)
        }
    }

}

fun openServerWarpScreenHandlerFactory(
    player: ServerPlayerEntity
) {
    player.openHandledScreen(
        SimpleNamedScreenHandlerFactory(
            { syncId, _, _ ->
                ServerWarpScreenHandlerFactory(
                    player,
                    syncId
                )
            },PM.returnStyledText(menuConfig.serverWarpConfig.title)
        )
    )
}
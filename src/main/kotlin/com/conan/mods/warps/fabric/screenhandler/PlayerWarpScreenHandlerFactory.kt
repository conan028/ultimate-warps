package com.conan.mods.warps.fabric.screenhandler

import com.conan.mods.warps.fabric.config.ConfigHandler.menuConfig
import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.FilterType
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.util.DataKeys
import com.conan.mods.warps.fabric.util.PM
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity

class PlayerWarpScreenHandlerFactory (
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
    private var filterIndex = 0
    private var activeFilter: FilterType = FilterType.entries[0]

    private fun nextFilter() {
        filterIndex = (filterIndex + 1) % FilterType.entries.size
        activeFilter = FilterType.entries[filterIndex]

        populateInventory(currentIndex)
    }

    init {
        populateInventory(currentIndex)
    }

    private fun populateInventory(cIndex: Int) {
        for (i in 0 until inventory.size()) {
            inventory.setStack(i, menuConfig.playerWarpConfig.fillItem.returnWarpItem())
        }

        for (i in 45 until inventory.size()) {
            inventory.setStack(i, menuConfig.playerWarpConfig.barItem.returnWarpItem())
        }

        val playerWarps = dbHandler!!.getWarps(WarpType.PLAYER).drop(cIndex)
        val modifiedWarps = when (activeFilter) {
            FilterType.VISITS -> playerWarps.sortedByDescending { it.stats?.visits }
            FilterType.AVERAGE -> playerWarps.sortedByDescending { it.stats?.rates?.size }
            FilterType.RATINGS -> playerWarps.sortedByDescending { it.returnAverageRating() }
            FilterType.NONE -> playerWarps
        }
        for ((index, warp) in modifiedWarps.withIndex()) {
            val warpItem = warp.toItemStack()

            if (index < 45) {
                inventory.setStack(index, warpItem)
            }
        }

        menuConfig.playerWarpConfig.backItem.slot?.let {
            inventory.setStack(
                it,
                menuConfig.playerWarpConfig.backItem.returnWarpItem()
            )
        }

        val number = (playerWarps.size / 45) * 100
        if (number >= 100) {
            menuConfig.playerWarpConfig.nextPageItem.slot
                ?.let {
                    inventory.setStack(it, menuConfig.playerWarpConfig.nextPageItem.returnWarpItem())
                }
        }

        if (cIndex > 0) {
            menuConfig.playerWarpConfig.backPageItem.slot
                ?.let {
                    inventory.setStack(it, menuConfig.playerWarpConfig.backPageItem.returnWarpItem())
                }
        }

        menuConfig.playerWarpConfig.filterItem.slot
            ?.let {
                val filterItem = menuConfig.playerWarpConfig.filterItem.returnWarpItem()
                val loreToSet = FilterType.entries.map { filter ->
                    if (filter == activeFilter) {
                        " <dark_gray>▪ <gray>${filter.name} <dark_gray>[<green>Active<dark_gray>]"
                    } else {
                        " <dark_gray>▪ <gray>${filter.name}"
                    }
                }
                PM.setLore(filterItem, loreToSet)
                inventory.setStack(it, filterItem)
            }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, playerEntity: PlayerEntity) {
        val clickedSlot = inventory.getStack(slotIndex)

        val player = playerEntity as? ServerPlayerEntity ?: return

        val data = clickedSlot.get(DataComponentTypes.CUSTOM_DATA)

        if (slotIndex in 0 until 45) {
            if (data != null && data.contains(DataKeys.WARP_NAME)) {
                val name = data.copyNbt().getString(DataKeys.WARP_NAME)
                val warp = dbHandler!!.getWarpByName(name, WarpType.PLAYER)

                // Right Click
                if (button == 1) {
                    if (warp?.ownerInfo?.owner != player.uuidAsString) return

                    if (warp == null) {
                        PM.sendText(player, "<red>Something went wrong while fetching your warp.")
                        player.closeHandledScreen()
                        return
                    }
                    openChangeCategoryScreenHandlerFactory(player, warp, WarpType.PLAYER)
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
            menuConfig.playerWarpConfig.nextPageItem.slot -> {
                if (clickedSlot.isOf(menuConfig.playerWarpConfig.nextPageItem.returnWarpItem().item)) {
                    currentIndex += 45
                    populateInventory(currentIndex)
                }
            }
            menuConfig.playerWarpConfig.backPageItem.slot -> {
                if (clickedSlot.isOf(menuConfig.playerWarpConfig.backPageItem.returnWarpItem().item)) {
                    currentIndex -= 45
                    populateInventory(currentIndex)
                }
            }
            menuConfig.playerWarpConfig.backItem.slot -> openGenericWarpScreenHandlerFactory(player)
            menuConfig.playerWarpConfig.filterItem.slot -> nextFilter()
        }
    }

}

fun openPlayerWarpScreenHandlerFactory(
    player: ServerPlayerEntity
) {
    player.openHandledScreen(
        SimpleNamedScreenHandlerFactory(
            { syncId, _, _ ->
                PlayerWarpScreenHandlerFactory(
                    player,
                    syncId
                )
            },PM.returnStyledText(menuConfig.playerWarpConfig.title)
        )
    )
}
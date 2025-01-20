package com.conan.mods.warps.fabric.commands.player

import com.conan.mods.warps.fabric.config.ConfigHandler.config
import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.economy.EconomyInitializer.economy
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.permissions.UWPermissions
import com.conan.mods.warps.fabric.suggestions.WarpSuggestions
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PermUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object DeletePlayerWarpCommand {
    fun register(parent: LiteralArgumentBuilder<ServerCommandSource>) {
        val command = literal("delete")
            .requires { PermUtil.commandRequiresPermission(it, UWPermissions.DELETE_WARP_COMMAND) }
            .then(argument("name", StringArgumentType.word())
                .suggests(WarpSuggestions(null))
                .executes(::execute))

        parent.then(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val warpName = StringArgumentType.getString(context, "name").lowercase()

        // Check owner
        val playerWarps = dbHandler!!.getWarpsByUUID(player.uuidAsString)
        val warp = playerWarps.find { it.name == warpName }
        if (warp == null) {
            PM.sendText(player, lang("ultimate_warps.errors.no_warp_found")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        // Return costs
        if (config.config.economy.isEnabled) {
            if (config.config.economy.returnMoneyOnDeletion) {
                val warpCosts = config.config.economy.warpCost
                economy.deposit(player.uuid, warpCosts)
            }
        }

        dbHandler!!.deleteWarp(warp, WarpType.PLAYER)
        PM.sendText(player, lang("ultimate_warps.player_warps.success.delete")
            .replace("%warp_name%", warpName)
        )

        return Command.SINGLE_SUCCESS
    }

}
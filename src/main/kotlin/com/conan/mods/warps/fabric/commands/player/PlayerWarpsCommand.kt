package com.conan.mods.warps.fabric.commands.player

import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.permissions.UWPermissions
import com.conan.mods.warps.fabric.suggestions.WarpSuggestions
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PermUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object PlayerWarpsCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val pWarpCommand = literal("pwarp")
            .requires { PermUtil.commandRequiresPermission(it, UWPermissions.WARP_COMMAND) }
            .then(argument("name", StringArgumentType.greedyString())
                .suggests(WarpSuggestions(WarpType.PLAYER))
                .executes(::executePWarp))

        // Player warps
        CreatePlayerWarpCommand.register(pWarpCommand)
        DeletePlayerWarpCommand.register(pWarpCommand)
        RatePlayerWarpCommand.register(pWarpCommand)

        dispatcher.register(pWarpCommand)

    }

    private fun executePWarp(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val warpName = StringArgumentType.getString(context, "name").lowercase()
        val warp = dbHandler!!.getWarps(WarpType.PLAYER).firstOrNull { it.name == warpName }
        if (warp == null) {
            PM.sendText(player, lang("ultimate_warps.errors.no_warp_found")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        warp.teleportPlayer(player)
        PM.sendText(player, lang("ultimate_warps.player_warps.success.teleport")
            .replace("%warp_name%", warpName)
        )

        return Command.SINGLE_SUCCESS
    }

}
package com.conan.mods.warps.fabric.commands.player

import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.permissions.UWPermissions
import com.conan.mods.warps.fabric.suggestions.WarpSuggestions
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PermUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object RatePlayerWarpCommand {
    fun register(parent: LiteralArgumentBuilder<ServerCommandSource>) {
        val command = literal("rate")
            .requires { PermUtil.commandRequiresPermission(it, UWPermissions.RATE_WARP_COMMAND) }
            .then(argument("name", StringArgumentType.word())
                .suggests(WarpSuggestions(WarpType.PLAYER))
                .then(argument("rate", IntegerArgumentType.integer(1, 5))
                    .executes(::execute)))

        parent.then(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val warpName = StringArgumentType.getString(context, "name").lowercase()
        val rate = IntegerArgumentType.getInteger(context, "rate")

        val warp = dbHandler!!.getWarps(WarpType.PLAYER).find { it.name == warpName}
        if (warp == null) {
            PM.sendText(player, lang("ultimate_warps.errors.no_warp_found")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        if (warp.ownerInfo?.owner == player.uuidAsString) {
            PM.sendText(player, lang("ultimate_warps.player_warps.errors.rate_own_warp")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        val rates = warp.stats?.rates
        if (rates?.containsKey(player.uuidAsString) == true) {
            PM.sendText(player, lang("ultimate_warps.player_warps.errors.already_rated_warp")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        rates?.putIfAbsent(player.uuidAsString, rate)
        val updatedWarp = warp.copy(
            stats = warp.stats
        )

        dbHandler!!.updateWarp(updatedWarp, WarpType.PLAYER)

        PM.sendText(player, lang("ultimate_warps.player_warps.success.rate")
            .replace("%warp_name%", warpName)
            .replace("%rating%", "$rate")
        )

        return Command.SINGLE_SUCCESS
    }

}
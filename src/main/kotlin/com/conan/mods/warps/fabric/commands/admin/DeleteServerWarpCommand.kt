package com.conan.mods.warps.fabric.commands.admin

import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.suggestions.WarpSuggestions
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PM.executeTaskOffMain
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object DeleteServerWarpCommand {
    fun register(parent: LiteralArgumentBuilder<ServerCommandSource>) {
        val deleteCommand = literal("delete")
            .then(argument("name", StringArgumentType.greedyString())
                .suggests(WarpSuggestions(WarpType.SERVER))
                .executes(::execute))

        parent.then(deleteCommand)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val warpName = StringArgumentType.getString(context, "name").lowercase()

        val warp = dbHandler!!.getWarps(WarpType.SERVER).find { it.name == warpName }
        if (warp == null) {
            PM.sendText(player, lang("ultimate_warps.errors.no_warp_found")
                .replace("%warp_name%", warpName)
            )
            return 0
        }


        executeTaskOffMain {
            dbHandler!!.deleteWarp(warp, WarpType.SERVER)
        }

        PM.sendText(player, lang("ultimate_warps.admin.server_warps.delete")
            .replace("%warp_name%", warpName)
        )
        return Command.SINGLE_SUCCESS
    }

}
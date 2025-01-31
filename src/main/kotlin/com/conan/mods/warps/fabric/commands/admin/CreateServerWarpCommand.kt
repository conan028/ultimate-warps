package com.conan.mods.warps.fabric.commands.admin

import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.models.Warp
import com.conan.mods.warps.fabric.models.WarpCoordinates
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PM.executeTaskOffMain
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object CreateServerWarpCommand {
    fun register(parent: LiteralArgumentBuilder<ServerCommandSource>) {
        val createCommand = literal("create")
            .then(argument("name", StringArgumentType.greedyString())
                .executes(::execute))

        parent.then(createCommand)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val warpName = StringArgumentType.getString(context, "name").lowercase()

        val warps = dbHandler!!.getWarps(WarpType.SERVER).filter { it.name == warpName }
        if (warps.isNotEmpty()) {
            PM.sendText(player, lang("ultimate_warps.admin.server_warps.already_exists")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        val warp = Warp (
            warpName,
            "chest",
            null,
            null,
            WarpCoordinates(
                player.world.registryKey.value.path,
                player.x,
                player.y,
                player.z,
                player.yaw,
                player.pitch
            )
        )

        executeTaskOffMain {
            dbHandler!!.addWarp(warp, WarpType.SERVER)
        }
        PM.sendText(player, lang("ultimate_warps.admin.server_warps.add")
            .replace("%warp_name%", warpName)
        )

        return Command.SINGLE_SUCCESS
    }
}
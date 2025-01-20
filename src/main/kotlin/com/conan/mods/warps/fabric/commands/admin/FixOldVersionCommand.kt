package com.conan.mods.warps.fabric.commands.admin

import com.conan.mods.warps.fabric.datahandler.JsonDBHandler
import com.conan.mods.warps.fabric.util.PM
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object FixOldVersionCommand {
    fun register(parent: LiteralArgumentBuilder<ServerCommandSource>) {
        val fixOldDataCommand = literal("fixData")
            .requires { it.hasPermissionLevel(2) }
            .executes(::execute)

        parent.then(fixOldDataCommand)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val jsonHandler = JsonDBHandler()
        jsonHandler.fixOldData()

        PM.sendText(player, "Fixed old data, please look in the config.")

        return Command.SINGLE_SUCCESS
    }

}
data class OldWarp (
    val warpId: String,
    var playerUUID: String,
    val playerName: String,
    var category: String,
    val name: String,
    val dimension: String,
    val coords: Long,
    val server: String?
)
package com.conan.mods.warps.fabric.commands.admin

import com.conan.mods.warps.fabric.config.ConfigHandler.menuConfig
import com.conan.mods.warps.fabric.config.ConfigHandler.config
import com.conan.mods.warps.fabric.config.ConfigHandler.langConfig
import com.conan.mods.warps.fabric.util.PM
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object ReloadCommand {

    fun register(parent: LiteralArgumentBuilder<ServerCommandSource>) {
        val reloadCommand = literal("reload")
            .executes(::execute)

        parent.then(reloadCommand)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        menuConfig.reload()
        config.reload()
        langConfig.reload()

        PM.sendText(player, "<green>Successfully reloaded config.")

        return Command.SINGLE_SUCCESS
    }
}
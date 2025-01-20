package com.conan.mods.warps.fabric.commands.admin

import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.permissions.UWPermissions
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PermUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object AdminCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = literal("wadmin")
            .requires { it.hasPermissionLevel(2) || PermUtil.commandRequiresPermission(it, UWPermissions.WARP_ADMIN) }
            .executes(::execute)

        // Server Commands
        val serverCommands = literal("server")
        DeleteServerWarpCommand.register(serverCommands)
        CreateServerWarpCommand.register(serverCommands)
        command.then(serverCommands)

        // Player Commands
        val playerCommands = literal("player")
        AdminDeleteWarpCommand.register(playerCommands)
        command.then(playerCommands)

        // Reload Command
        ReloadCommand.register(command)

        // Fix Old Data Command
        FixOldVersionCommand.register(command)

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val messages = listOf(
            "ultimate_warps.commands.admin.header",
            "ultimate_warps.commands.admin.break",
            "ultimate_warps.commands.admin.server",
            "ultimate_warps.commands.admin.server.options.create",
            "ultimate_warps.commands.admin.server.options.delete",
            "ultimate_warps.commands.admin.player",
            "ultimate_warps.commands.admin.player.options.delete",
            "ultimate_warps.commands.admin.reload"
        )

        messages.forEach {
            PM.sendText(player, lang(it))
        }

        return Command.SINGLE_SUCCESS
    }
}
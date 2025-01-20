package com.conan.mods.warps.fabric.commands.player

import com.conan.mods.warps.fabric.permissions.UWPermissions
import com.conan.mods.warps.fabric.screenhandler.openGenericWarpScreenHandlerFactory
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PermUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object WarpsCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val warpsCommand = literal("warps")
            .requires { PermUtil.commandRequiresPermission(it, UWPermissions.WARP_COMMAND) }
            .executes(::executeWarps)

        dispatcher.register(warpsCommand)
    }

    private fun executeWarps(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        openGenericWarpScreenHandlerFactory(player)
        return Command.SINGLE_SUCCESS
    }

}
package com.conan.mods.warps.fabric.commands.player

import com.conan.mods.warps.fabric.config.ConfigHandler.config
import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.economy.EconomyInitializer.economy
import com.conan.mods.warps.fabric.enums.WarpType
import com.conan.mods.warps.fabric.models.*
import com.conan.mods.warps.fabric.permissions.UWPermissions
import com.conan.mods.warps.fabric.util.PM
import com.conan.mods.warps.fabric.util.PermUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object CreatePlayerWarpCommand {
    fun register(parent: LiteralArgumentBuilder<ServerCommandSource>) {
        val command = literal("create")
            .requires { PermUtil.commandRequiresPermission(it, UWPermissions.CREATE_WARP_COMMAND) }
            .then(argument("name", StringArgumentType.word())
            .executes(::execute))

        parent.then(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player = context.source.playerOrThrow ?: return 0.also {
            context.source.sendError(PM.returnStyledText("This command can only be run by players."))
        }

        val warpName = StringArgumentType.getString(context, "name").lowercase()

        // Blacklist
        if (warpName in config.config.playerWarps.blackList) {
            PM.sendText(player, lang("ultimate_warps.player_warps.errors.blacklisted_name")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        // Warp check
        val warps = dbHandler!!.getWarps(WarpType.PLAYER).filter { it.name == warpName }
        if (warps.isNotEmpty()) {
            PM.sendText(player, lang("ultimate_warps.player_warps.errors.already_exists")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        // Max warps
        val playerWarps = dbHandler!!.getWarpsByUUID(player.uuidAsString)
        if (playerWarps.size >= config.config.playerWarps.maxWarps) {
            PM.sendText(player, lang("ultimate_warps.player_warps.errors.reached_max_warps")
                .replace("%warp_name%", warpName)
            )
            return 0
        }

        // Economy
        if (config.config.economy.isEnabled) {
            val warpCosts = config.config.economy.warpCost
            val balance = economy.getBalance(player.uuid)
            if (balance < warpCosts) {
                PM.sendText(player, lang("ultimate_warps.player_warps.errors.no_balance")
                    .replace("%warp_name%", warpName)
                )
                return 0
            }
        }

        // Name length
        val maxNameLength = config.config.playerWarps.maxLength
        if (warpName.length > maxNameLength) {
            PM.sendText(player, lang("ultimate_warps.player_warps.errors.name_length")
                .replace("%warp_name%", warpName)
                .replace("%%warp_name_length%%", "$maxNameLength")
            )
            return 0
        }

        // Take money
        if (config.config.economy.isEnabled) {
            val warpCosts = config.config.economy.warpCost
            economy.withdraw(player.uuid, warpCosts)
        }

        val warp = Warp (
            warpName,
            "chest",
            OwnerInfo(
                player.uuidAsString,
                player.name.string,
            ),
            WarpStats(),
            WarpCoordinates(
                player.world.registryKey.value.path,
                player.x,
                player.y,
                player.z,
                player.yaw,
                player.pitch
            )
        )

        dbHandler!!.addWarp(warp, WarpType.PLAYER)
        PM.sendText(player, lang("ultimate_warps.player_warps.success.add")
            .replace("%warp_name%", warpName)
        )

        return Command.SINGLE_SUCCESS
    }
}
package com.conan.mods.warps.fabric

import com.conan.mods.warps.fabric.commands.admin.AdminCommand
import com.conan.mods.warps.fabric.commands.player.PlayerWarpsCommand
import com.conan.mods.warps.fabric.commands.player.WarpCommand
import com.conan.mods.warps.fabric.commands.player.WarpsCommand
import com.conan.mods.warps.fabric.config.ConfigHandler
import com.conan.mods.warps.fabric.config.baseconfig.LangConfig
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object UltimateWarps : ModInitializer {

    val LOGGER: Logger = LogManager.getLogger()

    var server: MinecraftServer? = null

    override fun onInitialize() {

        LOGGER.info("[Ultimate Warps] - Enabled")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            WarpCommand.register(dispatcher)
            WarpsCommand.register(dispatcher)

            PlayerWarpsCommand.register(dispatcher)

            AdminCommand.register(dispatcher)
        }

        ServerLifecycleEvents.SERVER_STARTING.register {
            LangConfig
            ConfigHandler
            server = it
        }

    }

}
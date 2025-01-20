package com.conan.mods.warps.fabric.suggestions

import com.conan.mods.warps.fabric.datahandler.DatabaseHandlerSingleton.dbHandler
import com.conan.mods.warps.fabric.enums.WarpType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

class WarpSuggestions(
    private val type: WarpType?
) : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>?,
        builder: SuggestionsBuilder?,
    ): CompletableFuture<Suggestions> {
        val player = context?.source?.playerOrThrow

        val warpList = when (type) {
            WarpType.SERVER -> dbHandler!!.getWarps(type)
            WarpType.PLAYER -> dbHandler!!.getWarps(type)
            null -> player?.uuidAsString?.let { dbHandler!!.getWarpsByUUID(it) }
        }

        warpList?.forEach {
            builder?.suggest(it.name)
        }
        return builder!!.buildFuture()
    }
}
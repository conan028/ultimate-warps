package com.conan.mods.warps.fabric.util

import com.conan.mods.warps.fabric.UltimateWarps.server
import com.conan.mods.warps.fabric.config.baseconfig.LangConfig.lang
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

object PM {

    private fun parseMessageWithStyles(text: String, placeholder: String): Component {
        var mm = MiniMessage.miniMessage();
        return mm.deserialize(text.replace("{placeholder}", placeholder)).decoration(TextDecoration.ITALIC, false)
    }

    fun returnStyledText(text: String): Text {
        val component = parseMessageWithStyles(text, "placeholder")
        val gson = GsonComponentSerializer.gson()
        val json = gson.serialize(component)
        return Text.Serialization.fromJson(json, server?.registryManager) as Text
    }

    fun setLore(itemStack: ItemStack, lore: List<String>) {
        var itemLore = itemStack.components.get(DataComponentTypes.LORE)

        if (itemLore == null) {
            itemLore = LoreComponent(emptyList())
        }

        val allLoreLines: MutableList<Text> = itemLore.lines.toMutableList()

        for (line in lore) {
            allLoreLines.add(returnStyledText(line))
        }

        itemLore = LoreComponent(allLoreLines)

        itemStack.set(DataComponentTypes.LORE, itemLore)
    }

    fun sendText(player: PlayerEntity, text: String) {
        val component = returnStyledText(text.replace("%prefix%", lang("ultimate_warps.prefix")))
        player.sendMessage(component, false)
    }

}
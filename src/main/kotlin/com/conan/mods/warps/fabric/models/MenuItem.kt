package com.conan.mods.warps.fabric.models

import com.conan.mods.warps.fabric.util.PM
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.StringNbtReader
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

data class MenuItem (
    val name: String? = null,
    val material: String,
    val slot: Int?,
    val count: Int = 1,
    val nbt: String? = null,
) {
    fun returnWarpItem() : ItemStack {
        val item = Registries.ITEM.get(Identifier.tryParse(this.material))
        val itemStack = ItemStack(item)

        itemStack.count = this.count

        if (this.nbt != null) {
            val compound = StringNbtReader.parse(this.nbt)
            itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound))
        }

        if (this.name != null) {
            itemStack.set(DataComponentTypes.CUSTOM_NAME, PM.returnStyledText(this.name.capitalize()))
        }

        return itemStack
    }

}
package com.conan.mods.warps.fabric.economy

import com.conan.mods.warps.fabric.UltimateWarps.LOGGER

object EconomyInitializer {

    lateinit var economy: EconomyInterface

    init {
        reload()
    }

    private fun reload() {
        try {
            economy = ImpactorEconomy()
        } catch (e: NoClassDefFoundError) {
            LOGGER.info("Failed to load Economy API")
            throw e
        }
    }
}
package com.conan.mods.warps.fabric.config.baseconfig

import java.io.File

object ReadMe {

    private val readmeFile = File("config/UltimateWarps/readme.md")

    private val readme = """
# Ultimate Warps

A highly customizable server-side mod for creating both server and player warps, which supports both JSON and MongoDB as data storage.

## Overview
This server-sided mod is meant to be used by anyone who wishes to enhance either their own or community's in-game experience. In this ReadME.md it will teach you all the neccesarry commands and hopefully answer any questions you might have. If you are left with any questions or suggestions, please feel free to join my [Discord](https://discord.com/invite/NBxuBvju3p) server.


## Commands

The commands are split up into two parts. The first section will include all operators commands that require [Permission Level 2](https://minecraft.fandom.com/wiki/Permission_level), and the second part is meant for all players to use.

# Commands Overview

## Administrator Commands  
_Requires Permission Level 2 or `ultimate_warps.admin` as a LuckPerm permission._  
`/wadmin` » Displays all available administrator commands.  
- `server <subcommand>` » Manages server-warps with the following subcommands:  
  - `create <name>` » Creates a new server-warp.  
  - `delete <name>` » Deletes an existing server-warp.  
- `player <subcommand>` » Manages player-warps as an administrator:  
  - `delete <name>` » Deletes a player-warp (use with caution!).  
- `reload` » Reloads all configuration files.  

---

## General Commands  
_Requires `ultimate_warps.user` as a LuckPerm permission._  
- `/warp <name>` » Teleports the player to an existing server-warp.  
- `/warps` » Opens a GUI to search both server and player warps.  
- `/pwarp <subcommand> <name>` » Manages player-warps with the following subcommands:  
  - `<name>` » Teleports the player to an existing player-warp.  
  - `rate <name>` » Rates an existing player-warp _(requires `ultimate_warps.rate`)_.  
  - `create <name>` » Creates a new player-warp _(requires `ultimate_warps.create`)_.  
  - `delete <name>` » Deletes an existing player-warp _(requires `ultimate_warps.delete`)_.  
## Configuration

The configuration files mainly consist out of 4 parts:
 - Base
 - Datastore
 - Language
 - GUIs

---

## Base Config
This will take you through each configuration property, and explain its purpose.

```json
{
  "version": 1.1,
  "lang": "en_us",
  "playerWarps": {
    "maxWarps": 3,
    "maxLength": 10,
    "lore": [
      "<dark_gray>Owner: <white>%owner%",
      "<dark_gray>Visits: <white>%visits%",
      " ",
      "<dark_gray>Rates: <white>%rates%",
      "<dark_gray>Average Rate: <white>%average_rating%"
    ],
    "blackList": []
  },
  "economy": {
    "isEnabled": false,
    "warpCost": 1000.0,
    "returnMoneyOnDeletion": true
  }
}
```

### General
 - `version` » The config schema version.
 - `lang` » The default language for the mod.

### Player Warps
- `maxWarps` » The default amount of warps a player can make.
- `maxLength` » The maximum length a warp name can have.
- `lore` » Sets the lore for the player-warps GUI.
    - Placeholders
        - `%owner%` » Returns the warp owner.
        - `%visits%` » Returns the amount of times a warp has been visited.
        - `%rates%` » Returns the amount of rates a warp has been given.
        - `%average_rating%` » Returns the average rates of a warp.
- `blackList` » Contains a list of prohibited player-warp names.

### Economy
- `isEnabled` » Either turns on/off the economy _(Impactor required when set to true)_.
- `warpCost` » The costs it takes to create warp.
- `returnMoneyOnDeletion` » Returns the warp cost to a player upon deleting a player owned warp.

---

## Datastore Config
Deciding where you will save your data is important when setting up this mod. This mod both supports MongoDB and JSON as data storage. Ultimately the decision is up to you as the reader, but here are a few tips to decide.

```json
{
  "dataStore": "JSON",
  "mongoDB": {
    "connectionString": "mongodb://localhost:27017/",
    "database": "warps",
    "playerWarpCollection": "player_warp_collection",
    "serverWarpCollection": "server_warp_collection"
  }
}
```

### MongoDB
 - Perfect for larger networks.
 - Performant.
 - Easier to manage data for those with MongoDB experience.

### JSON
 - Simple and light weight to use.
 - Flexible format.
 - Perfect for smaller communities.

---

## Language Config
The language file is located in the _lang_ folder. To create a new language file, simply change the config property in the **Base config** to your wished language format. Once done, you can restart the server and it will generate a new language file for you to use, and modify.

Example of changing your language file to Spanish in the _config.json_ file.

_old_:
```json
  "lang": "en_us"
```

_new_:
```json
  "lang": "es_es"
```

---

## GUIs Config
The customizable GUIs are a key element to this mod.

Example of an item in JSON:
```json
  "closeItem": {
    "name": "<red>Close",
    "material": "barrier",
    "slot": 22,
    "nbt": "[]"
    "count": 1
  }
```

### Menu Item
- `name` » The display name of an item _(for name formatting, please refer to the **Additional Sources** down below.)_.
- `material` » The material the item is made of.
- `slot` » The slot the item will be placed in _(note that not all items have this e.g. fillItem)_.
- `nbt` » Sets the custom data of an item _(note that after Minecraft 1.20.5 the way NBT works has changed, please refer to the **Additional Sources** down below)_.
- `count` » Sets the amount of the item.

---

There are currently four different GUIs players can navigate through.

 - General navigation
 - Server warp navigation
    - Categories
 - Player warp navigation
    - Categories

---

### General Navigation
Gives the option to search through either the server- or player warps.

---

### Server-Warp Navigation
Here you can find all server-warps that have been made by server administrators.

**NOTE:** Server administrators can right-click a warp item to change the material in-game, this will open the *Category Navigation*.


---

### Player-Warp Navigation

**NOTE:** players can right-click a warp item that belongs to themselves to change the material in-game, this will open the *Category Navigation*.

---

### Category Navigation
Opens up a GUI that display all items and blocks in-game for users to choose their desired material block.

**NOTE:** Server administrators can add items and or blocks to the _blacklist_ in the _category_menu_config.json_, this causes for server blocks and items to not show up.

---

## Dependencies

 - [LuckPerms](https://luckperms.net/) (optional).
 - [Impactor](https://modrinth.com/mod/impactor) (optional).
## Support

For support, join my [Discord](https://discord.gg/NBxuBvju3p) server.


## Authors

- [@conan](https://github.com/conan028)


## License

[MIT](https://choosealicense.com/licenses/mit/)


## Additional Sources
 - [NBT format](https://minecraft.fandom.com/wiki/NBT_format)
 - [MiniMessage](https://docs.advntr.dev/minimessage/format)
 - [MiniMessageViewer](https://webui.advntr.dev/)
""".trimMargin()

    init {
        if (!readmeFile.exists()) {
            readmeFile.parentFile.mkdirs()
            readmeFile.createNewFile()
            readmeFile.writeText(readme)
        }
    }

}
/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.config;

import net.hazeservers.sg.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class Settings {

    //YamlConfiguration for Settings variables
    private static YamlConfiguration config;

    public static void setConfig(YamlConfiguration config) {
        Settings.config = config;
    }

    //General Plugin variables
    public enum General {

        LOBBY_SERVER,
        SPAWN_WORLD,

        STARTING_PLAYERS,
        DEATHMATCH_PLAYERS,

        KILL_TOKENS,
        WIN_TOKENS,

        ITEM_BREAK_WHITELIST,
        ITEM_PLACE_WHITELIST,

        CHEST_LOADOUT,

        RESTART_COMMANDS;

        public boolean toBoolean() {
            return config.getBoolean(name());
        }

        public int toInt() {
            return config.getInt(name());
        }

        public List<String> toList() {
            return config.getStringList(name());
        }

        public String toString() {
            return config.getString(name());
        }

        public World toWorld() {
            return Bukkit.getWorld(config.getString(name()));
        }
    }

    //GameTimer countdown variables
    public enum Countdown {

        //STARTING/FORCE_STARTING countdowns
        START_PREGAME,
        //PREGAME countdowns
        START_GAME,
        //INGAME countdowns
        EQUIP_KITS,
        RESET_CHESTS,
        END_GAME,
        //DEATH_MATCH countdowns
        ENABLE_PVP,
        END_DEATHMATCH,
        //FINISHING countdowns
        RESET_SERVER;

        public int toInt() {
            return config.getInt("countdown." + name());
        }
    }

    //Inventory Menu variables
    public enum Menus {

        KIT_MENU, SPECTATOR_MENU;

        public int getSize() {
            return config.getInt("menus." + name() + ".SIZE");
        }

        public String getTitle() {
            return StringUtil.color(config.getString("menus." + name() + ".TITLE"));
        }
    }

    //MySQL Server variables
    public enum MySQL {

        HOST, USERNAME, PASSWORD, DATABASE, PORT;

        public String toString() {
            return config.getString("mysql." + name());
        }

        public int toInt() {
            return config.getInt("mysql." + name());
        }
    }
}

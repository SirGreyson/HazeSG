/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.config;

import net.hazeservers.sg.game.GameState;
import net.hazeservers.sg.util.StringUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class Lang {

    //YamlConfiguration for Lang values
    private static YamlConfiguration config;

    public static void setConfig(YamlConfiguration config) {
        Lang.config = config;
    }

    //Configured Private (Player) Messages
    public enum Messages {

        PREFIX,

        CANNOT_JOIN,

        KIT_NO_PERM,
        KIT_SELECTED,
        KIT_EQUIPPED,

        TOKENS_GAINED,

        SPECTATOR_MODE;

        public String toString() {
            return config.getString("messages." + name());
        }
    }

    //Configured Public (Server) Messages
    public enum Broadcasts {

        PREFIX,

        JOIN_MESSAGE,
        QUIT_MESSAGE,

        DEATH_MESSAGE,
        PVP_DEATH_MESSAGE,
        REMAINING_PLAYERS,

        STARTING,
        START_CANCELLED,

        PREGAME_STARTED,
        GAME_STARTING,

        GAME_STARTED,
        CHESTS_RESTOCKED,
        DEATHMATCH_STARTING,

        DEATHMATCH_STARTED,
        PVP_ENABLED,
        DEATHMATCH_ENDING,

        GAME_FINISHED,
        SERVER_RESTARTING;

        public String toString() {
            return config.getString("broadcasts." + name());
        }
    }

    //Configured Server-List (MOTD) Messages
    public enum MOTD {

        WAITING, STARTING, INGAME, DEATH_MATCH, FINISHING, RESETTING;

        public String toString() {
            return StringUtil.color(config.getString("motd." + name()));
        }

        public static MOTD fromGameState(GameState gameState) {
            switch (gameState) {
                case FORCE_STARTING:
                    return STARTING;
                case PREGAME:
                    return INGAME;
                default:
                    return valueOf(gameState.name());
            }
        }
    }

    //Configured Scoreboard Messages
    public enum Scoreboard {

        TITLE, FORMAT;

        public String toString() {
            return StringUtil.color(config.getString("scoreboard." + name()));
        }

        public List<String> toList() {
            return config.getStringList("scoreboard." + name());
        }
    }
}

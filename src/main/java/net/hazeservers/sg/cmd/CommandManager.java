/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.cmd;

import net.hazeservers.sg.SurvivalGames;

public class CommandManager {

    private SurvivalGames plugin;

    public CommandManager(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommand("arena").setExecutor(new ArenaCommand(plugin));
        plugin.getCommand("hazesg").setExecutor(new SGCommand(plugin));
        plugin.getCommand("kits").setExecutor(new KitCommand(plugin));
    }
}

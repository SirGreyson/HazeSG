/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.cmd;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.game.Game;
import net.hazeservers.sg.game.GameState;
import net.hazeservers.sg.menu.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    private SurvivalGames plugin;

    public KitCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Messaging.send(sender, "Error! This command cannot be run from the Console!");
        } else if (Game.getInstance().getState() != GameState.PREGAME) {
            Messaging.send(sender, "&cYou cannot use this command right now!");
        } else {
            MenuManager.KIT_MENU.openMenu((Player) sender);
        }
        return true;
    }
}

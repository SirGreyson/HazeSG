/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.cmd;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SGCommand implements CommandExecutor {

    private SurvivalGames plugin;

    public SGCommand(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Messaging.send(sender, "&cInvalid arguments! Use &6/sg help &cfor a list of arguments");

            //SG Help Command
        } else if (args[0].equalsIgnoreCase("help")) {
            Messaging.send(sender, "&eUse &6/sg &e+ one of the following: " +
                    "\n&6stats - &eview your stats" +
                    "\n&6blood - &etoggle PvP blood effect");

            //SG Stats Command
        } else if (args[0].equalsIgnoreCase("stats")) {
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    Messaging.send(sender, "This command cannot be run from the Console!");
                } else {
                    PlayerData data = plugin.getDataManager().getData((Player) sender);
                    StringBuilder sb = new StringBuilder("&bStats for Player: &6" + sender.getName());
                    sb.append("\n&bKills: &6" + data.getKills());
                    sb.append("\n&bDeaths: &6" + data.getDeaths());
                    sb.append("\n&bWins: &6" + data.getWins());
                    Messaging.send(sender, sb.toString());
                }
            } else {
                if (!sender.isOp() && !sender.hasPermission("hazesg.admin")) {
                    Messaging.send(sender, "&cYou do not have permission to use this command!");
                } else {
                    String data = plugin.getDataManager().getData(args[1]);
                    Messaging.send(sender, data == null ?
                            "&cError! There are no stats saved for a Player with that name!" : data);
                }
            }

            //SG Blood Command
        } else if (args[0].equalsIgnoreCase("blood")) {
            if (!(sender instanceof Player)) {
                Messaging.send(sender, "This command cannot be run from the Console!");
            } else {
                PlayerData data = plugin.getDataManager().getData((Player) sender);
                data.toggleBlood();
                Messaging.send(sender, "&eBlood effects are now " + (data.canSeeBlood() ? "&aENABLED" : "&cDISABLED"));
            }

            //Invalid Sub-Command
        } else {
            Messaging.send(sender, "&cInvalid arguments! Use &6/sg help &cfor a list of arguments");
        }

        return true;
    }
}

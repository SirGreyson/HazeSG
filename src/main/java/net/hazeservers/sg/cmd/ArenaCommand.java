/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.cmd;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.arena.Arena;
import net.hazeservers.sg.arena.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    private ArenaManager arenaManager;

    public ArenaCommand(SurvivalGames plugin) {
        this.arenaManager = plugin.getArenaManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Messaging.send(sender, "&cInvalid arguments! Use &6/arena help &cfor a list of arguments");

            //Arena Command Help
        } else if (args[0].equalsIgnoreCase("help")) {
            Messaging.send(sender, "&eUse &6/arena &e+ one of the following: " +
                    "\n&6create <ID> - &ecreate a new Arena with the given ID" +
                    "\n&6remove <ID> - &edelete an existing Arena" +
                    "\n&6list - &elist all currently loaded Arenas" +
                    "\n&6info <ID> - &eget details for an Arena" +
                    "\n&6setdeathmatch <ID> - &esets the DeathMatch spawn for an Arena" +
                    "\n&6addspawn <ID> - &eadd a new spawn-point for an Arena");

            //Arena Creation Command
        } else if (args[0].equalsIgnoreCase("create")) {
            if (!(sender instanceof Player)) {
                Messaging.send(sender, "This command cannot be run from the Console!");
            } else if (args.length != 2) {
                Messaging.send(sender, "&cInvalid arguments! " +
                        "\nUsage: &6/arena create <ID>");
            } else if (arenaManager.hasArena(args[1])) {
                Messaging.send(sender, "&cError! There is already an Arena with that ID!");
            } else {
                arenaManager.createArena(args[1], ((Player) sender).getWorld());
                Messaging.send(sender, "&aSuccessfully created new Arena with the ID &b" + args[1]);
            }

            //Arena Removal Command
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                Messaging.send(sender, "&cInvalid arguments! " +
                        "\nUsage: &6/arena remove <ID>");
            } else if (!arenaManager.hasArena(args[1])) {
                Messaging.send(sender, "&cError! There is no Arena with that ID!");
            } else {
                arenaManager.removeArena(args[1]);
                Messaging.send(sender, "&aSuccessfully removed Arena with the ID &b" + args[1]);
            }

            //Arena Listing Command
        } else if (args[0].equalsIgnoreCase("list")) {
            StringBuilder sb = new StringBuilder("&7======= &cLoaded Arenas &7=======");
            for (String arenaID : arenaManager.getArenaIDs()) {
                Arena a = arenaManager.getArena(arenaID);
                sb.append("\n&8[" + a.getDisplayName(true) + "&8]&7 " + arenaID);
            }
            sb.append("\n&7=== &cUse &6/arena info <ID> &cfor more info on an Arena &7===");
            Messaging.send(sender, sb.toString());

            //Arena Info Command
        } else if (args[0].equalsIgnoreCase("info")) {
            if (args.length != 2) {
                Messaging.send(sender, "&cInvalid arguments! " +
                        "\nUsage: &6/arena info <ID>");
            } else if (!arenaManager.hasArena(args[1])) {
                Messaging.send(sender, "&cError! There is no Arena with that ID!");
            } else {
                Messaging.send(sender, arenaManager.getArena(args[1]).getArenaInfo());
            }

            //Arena DeathMatch Spawn Setting Command
        } else if (args[0].equalsIgnoreCase("setdeathmatch")) {
            if (!(sender instanceof Player)) {
                Messaging.send(sender, "This command cannot be run from the Console!");
            } else if (args.length != 2) {
                Messaging.send(sender, "&cInvalid arguments! " +
                        "\nUsage: &6/arena setdeathmatch <ID>");
            } else if (!arenaManager.hasArena(args[1])) {
                Messaging.send(sender, "&cError! There is no Arena with that ID!");
            } else if (!arenaManager.getArena(args[1]).getWorld().equals(((Player) sender).getWorld())) {
                Messaging.send(sender, "&cError! The DeathMatch spawn must be in the Arena's World!");
            } else {
                arenaManager.getArena(args[1]).setDeathMatchSpawn(((Player) sender).getLocation());
                Messaging.send(sender, "&aSuccessfully set DeathMatch spawn to your Location for Arena with ID &b" + args[1]);
            }

            //Arena Spawn Addition Command
        } else if (args[0].equalsIgnoreCase("addspawn")) {
            if (!(sender instanceof Player)) {
                Messaging.send(sender, "This command cannot be run from the Console!");
            } else if (args.length != 2) {
                Messaging.send(sender, "&cInvalid arguments! " +
                        "\nUsage: &6/arena addspawn <ID>");
            } else if (!arenaManager.hasArena(args[1])) {
                Messaging.send(sender, "&cError! There is no Arena with that ID!");
            } else if (!arenaManager.getArena(args[1]).getWorld().equals(((Player) sender).getWorld())) {
                Messaging.send(sender, "&cError! Arena spawn-points must be in the Arena's World!");
            } else {
                arenaManager.getArena(args[1]).addSpawnLocation(((Player) sender).getLocation());
                Messaging.send(sender, "&aSuccessfully added spawn-point at your Location for Arena with ID &b" + args[1]);
            }

            //Invalid Sub-Command Message
        } else {
            Messaging.send(sender, "&cInvalid arguments! Use &6/arena help &cfor a list of arguments");
        }
        return true;
    }
}

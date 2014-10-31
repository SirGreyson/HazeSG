/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg;

import net.hazeservers.sg.config.Lang;
import net.hazeservers.sg.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Messaging {

    private static Logger log = JavaPlugin.getPlugin(SurvivalGames.class).getLogger();
    private static String PREFIX = Lang.Messages.PREFIX.toString();
    private static String BROADCAST_PREFIX = Lang.Broadcasts.PREFIX.toString();

    //Console (Logger) Messages
    public static void printInfo(String message) {
        log.info(ChatColor.YELLOW.toString() + message);
    }

    public static void printErr(String message) {
        log.info(ChatColor.RED.toString() + message);
    }

    public static void printErr(String message, String source) {
        printErr(message + " [" + source + "]");
    }

    //Private (Player) Messages
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(StringUtil.color(PREFIX + " " + message));
    }

    public static void send(CommandSender sender, Lang.Messages message) {
        send(sender, message.toString());
    }

    public static void send(CommandSender sender, Lang.Messages message, Object... args) {
        send(sender, String.format(message.toString(), args));
    }

    //Public (Server) Messages
    public static void broadcast(String message) {
        Bukkit.broadcastMessage(StringUtil.color(BROADCAST_PREFIX + " " + message));
    }

    public static void broadcast(Lang.Broadcasts message) {
        broadcast(message.toString());
    }

    public static void broadcast(Lang.Broadcasts message, Object... args) {
        broadcast(String.format(message.toString(), args));
    }
}

/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.hooks;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeeManager {

    private SurvivalGames plugin;

    public BungeeManager(SurvivalGames plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    public void kickToLobby(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(Settings.General.LOBBY_SERVER.toString());
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public void kickAllToLobby() {
        for (Player player : Bukkit.getOnlinePlayers()) kickToLobby(player);
    }

}

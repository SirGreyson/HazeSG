/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.hooks;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;


public class VaultManager {

    private SurvivalGames plugin;
    private static Chat chat;

    public VaultManager(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    public boolean registerChat() {
        RegisteredServiceProvider<Chat> chatProv = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProv != null) {
            chat = chatProv.getProvider();
            Messaging.printInfo("Successfully registered Chat with Vault!");
            return true;
        } else {
            Messaging.printErr("Could not register Chat with Vault...");
            return false;
        }
    }

    public static String getPrefix(Player player) {
        if (chat == null) {
            return " ";
        }
        return " " + chat.getPlayerPrefix(player) + " ";
    }
}

/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.menu;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Settings;
import net.hazeservers.sg.game.Game;
import net.hazeservers.sg.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SpectatorMenu {

    private SurvivalGames plugin;
    private Game game;

    private int menuSize = Settings.Menus.SPECTATOR_MENU.getSize();
    private String menuTitle = Settings.Menus.SPECTATOR_MENU.getTitle();

    public SpectatorMenu(SurvivalGames plugin) {
        this.plugin = plugin;
        this.game = Game.getInstance();
    }

    public boolean isMenu(Inventory inv) {
        return inv.getSize() == menuSize && inv.getTitle().equals(menuTitle);
    }

    public void openMenu(Player player) {
        if (!game.isSpecator(player)) {
            Messaging.send(player, "&cError! You are not a Spectator!");
            return;
        }
        Inventory inv = Bukkit.createInventory(player, menuSize, menuTitle);
        for (Player p : PlayerUtil.getPlayerList(game.getPlayers())) inv.addItem(PlayerUtil.getHead(p));
        player.openInventory(inv);
    }

    public void handleClick(InventoryClickEvent e) {
        if (e.getCurrentItem().getType() != Material.SKULL_ITEM) return;
        Player player = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName());
        if (player == null || !game.isPlayer(player)) {
            e.getWhoClicked().closeInventory();
            Messaging.send((CommandSender) e.getWhoClicked(), "&cError! That Player is no longer in the Game!");
        } else {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().teleport(player.getLocation());
            Messaging.send((CommandSender) e.getWhoClicked(), "&eTeleporting to &6" + player.getName() + " &e...");
        }
    }
}

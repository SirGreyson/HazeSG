/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.menu;

import net.hazeservers.sg.SurvivalGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuManager implements Listener {

    private SurvivalGames plugin;

    public static KitMenu KIT_MENU;
    public static SpectatorMenu SPECTATOR_MENU;

    public MenuManager(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    public void loadMenus() {
        KIT_MENU = new KitMenu(plugin);
        SPECTATOR_MENU = new SpectatorMenu(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMenuClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (KIT_MENU.isMenu(e.getInventory())) {
            e.setCancelled(true);
            KIT_MENU.handleClick(e);
        } else if (SPECTATOR_MENU.isMenu(e.getInventory())) {
            e.setCancelled(true);
            SPECTATOR_MENU.handleClick(e);
        }
    }
}

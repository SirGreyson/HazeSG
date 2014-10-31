/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.menu;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Lang;
import net.hazeservers.sg.config.Settings;
import net.hazeservers.sg.kit.Kit;
import net.hazeservers.sg.kit.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class KitMenu {

    private KitManager kitManager;
    private List<String> kitIDs;

    private int menuSize = Settings.Menus.KIT_MENU.getSize();
    private String menuTitle = Settings.Menus.KIT_MENU.getTitle();

    public KitMenu(SurvivalGames plugin) {
        this.kitManager = plugin.getKitManager();
        this.kitIDs = kitManager.getKitIDs();
    }

    public boolean isMenu(Inventory inv) {
        return inv.getSize() == menuSize && inv.getTitle().equals(menuTitle);
    }

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(player, menuSize, menuTitle);
        for (String kitID : kitIDs) {
            inv.addItem(kitManager.getKit(kitID).getMenuIcon(player));
        }
        player.openInventory(inv);
    }

    public void handleClick(InventoryClickEvent e) {
        if (e.getRawSlot() >= kitIDs.size()) return;
        Kit kit = kitManager.getKit(kitIDs.get(e.getRawSlot()));
        e.getWhoClicked().closeInventory();
        if (kit == null) {
            Messaging.send((Player) e.getWhoClicked(), "&cError! Kit cannot be null! Please report this to an Admin");
        } else if (!kit.hasPermission((Player) e.getWhoClicked())) {
            Messaging.send((Player) e.getWhoClicked(), Lang.Messages.KIT_NO_PERM);
        } else {
            kitManager.setSelectedKit((Player) e.getWhoClicked(), kit.getKitID());
        }
    }
}

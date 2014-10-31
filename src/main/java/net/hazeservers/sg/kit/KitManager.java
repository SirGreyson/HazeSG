/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.kit;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitManager {

    private YamlConfiguration config;
    private TreeMap<String, Kit> loadedKits;

    private ItemStack kitSelector;
    private Map<UUID, String> selectedKits;

    private ItemStack playerSelector;
    private ItemStack hubReturner;

    public KitManager(SurvivalGames plugin) {
        this.config = plugin.getConfigManager().getConfig("kits");
        this.loadedKits = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.selectedKits = new HashMap<>();
    }

    public void loadKits() {
        for (String kitID : config.getKeys(false))
            loadedKits.put(kitID, Kit.deserialize(config.getConfigurationSection(kitID)));
        Messaging.printInfo("Successfully loaded " + loadedKits.size() + " Kits(s)!");
    }

    public List<String> getKitIDs() {
        return new ArrayList<>(loadedKits.keySet());
    }

    public boolean hasKit(String kitID) {
        return loadedKits.containsKey(kitID);
    }

    public Kit getKit(String kitID) {
        return loadedKits.get(kitID);
    }

    //Kit selection methods
    public boolean isKitSelector(ItemStack itemStack) {
        return getKitSelector().isSimilar(itemStack);
    }

    public ItemStack getKitSelector() {
        if (kitSelector != null) {
            return kitSelector;
        }
        ItemStack itemStack = new ItemStack(Material.BOW, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Kit Selector");
        meta.setLore(Arrays.asList(ChatColor.GREEN + "Right click me to open the Kit menu"));
        itemStack.setItemMeta(meta);
        kitSelector = itemStack;
        return kitSelector;
    }

    public Kit getSelectedKit(Player player) {
        if (!selectedKits.containsKey(player.getUniqueId())) return loadedKits.get("DEFAULT");
        return loadedKits.get(selectedKits.get(player.getUniqueId()));
    }

    public void setSelectedKit(Player player, String kitID) {
        selectedKits.put(player.getUniqueId(), kitID);
        Messaging.send(player, Lang.Messages.KIT_SELECTED, getKit(kitID).getDisplayName(true));
    }

    //Equip all Players with their selected Kit (or DEFAULT)
    public void equipAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            getSelectedKit(player).equipPlayer(player);
            Messaging.send(player, Lang.Messages.KIT_EQUIPPED, getSelectedKit(player).getDisplayName(true));
        }
    }

    //Equip new Spectator with a Spectator Kit
    public void equipSpectator(Player player) {
        //Player Selector
        if (playerSelector == null) {
            ItemStack i = new ItemStack(Material.COMPASS, 1);
            ItemMeta m = i.getItemMeta();
            m.setDisplayName(ChatColor.GOLD + "Player Selector");
            m.setLore(Arrays.asList(ChatColor.GREEN + "Click me to chose who to spectate"));
            i.setItemMeta(m);
            playerSelector = i;
        }
        player.getInventory().setItem(0, playerSelector);
        //Hub Returner
        if (hubReturner == null) {
            ItemStack i = new ItemStack(Material.COMPASS, 1);
            ItemMeta m = i.getItemMeta();
            m.setDisplayName(ChatColor.GOLD + "Return to Hub");
            m.setLore(Arrays.asList(ChatColor.GREEN + "Click me to return to the Hub server"));
            i.setItemMeta(m);
            hubReturner = i;
        }
        player.getInventory().setItem(8, hubReturner);
    }
}

/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.kit;

import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit {

    private SurvivalGames plugin;
    private String kitID;
    private String permission;
    private String displayName;
    private Material iconMaterial;
    private Map<Integer, List<ItemStack>> kitItems;
    private Map<Integer, List<PotionEffect>> kitEffects;

    public Kit(String kitID, String permission, String displayName, Material iconMaterial, Map<Integer, List<ItemStack>> kitItems, Map<Integer, List<PotionEffect>> kitEffects) {
        this.kitID = kitID;
        this.permission = permission;
        this.displayName = displayName;
        this.iconMaterial = iconMaterial;
        this.kitItems = kitItems;
        this.kitEffects = kitEffects;
    }

    public String getKitID() {
        return kitID;
    }

    public boolean hasPermission(Player player) {
        return permission.equalsIgnoreCase("NONE") || player.hasPermission(permission);
    }

    public String getDisplayName(boolean colored) {
        return colored ? StringUtil.color(displayName) : displayName;
    }

    public ItemStack getMenuIcon(Player player) {
        ItemStack output = new ItemStack(iconMaterial, 1);
        ItemMeta meta = output.getItemMeta();
        meta.setDisplayName(StringUtil.color(displayName));
        meta.setLore(Arrays.asList(
                hasPermission(player) ? ChatColor.AQUA + "Click to equip this Kit!" : ChatColor.RED + "You do not have permission for this Kit!",
                ChatColor.GOLD + "Upgrade Level:" + ChatColor.YELLOW + plugin.getDataManager().getKitLevel(player, this)
        ));
        output.setItemMeta(meta);
        return output;
    }

    public void equipPlayer(Player player) {
        player.getInventory().clear();
        int level = plugin.getDataManager().getKitLevel(player, this);
        for (ItemStack i : kitItems.get(level)) player.getInventory().addItem(i);
        for (PotionEffect p : kitEffects.get(level)) player.addPotionEffect(p);
    }

    public static Kit deserialize(final ConfigurationSection c) {
        Map<Integer, List<ItemStack>> kitItems = new HashMap<Integer, List<ItemStack>>() {{
            for (String level : c.getConfigurationSection("kitItems").getKeys(false))
                put(StringUtil.asInt(level), StringUtil.parseItemList(c.getStringList("kitItems." + level)));
        }};
        Map<Integer, List<PotionEffect>> kitEffects = new HashMap<Integer, List<PotionEffect>>() {{
            for (String level : c.getConfigurationSection("kitEffects").getKeys(false))
                put(StringUtil.asInt(level), StringUtil.parseEffectList(c.getStringList("kitEffects." + level)));
        }};
        return new Kit(c.getName(), c.getString("permission"), c.getString("displayName"), Material.valueOf(c.getString("iconMaterial")), kitItems, kitEffects);
    }
}

/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.util;

import net.hazeservers.sg.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerUtil {

    public static void dropInventory(Player player) {
        if (player == null) return;
        for (ItemStack i : player.getInventory().getContents())
            if (i != null && i.getType() != Material.AIR)
                player.getWorld().dropItem(player.getLocation(), i);
    }

    public static ItemStack getHead(Player player) {
        ItemStack output = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) output.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(player.getName());
        output.setItemMeta(meta);
        return output;
    }

    public static List<Player> getPlayerList(List<UUID> input) {
        List<Player> output = new ArrayList<>();
        Iterator<UUID> i = input.iterator();
        while (i.hasNext()) {
            Player player = Bukkit.getPlayer(i.next());
            if (player == null) i.remove();
            else output.add(player);
        }
        return output;
    }

    public static void reset(Player player) {
        if (player == null) return;
        player.setHealth(player.getMaxHealth());
        player.setSaturation(4.0f);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0);
        player.setTotalExperience(0);
        player.setFlying(false);
        player.setAllowFlight(false);
        for (PotionEffect p : player.getActivePotionEffects())
            player.removePotionEffect(p.getType());
    }

    public static void reset(Player player, boolean teleport, boolean clearBoard) {
        reset(player);
        if (teleport) player.teleport(Settings.General.SPAWN_WORLD.toWorld().getSpawnLocation());
        if (clearBoard) player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public static void resetAll(boolean teleport, boolean clearBoard) {
        for (Player player : Bukkit.getOnlinePlayers()) reset(player, teleport, clearBoard);
    }
}

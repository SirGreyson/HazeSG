/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.arena;

import net.hazeservers.sg.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Arena {

    private String arenaID;
    private String displayName;
    private String worldName;

    private Vector deathMatchSpawn;
    private List<Vector> spawnLocations;

    public Arena(String arenaID, World world) {
        this.arenaID = arenaID;
        this.displayName = arenaID;
        this.worldName = world.getName();
        this.deathMatchSpawn = world.getSpawnLocation().toVector();
        this.spawnLocations = new ArrayList<>();
    }

    public Arena(String arenaID, String displayName, String worldName, Vector deathMatchSpawn, List<Vector> spawnLocations) {
        this.arenaID = arenaID;
        this.displayName = displayName;
        this.worldName = worldName;
        this.deathMatchSpawn = deathMatchSpawn;
        this.spawnLocations = spawnLocations;
    }

    public String getArenaID() {
        return arenaID;
    }

    public String getDisplayName(boolean color) {
        return color ? StringUtil.color(displayName) : displayName;
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public Location getDeathMatchSpawn() {
        return deathMatchSpawn.toLocation(getWorld());
    }

    public void setDeathMatchSpawn(Location deathMatchSpawn) {
        this.deathMatchSpawn = deathMatchSpawn.toVector();
    }

    public List<Vector> getSpawnLocations() {
        return spawnLocations;
    }

    public void addSpawnLocation(Location location) {
        spawnLocations.add(location.toVector());
    }

    public String getArenaInfo() {
        StringBuilder sb = new StringBuilder("&7Info for Arena &c" + arenaID);
        sb.append("\n&6Display Name: " + getDisplayName(true));
        sb.append("\n&6World Name: &e" + worldName);
        sb.append("\n&6DeathMatch Spawn: &e" + deathMatchSpawn.toString());
        sb.append("\n&6Spawn Locations:");
        for (Vector spawn : spawnLocations) {
            sb.append("&e- " + spawn.toString());
        }
        sb.append("&7==========");
        return sb.toString();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("displayName", displayName);
        output.put("worldName", worldName);
        output.put("deathMatchSpawn", deathMatchSpawn.toString());
        output.put("spawnLocations", StringUtil.getVectorStringList(spawnLocations));
        return output;
    }

    public static Arena deserialize(ConfigurationSection c) {
        return new Arena(c.getName(), c.getString("displayName"), c.getString("worldName"),
                StringUtil.parseVector(c.getString("deathMatchSpawn")), StringUtil.parseVectorList(c.getStringList("spawnLocations")));
    }
}

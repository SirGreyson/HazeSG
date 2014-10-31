/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.arena;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.util.FileUtil;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Random;
import java.util.TreeMap;

public class ArenaManager {

    private SurvivalGames plugin;
    private YamlConfiguration config;
    private TreeMap<String, Arena> loadedArenas;

    public ArenaManager(SurvivalGames plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getConfig("arenas");
        this.loadedArenas = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void loadArenas() {
        for (String arenaID : config.getKeys(false))
            loadedArenas.put(arenaID, Arena.deserialize(config.getConfigurationSection(arenaID)));
        Messaging.printInfo("Successfully loaded " + loadedArenas.size() + " Arena(s)!");
    }

    public void saveArenas() {
        for (String arenaID : loadedArenas.keySet())
            config.set(arenaID, loadedArenas.get(arenaID).serialize());
        Messaging.printInfo("Successfully saved all Arenas!");
    }

    public void createArena(String arenaID, World world) {
        loadedArenas.put(arenaID, new Arena(arenaID, world));
    }

    public void removeArena(String arenaID) {
        config.set(loadedArenas.get(arenaID).getArenaID(), null);
        loadedArenas.remove(arenaID);
    }

    public Collection<String> getArenaIDs() {
        return loadedArenas.keySet();
    }

    public boolean hasArena(String arenaID) {
        return loadedArenas.containsKey(arenaID);
    }

    public Arena getArena(String arenaID) {
        return loadedArenas.get(arenaID);
    }

    public Arena getRandomArena() {
        if (loadedArenas.size() <= 0) return null;
        return (Arena) loadedArenas.values().toArray()[new Random().nextInt(loadedArenas.size())];
    }

    private File getArenaTemplate(Arena arena) {
        File templateDir = new File(plugin.getDataFolder(), File.separator + "templates");
        if (!templateDir.exists()) templateDir.mkdir();
        File worldDir = new File(templateDir, File.separator + arena.getWorldName());
        if (worldDir == null) Messaging.printErr("Error! Could not find World template!", arena.getWorldName());
        return worldDir;
    }

    public boolean loadArenaWorld(Arena arena) {
        Messaging.printInfo("Loading World template for Arena with ID " + arena.getArenaID());
        File template = getArenaTemplate(arena);
        if (template == null) return false;
        if (!FileUtil.copy(template, new File(plugin.getServer().getWorldContainer(), arena.getWorldName())))
            Messaging.printErr("Error! Could not copy World template!", arena.getWorldName());
        else plugin.getServer().createWorld(new WorldCreator(arena.getWorldName()));
        return arena.getWorld() != null;
    }

    public void unloadArenaWorld(Arena arena) {
        Messaging.printInfo("Unloading World template for Arena with ID " + arena.getArenaID());
        plugin.getServer().unloadWorld(arena.getWorld(), false);
        FileUtil.deleteFolder(new File(plugin.getServer().getWorldContainer(), arena.getWorldName()));
        Messaging.printInfo("World successfully unloaded and deleted!");
    }
}

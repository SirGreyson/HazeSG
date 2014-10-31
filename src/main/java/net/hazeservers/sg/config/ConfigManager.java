/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.config;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.util.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

public class ConfigManager {

    private SurvivalGames plugin;
    private TreeMap<String, File> configFiles = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private TreeMap<String, YamlConfiguration> loadedConfigs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public ConfigManager(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads all necessary YamlConfigurations
     */
    public void loadConfigs() {
        loadConfig("arenas");
        loadConfig("config");
        loadConfig("lang");
        loadConfig("kits");
        Lang.setConfig(getConfig("lang")); //TODO Make this nicer
        Settings.setConfig(getConfig("config")); //TODO Make this nicer
        Messaging.printInfo("Successfully loaded all Configurations!");
    }

    /**
     * Saves all necessary YamlConfigurations
     */
    public void saveConfigs() {
        saveConfig("arenas");
        Messaging.printInfo("Successfully saved all Configurations!");
    }

    /**
     * Checks to see if a given YamlConfiguration is loaded
     *
     * @param name The name of the YamlConfiguration
     * @return Whether or not the YamlConfiguration is loaded
     */
    private boolean isConfigLoaded(String name) {
        return loadedConfigs.containsKey(name);
    }

    /**
     * Gets a loaded YamlConfiguration by name
     *
     * @param name The name of the YamlConfiguration
     * @return The loaded YamlConfiguration, if it exists
     */
    public YamlConfiguration getConfig(String name) {
        return loadedConfigs.get(name);
    }

    /**
     * Loads changes to a YamlConfiguration's File
     *
     * @param name The name of the YamlConfiguration to reload
     */
    public void reloadConfig(String name) {
        if (!FileUtil.validate(configFiles.get(name), plugin))
            Messaging.printErr("Error! Could not reload Configuration!", name);
        else loadedConfigs.put(name, YamlConfiguration.loadConfiguration(configFiles.get(name)));
    }

    /**
     * Loads a YamlConfiguration from an existing or newly created File
     *
     * @param name The name of the YamlConfiguration to load
     */
    private void loadConfig(String name) {
        File cFile = new File(plugin.getDataFolder(), name + ".yml");
        if (!FileUtil.validate(cFile, plugin)) Messaging.printErr("Error! Could not load Configuration!", name);
        else if (!isConfigLoaded(name)) {
            configFiles.put(name, cFile);
            loadedConfigs.put(name, YamlConfiguration.loadConfiguration(cFile));
        }
    }

    /**
     * Saves a YamlConfiguration to a File
     *
     * @param name The name of the YamlConfiguration to save
     */
    private void saveConfig(String name) {
        if (!isConfigLoaded(name)) {
            Messaging.printErr("Error! Tried to save invalid Configuration!", name);
            return;
        }
        try {
            loadedConfigs.get(name).save(configFiles.get(name));
        } catch (IOException e) {
            Messaging.printErr("Error! Could not save Configuration!", name);
        }
    }

}

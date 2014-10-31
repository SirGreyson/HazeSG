/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg;

import net.hazeservers.sg.arena.ArenaManager;
import net.hazeservers.sg.cmd.CommandManager;
import net.hazeservers.sg.config.ConfigManager;
import net.hazeservers.sg.data.DataManager;
import net.hazeservers.sg.game.Game;
import net.hazeservers.sg.hooks.BungeeManager;
import net.hazeservers.sg.hooks.VaultManager;
import net.hazeservers.sg.kit.KitManager;
import net.hazeservers.sg.listener.PlayerListener;
import net.hazeservers.sg.listener.ServerListener;
import net.hazeservers.sg.menu.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SurvivalGames extends JavaPlugin {

    private ArenaManager arenaManager;
    private BungeeManager bungeeManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private DataManager dataManager;
    private KitManager kitManager;
    private MenuManager menuManager;
    private ScoreboardManager scoreboardManager;
    private VaultManager vaultManager;

    public void onEnable() {
        //Load Managers
        getConfigManager().loadConfigs();
        getArenaManager().loadArenas();
        getCommandManager().registerCommands();
        getDataManager().openConnection();
        getKitManager().loadKits();
        getMenuManager().loadMenus();
        getScoreboardManager().loadSpectatorTeam();
        getVaultManager().registerChat();
        //Setup Game
        Game.getInstance().setup(this);
        //Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        Messaging.printInfo("has been enabled");
    }

    public void onDisable() {
        getArenaManager().saveArenas();
        getConfigManager().saveConfigs();
        getDataManager().closeAndSave();
        Messaging.printInfo("has been disabled");
    }

    public ArenaManager getArenaManager() {
        if (arenaManager == null) arenaManager = new ArenaManager(this);
        return arenaManager;
    }

    public BungeeManager getBungeeManager() {
        if (bungeeManager == null) bungeeManager = new BungeeManager(this);
        return bungeeManager;
    }

    public CommandManager getCommandManager() {
        if (commandManager == null) commandManager = new CommandManager(this);
        return commandManager;
    }

    public ConfigManager getConfigManager() {
        if (configManager == null) configManager = new ConfigManager(this);
        return configManager;
    }

    public DataManager getDataManager() {
        if (dataManager == null) dataManager = new DataManager(this);
        return dataManager;
    }

    public KitManager getKitManager() {
        if (kitManager == null) kitManager = new KitManager(this);
        return kitManager;
    }

    public MenuManager getMenuManager() {
        if (menuManager == null) menuManager = new MenuManager(this);
        return menuManager;
    }

    public ScoreboardManager getScoreboardManager() {
        if (scoreboardManager == null) scoreboardManager = new ScoreboardManager(this);
        return scoreboardManager;
    }

    public VaultManager getVaultManager() {
        if (vaultManager == null) vaultManager = new VaultManager(this);
        return vaultManager;
    }
}

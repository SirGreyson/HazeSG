/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.game;

import me.lordal.haze.tokens.api.TokensAPI;
import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.arena.Arena;
import net.hazeservers.sg.config.Lang;
import net.hazeservers.sg.config.Settings;
import net.hazeservers.sg.util.FileUtil;
import net.hazeservers.sg.util.PlayerUtil;
import net.hazeservers.sg.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Game {

    //Singleton Declaration
    private static Game instance = null;

    protected Game() {
    }

    public static Game getInstance() {
        if (instance == null) instance = new Game();
        return instance;
    }

    //Game Object Variables
    private SurvivalGames plugin;
    private Arena activeArena;
    private GameTimer gameTimer;

    private GameState gameState = GameState.WAITING;
    private List<UUID> players = new ArrayList<UUID>();
    private List<UUID> spectators = new ArrayList<UUID>();

    private int index = -1;
    private List<Location> openedChests = new ArrayList<>();
    private Random random = new Random();

    //Game Object Setup
    public void setup(SurvivalGames plugin) {
        this.plugin = plugin;
        this.gameTimer = new GameTimer(plugin);
        setActiveArena(plugin.getArenaManager().getRandomArena());
        FileUtil.deleteFolder(new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats"));
    }

    public Arena getActiveArena() {
        return activeArena;
    }

    public void setActiveArena(Arena activeArena) {
        this.activeArena = activeArena;
        if (!plugin.getArenaManager().loadArenaWorld(activeArena)) {
            Messaging.printErr("Error! Could not load World for Arena!", activeArena.getArenaID());
        }
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public GameState getState() {
        return gameState;
    }

    public void setState(GameState gameState) {
        gameTimer.cancel();
        this.gameState = gameState;
        if (gameState.isRunnable()) gameTimer.run();
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public boolean isPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        PlayerUtil.reset(player);
        player.teleport(getNextSpawn());
        players.add(player.getUniqueId());
        plugin.getScoreboardManager().assignGameBoard(player);
        player.getInventory().addItem(plugin.getKitManager().getKitSelector());
    }

    public void removePlayer(Player player) {
        PlayerUtil.reset(player);
        players.remove(player.getUniqueId());
        if (canFinish()) finishGame();
    }

    public boolean isSpecator(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public void addSpectator(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        spectators.add(player.getUniqueId());
        plugin.getScoreboardManager().addSpectator(player);
        plugin.getKitManager().equipSpectator(player);
        Messaging.send(player, Lang.Messages.SPECTATOR_MODE);
    }

    private Location getNextSpawn() {
        if (index++ >= activeArena.getSpawnLocations().size()) index = 0;
        return activeArena.getSpawnLocations().get(index).toLocation(activeArena.getWorld());
    }

    public boolean isChestOpened(Block block) {
        return openedChests.contains(block.getLocation());
    }

    public void setChestOpened(Block block) {
        openedChests.add(block.getLocation());
    }

    public void loadChest(Chest chest) {
        chest.getBlockInventory().clear();
        Inventory inv = chest.getBlockInventory();
        for (String i : Settings.General.CHEST_LOADOUT.toList()) {
            if (!i.contains("=")) {
                inv.addItem(StringUtil.parseItem(i));
            } else if (random.nextInt(100) <= StringUtil.asInt(i.split("=")[1])) {
                inv.addItem(StringUtil.parseItem(i.split("=")[0]));
            }
        }
    }

    public void resetChests() {
        openedChests = new ArrayList<>();
        Messaging.broadcast(Lang.Broadcasts.CHESTS_RESTOCKED);
    }

    //Game State Management Methods

    public boolean canStart() {
        if (gameState == GameState.FORCE_STARTING) return Bukkit.getOnlinePlayers().size() > 1;
        else return Bukkit.getOnlinePlayers().size() >= Settings.General.STARTING_PLAYERS.toInt();
    }

    public void cancelStart() {
        setState(GameState.WAITING);
        Messaging.broadcast(Lang.Broadcasts.START_CANCELLED);
    }

    public void startPreGame() {
        activeArena.getWorld().setTime(0);
        for (Player player : Bukkit.getOnlinePlayers()) addPlayer(player);
        setState(GameState.PREGAME);
        Messaging.broadcast(Lang.Broadcasts.PREGAME_STARTED, gameTimer.getCountdown());
    }

    public void startGame() {
        PlayerUtil.resetAll(false, false);
        setState(GameState.INGAME);
        Messaging.broadcast(Lang.Broadcasts.GAME_STARTED);
    }

    public boolean canStartDeathMatch() {
        return players.size() <= Settings.General.DEATHMATCH_PLAYERS.toInt();
    }

    public boolean isPvPDisabled() {
        return gameState == GameState.DEATH_MATCH &&
                gameTimer.getCountdown() > Settings.Countdown.END_DEATHMATCH.toInt() - Settings.Countdown.ENABLE_PVP.toInt();
    }

    public void startDeathMatch() {
        for (Player player : PlayerUtil.getPlayerList(players)) player.teleport(activeArena.getDeathMatchSpawn());
        setState(GameState.DEATH_MATCH);
        Messaging.broadcast(Lang.Broadcasts.DEATHMATCH_STARTED, Settings.Countdown.ENABLE_PVP.toInt());
    }

    public boolean canFinish() {
        return gameState != GameState.FINISHING && players.size() <= 1;
    }

    public void finishGame() {
        PlayerUtil.resetAll(true, true);
        setState(GameState.FINISHING);
        if (players.size() == 1) {
            Player winner = Bukkit.getPlayer(players.get(0));
            Messaging.broadcast(Lang.Broadcasts.GAME_FINISHED, winner.getName());
            TokensAPI.depositTokens(winner, Settings.General.WIN_TOKENS.toInt());
            Messaging.send(winner, Lang.Messages.TOKENS_GAINED, Settings.General.WIN_TOKENS.toInt());
            plugin.getDataManager().getData(winner).incrementWins();
        } else {
            Messaging.broadcast(Lang.Broadcasts.GAME_FINISHED, "NO ONE");
        }
    }

    public void reset() {
        setState(GameState.RESETTING);
        plugin.getBungeeManager().kickAllToLobby();
        plugin.getArenaManager().unloadArenaWorld(activeArena);
        FileUtil.deleteFolder(new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats"));
        for (String cmd : Settings.General.RESTART_COMMANDS.toList()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
}

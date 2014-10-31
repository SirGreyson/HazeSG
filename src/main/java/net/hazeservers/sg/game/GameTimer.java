/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.game;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Lang;
import net.hazeservers.sg.config.Settings;
import org.bukkit.scheduler.BukkitTask;

public class GameTimer {

    private SurvivalGames plugin;
    private final Game game = Game.getInstance();

    private int countdown;
    private BukkitTask task;

    public GameTimer(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    public int getCountdown() {
        return countdown;
    }

    public void cancel() {
        if (task == null) return;
        task.cancel();
        task = null;
    }

    public void run() {
        //Timer for Starting tasks
        if (game.getState() == GameState.STARTING || game.getState() == GameState.FORCE_STARTING) {
            countdown = Settings.Countdown.START_PREGAME.toInt() + 1;
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    if (!game.canStart()) {
                        game.cancelStart();
                    } else if (countdown-- <= 0) {
                        game.startPreGame();
                    } else if (countdown < 10 || countdown % 5 == 0) {
                        Messaging.broadcast(Lang.Broadcasts.STARTING, countdown);
                    }
                }
            }, 20, 20);

            //Timer for Pre-Game tasks
        } else if (game.getState() == GameState.PREGAME) {
            countdown = Settings.Countdown.START_GAME.toInt();
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    if (countdown-- <= 0) {
                        game.startGame();
                        return;
                    } else if (countdown <= 10 || countdown % 5 == 0) {
                        Messaging.broadcast(Lang.Broadcasts.GAME_STARTING, countdown);
                    }
                    plugin.getScoreboardManager().updatePlayerBoards();
                }
            }, 20, 20);

            //Timer for In-Game tasks
        } else if (game.getState() == GameState.INGAME) {
            countdown = Settings.Countdown.END_GAME.toInt();
            final int kitTime = countdown - Settings.Countdown.EQUIP_KITS.toInt();
            final int chestTime = countdown - Settings.Countdown.RESET_CHESTS.toInt();
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    if (countdown-- <= 0) {
                        game.startDeathMatch();
                        return;
                    } else if (countdown == kitTime) {
                        plugin.getKitManager().equipAllPlayers();
                    } else if (countdown == chestTime) {
                        game.resetChests();
                    } else if (countdown > 60 && game.canStartDeathMatch()) {
                        countdown = 61;
                    } else if (countdown <= 10 || (countdown <= 60 && countdown % 10 == 0)) {
                        Messaging.broadcast(Lang.Broadcasts.DEATHMATCH_STARTING, countdown);
                    }
                    plugin.getScoreboardManager().updatePlayerBoards();
                }
            }, 20, 20);

            //Timer for DeathMatch tasks
        } else if (game.getState() == GameState.DEATH_MATCH) {
            countdown = Settings.Countdown.END_DEATHMATCH.toInt();
            final int pvpTime = countdown - Settings.Countdown.ENABLE_PVP.toInt();
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    if (countdown-- <= 0) {
                        game.finishGame();
                        return;
                    } else if (countdown == pvpTime) {
                        Messaging.broadcast(Lang.Broadcasts.PVP_ENABLED);
                    } else if (countdown <= 10 || (countdown <= 60 && countdown % 10 == 0)) {
                        Messaging.broadcast(Lang.Broadcasts.DEATHMATCH_ENDING, countdown);
                    }
                    plugin.getScoreboardManager().updatePlayerBoards();
                }
            }, 20, 20);

            //Timer for Finishing tasks
        } else if (game.getState() == GameState.FINISHING) {
            countdown = Settings.Countdown.RESET_SERVER.toInt();
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    if (countdown-- <= 0) {
                        game.reset();
                    } else if (countdown <= 10 || countdown % 5 == 0) {
                        Messaging.broadcast(Lang.Broadcasts.SERVER_RESTARTING, countdown);
                    }
                }
            }, 20, 20);
        }
    }
}

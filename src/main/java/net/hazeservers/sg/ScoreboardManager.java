/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg;

import com.google.common.collect.Lists;
import net.hazeservers.sg.config.Lang;
import net.hazeservers.sg.game.Game;
import net.hazeservers.sg.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardManager {

    private SurvivalGames plugin;
    private final Game game = Game.getInstance();

    private Team spectatorTeam;

    public ScoreboardManager(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    //GameBoard methods
    private Scoreboard newGameBoard() {
        Scoreboard output = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = output.registerNewObjective("main", "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        return output;
    }

    private List<String> getEntries(Player player) {
        List<String> output = new ArrayList<>();
        for (String s : Lang.Scoreboard.FORMAT.toList()) {
            output.add(s
                    .replace("%arena%", game.getActiveArena().getDisplayName(true))
                    .replace("%kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)))
                    .replace("%players%", String.valueOf(game.getPlayers().size()))
                    .replace("%time%", StringUtil.formatTime(game.getGameTimer().getCountdown())));
        }
        return StringUtil.colorAll(Lists.reverse(output));
    }

    public void assignGameBoard(Player player) {
        player.setScoreboard(newGameBoard());
        updateGameBoard(player);
    }

    private void updateGameBoard(Player player) {
        Objective o = player.getScoreboard().getObjective("main");
        o.setDisplayName(Lang.Scoreboard.TITLE.toString());
        for (String s : o.getScoreboard().getEntries()) o.getScoreboard().resetScores(s);
        List<String> entries = getEntries(player);
        for (int i = 0; i < entries.size(); i++) o.getScore(entries.get(i)).setScore(i + 1);
    }

    public void updatePlayerBoards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getScoreboard().getObjective("main") == null) assignGameBoard(player);
            else updateGameBoard(player);
        }
    }

    //Spectator Board methods
    public void loadSpectatorTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        spectatorTeam = scoreboard.getTeam("SPECTATORS");
        if (spectatorTeam == null) {
            spectatorTeam = scoreboard.registerNewTeam("SPECTATORS");
            spectatorTeam.setAllowFriendlyFire(false);
            spectatorTeam.setCanSeeFriendlyInvisibles(true);
        }
    }

    public void addSpectator(Player player) {
        spectatorTeam.addPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15, true));
    }
}

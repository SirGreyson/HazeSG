/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.data;

import org.bukkit.entity.Player;

public class PlayerData {

    private String playerID, lastName;
    private int kills, deaths, wins;
    private boolean showBlood;

    public PlayerData(Player player) {
        this.playerID = player.getUniqueId().toString();
        this.lastName = player.getName();
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.showBlood = true;
    }

    public PlayerData(Player player, int kills, int deaths, int wins, boolean showBlood) {
        this.playerID = player.getUniqueId().toString();
        this.lastName = player.getName();
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.showBlood = showBlood;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        this.kills += 1;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        this.deaths += 1;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        this.wins += 1;
    }

    public boolean canSeeBlood() {
        return showBlood;
    }

    public void toggleBlood() {
        showBlood = !showBlood;
    }

    public Object[] serialize() {
        return new Object[]{playerID, lastName, kills, deaths, wins, showBlood ? 1 : 0};
    }
}

/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.game;

public enum GameState {

    WAITING, STARTING, FORCE_STARTING, PREGAME, INGAME, DEATH_MATCH, FINISHING, RESETTING;

    public boolean canPvP() {
        return this == INGAME || this == DEATH_MATCH;
    }

    public boolean isJoinable() {
        return this == WAITING || this == STARTING || this == FORCE_STARTING;
    }

    public boolean isRunnable() {
        return this != WAITING && this != RESETTING;
    }
}

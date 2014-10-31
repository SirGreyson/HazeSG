/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.listener;

import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Lang;
import net.hazeservers.sg.game.Game;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

    private SurvivalGames plugin;
    private Game game = Game.getInstance();

    public ServerListener(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        e.setCancelled(!game.isPlayer((Player) e.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        e.setCancelled(!game.getState().canPvP() || !game.isPlayer((Player) e.getDamager()) || game.isPvPDisabled());
        if (!e.isCancelled()) {
            if (plugin.getDataManager().getData((Player) e.getEntity()).canSeeBlood()) {
                ((Player) e.getEntity()).playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, 152);
            }
            if (plugin.getDataManager().getData((Player) e.getDamager()).canSeeBlood()) {
                ((Player) e.getDamager()).playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, 152);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent e) {
        e.setMotd(Lang.MOTD.fromGameState(Game.getInstance().getState()).toString());
    }
}

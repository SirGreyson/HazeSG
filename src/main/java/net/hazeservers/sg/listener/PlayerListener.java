/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.listener;

import me.lordal.haze.tokens.api.TokensAPI;
import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Lang;
import net.hazeservers.sg.config.Settings;
import net.hazeservers.sg.game.Game;
import net.hazeservers.sg.game.GameState;
import net.hazeservers.sg.hooks.VaultManager;
import net.hazeservers.sg.menu.MenuManager;
import net.hazeservers.sg.util.PlayerUtil;
import net.hazeservers.sg.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Iterator;

public class PlayerListener implements Listener {

    private SurvivalGames plugin;
    private Game game = Game.getInstance();

    public PlayerListener(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().isOp()) e.setCancelled(false);
        else
            e.setCancelled(!Settings.General.ITEM_BREAK_WHITELIST.toList().contains(e.getBlock().getType().toString()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().isOp()) e.setCancelled(false);
        else
            e.setCancelled(!Settings.General.ITEM_PLACE_WHITELIST.toList().contains(e.getBlock().getType().toString()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (game.isSpecator(e.getPlayer())) {
            Iterator<Player> i = e.getRecipients().iterator();
            while (i.hasNext()) {
                if (!game.isSpecator(i.next())) i.remove();
            }
        }
        e.setFormat(StringUtil.color("[" + plugin.getDataManager().getData(e.getPlayer()).getKills() + "]"
                + VaultManager.getPrefix(e.getPlayer()) + "&7%1$s: %2$s"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        if (!game.isPlayer(e.getEntity())) return;
        //Player Died
        if (e.getEntity().getKiller() == null) {
            Messaging.broadcast(Lang.Broadcasts.DEATH_MESSAGE, e.getEntity().getName());
            //Player was Killed
        } else {
            Messaging.broadcast(Lang.Broadcasts.PVP_DEATH_MESSAGE, e.getEntity().getName(), e.getEntity().getKiller().getName());
            TokensAPI.depositTokens(e.getEntity().getKiller(), Settings.General.KILL_TOKENS.toInt());
            Messaging.send(e.getEntity().getKiller(), Lang.Messages.TOKENS_GAINED, Settings.General.KILL_TOKENS.toInt());
            plugin.getDataManager().getData(e.getEntity().getKiller()).incrementKills();
        }
        //Fake Player Death
        PlayerUtil.dropInventory(e.getEntity());
        game.removePlayer(e.getEntity());
        game.addSpectator(e.getEntity());
        e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
        //Increment Deaths Stat
        plugin.getDataManager().getData(e.getEntity()).incrementDeaths();
        //Remaining Players Message
        if (game.getPlayers().size() > 1) {
            Messaging.broadcast(Lang.Broadcasts.REMAINING_PLAYERS, game.getPlayers().size());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        e.setCancelled(game.isSpecator(e.getPlayer()) || !game.getState().canPvP());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        //Handle Kit Selector Item
        if (game.getState() == GameState.PREGAME && e.getItem() != null) {
            if (e.getMaterial() == Material.BOW) {
                e.setCancelled(true);
                MenuManager.KIT_MENU.openMenu(e.getPlayer());
            }
            //Handle Spectator Kit Items
        } else if (game.isSpecator(e.getPlayer()) && e.getItem() != null) {
            if (e.getMaterial() == Material.COMPASS) {
                MenuManager.SPECTATOR_MENU.openMenu(e.getPlayer());
            } else if (e.getMaterial() == Material.NETHER_STAR) {
                plugin.getBungeeManager().kickToLobby(e.getPlayer());
            }
        }
        //Handle Random Chest Opening
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getMaterial() == Material.CHEST) {
            e.setCancelled(game.getState() != GameState.INGAME || !game.isPlayer(e.getPlayer()));
            if (!e.isCancelled() && !game.isChestOpened(e.getClickedBlock())) {
                game.loadChest((Chest) e.getClickedBlock().getState());
                game.setChestOpened(e.getClickedBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerUtil.reset(e.getPlayer(), true, true);
        if (game.getState() == GameState.WAITING && game.canStart()) {
            game.setState(GameState.STARTING);
        }
        plugin.getDataManager().loadData(e.getPlayer());
        e.setJoinMessage(null);
        Messaging.broadcast(Lang.Broadcasts.JOIN_MESSAGE, e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (!game.getState().isJoinable() && !e.getPlayer().isOp()) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, StringUtil.color(Lang.Messages.CANNOT_JOIN.toString()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerUtil.reset(e.getPlayer(), true, true);
        if (game.isPlayer(e.getPlayer())) {
            game.removePlayer(e.getPlayer());
        }
        plugin.getDataManager().saveData(e.getPlayer());
        e.setQuitMessage(null);
        Messaging.broadcast(Lang.Broadcasts.QUIT_MESSAGE, e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (game.getState() != GameState.PREGAME || !game.isPlayer(e.getPlayer())) {
            return;
        }
        e.setCancelled(true);
    }
}

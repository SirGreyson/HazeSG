/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.data;

import net.hazeservers.sg.Messaging;
import net.hazeservers.sg.SurvivalGames;
import net.hazeservers.sg.config.Settings;
import net.hazeservers.sg.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private SurvivalGames plugin;
    private Map<UUID, PlayerData> loadedData;

    private Connection connection;

    public DataManager(SurvivalGames plugin) {
        this.plugin = plugin;
        this.loadedData = new HashMap<>();
    }

    public void closeAndSave() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            saveData(player);
        }
        closeConnection();
        Messaging.printInfo("Successfully saved all PlayerData!");
    }

    public void loadData(Player player) {
        try {
            ResultSet res = querySQL("SELECT * FROM player_data WHERE player_id = '" + player.getUniqueId().toString() + "';");
            //PlayerData does not exist in database
            if (!res.next()) {
                loadedData.put(player.getUniqueId(), new PlayerData(player));
                //PlayerData exists and can be loaded from database
            } else {
                loadedData.put(player.getUniqueId(), new PlayerData(player, res.getInt("kills"), res.getInt("deaths"), res.getInt("wins"), res.getBoolean("showBlood")));
            }
        } catch (SQLException e) {
            Messaging.printErr("Error! Could not load PlayerData!", e.getMessage());
        }
    }

    public void saveData(Player player) {
        PlayerData data = loadedData.get(player.getUniqueId());
        //PlayerData not loaded on the server
        if (data == null) {
            Messaging.printErr("Error! Could not save PlayerData because it was null!", player.getName());
            return;
        }
        //PlayerData is loaded on the server
        try {
            Object[] args = data.serialize();
            updateSQL("INSERT INTO player_data (player_id, lastName, kills, deaths, wins, showBlood) VALUES (" +
                    "'" + args[0] + "', " +
                    "'" + args[1] + "', " +
                    "'" + args[2] + "', " +
                    "'" + args[3] + "', " +
                    "'" + args[4] + "', " +
                    "'" + args[5] + "') " +
                    "ON DUPLICATE KEY UPDATE " +
                    "lastName = VALUES(lastName), kills = VALUES(kills), deaths = VALUES(deaths), wins = VALUES(wins), showBlood = VALUES(showBlood);");
            loadedData.remove(player.getUniqueId());
        } catch (SQLException e) {
            Messaging.printErr("Error! Could not save PlayerData!", e.getMessage());
        }
    }

    public PlayerData getData(Player player) {
        return loadedData.get(player.getUniqueId());
    }

    public String getData(String playerName) {
        try {
            ResultSet res = querySQL("SELECT * FROM player_data WHERE lastName = '" + playerName + "';");
            //PlayerData exists for this Player
            if (res.next()) {
                StringBuilder sb = new StringBuilder("&bStats for Player: &6" + playerName);
                sb.append("\n&bKills: &6" + res.getInt("kills"));
                sb.append("\n&bDeaths: &6" + res.getInt("deaths"));
                sb.append("\n&bWins: &6" + res.getInt("wins"));
                return sb.toString();
            }
        } catch (SQLException e) {
            Messaging.printErr("Error! Could not get PlayerData!", e.getMessage());
        }
        return null;
    }

    public int getKitLevel(Player player, Kit kit) {
        try {
            ResultSet res = querySQL("SELECT * FROM player_kits WHERE player_id = '" + player.getUniqueId().toString() + "';");
            if (res.next()) {
                return res.getInt(kit.getKitID());
            }
        } catch (SQLException e) {
            Messaging.printErr("Error! Could not get upgrade level!", e.getMessage());
        }
        return 0;
    }

    //MySQL Database Management

    public boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public Connection openConnection() {
        try {
            if (checkConnection()) {
                return connection;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            Settings.MySQL.HOST.toString() + ":" +
                            Settings.MySQL.PORT.toString() + "/" +
                            Settings.MySQL.DATABASE.toString(),
                    Settings.MySQL.USERNAME.toString(), Settings.MySQL.PASSWORD.toString());
            //Create PlayerData Table in Schema
            updateSQL("CREATE TABLE IF NOT EXISTS " + Settings.MySQL.DATABASE.toString() + ".player_data (" +
                    "player_id CHAR(36) NOT NULL, " +
                    "lastName VARCHAR(16) NOT NULL, " +
                    "kills INT NULL DEFAULT 0, " +
                    "deaths INT NULL DEFAULT 0, " +
                    "wins INT NULL DEFAULT 0, " +
                    "showBlood INT NULL DEFAULT 1, " +
                    "PRIMARY KEY (player_id));");
            Messaging.printInfo("Successfully connected to MySQL database!");
        } catch (SQLException e) {
            Messaging.printErr("Error! Could not open MySQL Connection!", e.getMessage());
        } catch (ClassNotFoundException e) {
            Messaging.printErr("Error! MySQL Driver not found! Please install MySQL on this machine!");
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (!checkConnection()) {
                return;
            }
            connection.close();
            connection = null;
        } catch (SQLException e) {
            Messaging.printErr("Error! Could not close MySQL Connection!", e.getMessage());
        }
    }

    public ResultSet querySQL(String query) throws SQLException {
        if (!checkConnection()) {
            openConnection();
        }
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public int updateSQL(String update) throws SQLException {
        if (!checkConnection()) {
            openConnection();
        }
        Statement statement = connection.createStatement();
        return statement.executeUpdate(update);
    }
}

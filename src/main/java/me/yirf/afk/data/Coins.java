package me.yirf.afk.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class Coins {

    private Connection connection;

    public Coins(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
            CREATE TABLE IF NOT EXISTS players (
                uuid TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                coins INTEGER DEFAULT 0
            )
        """);
        }
    }

    public void registerPlayer(OfflinePlayer p) throws SQLException {
        if (!playerExists(p.getUniqueId())) {
            try (PreparedStatement ps = connection.prepareStatement(("INSERT INTO players (uuid, username) VALUES(?, ?)"))){
                ps.setString(1, p.getUniqueId().toString());
                ps.setString(2, p.getName());
                ps.executeUpdate();
            }
        }
    }

    public boolean playerExists(UUID uuid) throws SQLException {
        String query = "SELECT 1 FROM players WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void registerPlayer(UUID uuid, String username) throws SQLException {
        if (uuid != null && !playerExists(uuid)) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO players (uuid, username) VALUES (?, ?)")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.executeUpdate();
            }
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void setCoins(OfflinePlayer p, int amount) throws SQLException {
        if (!playerExists(p.getUniqueId())){
            registerPlayer(p);
        } else {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET coins = ? WHERE uuid = ?")){
                ps.setInt(1, amount);
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        }
    }

    public int getCoins(OfflinePlayer p) throws SQLException {
        if (playerExists(p.getUniqueId())) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT coins FROM players WHERE uuid = ?")) {
                ps.setString(1, p.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("coins");
                    }
                }
            }
        }
        return 0;
    }
}
package me.yirf.afk.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.yirf.afk.Afk;
import me.yirf.afk.data.Coins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.sql.SQLException;

public class AfkExpansion extends PlaceholderExpansion {

    private final Afk plugin;
    private final Coins coins;

    public AfkExpansion(Afk plugin, Coins coins) {
        this.plugin = plugin;
        this.coins = coins;
    }

    @Override
    public String getAuthor() {
        return "Yirf";
    }
    @Override
    public String getIdentifier() {
        return "afk";
    }
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("coins")) {
            return "" + getCoins(player);
        }
        return null;
    }

    private int getCoins(OfflinePlayer target) {
        try {
            if (!coins.playerExists(target.getUniqueId())) {
                return 0;
            }
            int bal = coins.getCoins(target);
            return bal;
        } catch (SQLException ex) {
            Bukkit.getLogger().warning((ChatColor.RED + "An error occurred while fetching coins"));
            ex.printStackTrace();
        }
        return 0;
    }
}
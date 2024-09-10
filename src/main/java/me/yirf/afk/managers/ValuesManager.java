package me.yirf.afk.managers;

import java.util.Map;
import java.util.Objects;

import me.yirf.afk.Afk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ValuesManager {
    public static World globalWorld;
    public static Location loc1;
    public static Location loc2;

    public static void load() {
        FileConfiguration config = Afk.instance.getConfig();
        ValuesManager.loadWorldAndLocations(config);
        Afk.instance.getLogger().info("Region values loaded");
    }

    private static void loadWorldAndLocations(Configuration config) {
        String worldName = config.getString("region.locations.world");
        globalWorld = Objects.requireNonNull(Bukkit.getWorld(worldName), "World not found: " + worldName);
        loc1 = ValuesManager.loadLocation(config, "region.locations.loc1");
        loc2 = ValuesManager.loadLocation(config, "region.locations.loc2");
    }

    private static Location loadLocation(Configuration config, String path) {
        return new Location(globalWorld, config.getDouble(path + ".x"), config.getDouble(path + ".y"), config.getDouble(path + ".z"));
    }

}
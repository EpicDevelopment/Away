package me.yirf.afk.data;

import me.yirf.afk.Afk;
import me.yirf.afk.Interface.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Config implements Color {

    private Afk afk;
    private FileConfiguration config;
    private File configFile;
    private Color translate = new Color(){};

    public Config(Afk afk) {
        this.afk = afk;
        this.config = afk.getConfigYaml();
        this.configFile = afk.getConfigFile();
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public Set<String> getNodes() {
        return config.getKeys(false);
    }

    public List<String> getStringList(String path) {
        List<String> list = (List<String>) config.getList(path);
        assert list != null;
        list = list.stream()
                .map(translate::format)
                .collect(Collectors.toList());
        return list;
    }

    public Location getTeleport() {
        Double x = config.getDouble("warp.x");
        Double y = config.getDouble("warp.y");
        Double z = config.getDouble("warp.z");
        float pitch = (float) config.getDouble("warp.pitch", 0.0);
        float yaw = (float) config.getDouble("warp.yaw", 0.0);
        String wrld = config.getString("warp.world");
        World world = (wrld != null) ? Bukkit.getWorld(wrld) : Bukkit.getWorld("world");
        if (world == null) {
            Bukkit.getLogger().severe("Unable to find proper world for afk warp in config.yml");
            return null;
        }

        Location loc = new Location(world, x, y, z, yaw, pitch);
        return loc;
    }

    public void reload() {
        if (configFile == null) {
            Bukkit.getLogger().warning("Unable to find config.yml in path 'plugins/afk/' remaking it...");
            configFile = new File(afk.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

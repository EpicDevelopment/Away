package me.yirf.afk;

import me.yirf.afk.commands.Admin;
import me.yirf.afk.commands.ShopCommand;
import me.yirf.afk.commands.Teleport;
import me.yirf.afk.data.*;
import me.yirf.afk.gui.Shop;
import me.yirf.afk.listeners.Join;
import me.yirf.afk.listeners.Quit;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public final class Afk extends JavaPlugin {

    private File configFile = new File(getDataFolder(), "config.yml");
    private FileConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);
    private File messagesFile = new File(getDataFolder(), "messages.yml");
    private FileConfiguration messagesYaml = YamlConfiguration.loadConfiguration(messagesFile);
    private File shopperFile = new File(getDataFolder(), "shop.yml");
    private FileConfiguration shopperYaml = YamlConfiguration.loadConfiguration(messagesFile);
    private Coins coins;

    @Override
    public void onEnable() {
        Config config = new Config(this);
        Messages messages = new Messages(this);
        Shopper shopper = new Shopper(this);
        Group group = new Group(config);
        Shop shop = new Shop(this, config, shopper);
        shop.buildShop();

        loadData();
        loadCommands(config, messages, shopper);
        loadListeners(group);
    }

    @Override
    public void onDisable() {
        if (coins != null) {
            try {
                coins.close();
                getLogger().info("coins.db closed successfully.");
            } catch (SQLException e) {
                getLogger().severe("Failed to close coins.db: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadData() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        if (!shopperFile.exists()) {
            saveResource("shop.yml", false);
        }

        try {
            String dbPath = new File(getDataFolder(), "coins.db").getAbsolutePath();
            coins = new Coins(dbPath);
            getLogger().info("coins.db initialized successfully.");
        } catch (SQLException e) {
            getLogger().severe("coins.db initialization failed: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;  // Exit the method early if database initialization fails
        }
    }

    private void loadCommands(Config config, Messages messages, Shopper shopper) {
        this.getCommand("afk").setExecutor(new Teleport(config));
        this.getCommand("away").setExecutor(new Admin(coins, config, messages));
        this.getCommand("afkshop").setExecutor(new ShopCommand(this, config, shopper));
    }

    private void loadListeners(Group group) {
        this.getServer().getPluginManager().registerEvents(new Join(coins), this);
        this.getServer().getPluginManager().registerEvents(new Quit(coins, group), this);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
                this::run,
                TimeUnit.SECONDS.toSeconds(30) * 20,
                TimeUnit.SECONDS.toSeconds(30) * 20
                );
    }

    public void run() {
        Bukkit.broadcastMessage("test sched");
    }

    public FileConfiguration getConfigYaml() {
        return configYaml;
    }

    public File getConfigFile() {
        return configFile;
    }

    public FileConfiguration getMessagesYaml() {
        return messagesYaml;
    }

    public File getMessagesFile() {
        return messagesFile;
    }
}

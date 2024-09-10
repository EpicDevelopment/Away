package me.yirf.afk;

import com.samjakob.spigui.SpiGUI;
import me.yirf.afk.events.custom.Listener.PlayerRegionHandler;
import me.yirf.afk.events.custom.Listener.RegionRelatedEventsHandler;
import me.yirf.afk.commands.Admin;
import me.yirf.afk.commands.ShopCommand;
import me.yirf.afk.commands.Teleport;
import me.yirf.afk.data.*;
import me.yirf.afk.gui.Shop;
import me.yirf.afk.listeners.Join;
import me.yirf.afk.listeners.Quit;
import me.yirf.afk.managers.ValuesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public final class Afk extends JavaPlugin {

    public static SpiGUI spiGUI;
    public static Afk instance;

    File configFile = new File(getDataFolder(), "config.yml");
    FileConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);
    File messagesFile = new File(getDataFolder(), "messages.yml");
    FileConfiguration messagesYaml = YamlConfiguration.loadConfiguration(messagesFile);
    File shopperFile = new File(getDataFolder(), "shop.yml");
    FileConfiguration shopperYaml = YamlConfiguration.loadConfiguration(shopperFile);
    Coins coins;
    Group group;

    @Override
    public void onEnable() {
        instance = this;
        spiGUI = new SpiGUI(this);
        Config config = new Config(this);
        Messages messages = new Messages(this);
        Shopper shopper = new Shopper(this);
        group = new Group(config);
        Shop shop = new Shop(this, config, shopper, coins, messages);

        loadData();
        loadCommands(config, messages, shopper, coins);
        loadListeners(group, config);
        ValuesManager.load();
        schedule();
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

    private void loadCommands(Config config, Messages messages, Shopper shopper, Coins coins) {
        this.getCommand("afk").setExecutor(new Teleport(config));
        this.getCommand("away").setExecutor(new Admin(coins, config, messages, shopper));
        this.getCommand("afkshop").setExecutor(new ShopCommand(this, config, shopper, coins, messages));
    }

    private void loadListeners(Group group, Config config) {
        this.getServer().getPluginManager().registerEvents(new Join(coins), this);
        this.getServer().getPluginManager().registerEvents(new Quit(coins, group), this);
        getServer().getPluginManager().registerEvents(new PlayerRegionHandler(group), this);
        getServer().getPluginManager().registerEvents(new RegionRelatedEventsHandler(group), this);
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
                this::run,
                TimeUnit.SECONDS.toSeconds(5) * 20,
                TimeUnit.SECONDS.toSeconds(5) * 20
                );
    }

    public void run() {
        Bukkit.broadcastMessage("test sched");
        Bukkit.broadcastMessage(group.group.toString() + " group");
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

    public File getShopperFile(){
        return shopperFile;
    }

    public FileConfiguration getShopperYaml(){
        return shopperYaml;
    }
}

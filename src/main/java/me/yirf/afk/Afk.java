package me.yirf.afk;

import com.samjakob.spigui.SpiGUI;
import me.yirf.afk.api.AfkExpansion;
import me.yirf.afk.commands.Admin;
import me.yirf.afk.commands.ShopCommand;
import me.yirf.afk.commands.Teleport;
import me.yirf.afk.data.*;
import me.yirf.afk.gui.Shop;
import me.yirf.afk.listeners.Join;
import me.yirf.afk.listeners.Quit;
import me.yirf.afk.listeners.onMoveEvent;
import me.yirf.afk.utils.TimeUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Afk extends JavaPlugin {

    public static SpiGUI spiGUI;
    public static Afk instance;
    public static Location rgLoc1;
    public static Location rgLoc2;

    File configFile = new File(getDataFolder(), "config.yml");
    FileConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);
    File messagesFile = new File(getDataFolder(), "messages.yml");
    FileConfiguration messagesYaml = YamlConfiguration.loadConfiguration(messagesFile);
    File shopperFile = new File(getDataFolder(), "shop.yml");
    FileConfiguration shopperYaml = YamlConfiguration.loadConfiguration(shopperFile);
    Coins coins;
    Group group;
    Messages messages;
    Config config;

    @Override
    public void onEnable() {
        instance = this;
        spiGUI = new SpiGUI(this);
        config = new Config(this);
        messages = new Messages(this);
        group = new Group(config);
        Shopper shopper = new Shopper(this);
        Shop shop = new Shop(this, config, shopper, coins, messages);
        rgLoc1 = config.buildLocation("region.locations.loc1");
        rgLoc2 = config.buildLocation("region.locations.loc2");

        activate();
        loadData();
        loadCommands(shopper, coins);
        loadListeners();
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

    public void activate() {
        String depend = "";
        if (!Bukkit.getPluginManager().isPluginEnabled(("PlaceholderAPI"))) {
            depend = "PlaceholderAPI";
        }
        if (!depend.isEmpty()) {
            getLogger().warning("------------------------------------------------");
            getLogger().warning("| Missing dependencies:");
            getLogger().warning("| " + depend);
            getLogger().warning("|");
            getLogger().warning("| Plugin will load but placeholders are disabled.");
            getLogger().warning("| Download Papi: https://www.spigotmc.org/resources/placeholderapi.6245/");
            getLogger().warning("------------------------------------------------");
            return;
        }
        new AfkExpansion(this, coins);
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

    private void loadCommands(Shopper shopper, Coins coins) {
        this.getCommand("afk").setExecutor(new Teleport(config));
        this.getCommand("away").setExecutor(new Admin(coins, config, messages, shopper));
        this.getCommand("afkshop").setExecutor(new ShopCommand(this, config, shopper, coins, messages));
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new Join(coins), this);
        this.getServer().getPluginManager().registerEvents(new Quit(coins, group), this);
        this.getServer().getPluginManager().registerEvents(new onMoveEvent(messages, group), this);
    }

    public void schedule() {
        //int timer = configYaml.getInt("timer");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
                this::run,
                20,
                20
                );
    }

    public void run() {
        group.group.forEach(this::checkAfk);
    }

    private void checkAfk(UUID uuid, Long millis) {
        Player player = getServer().getPlayer(uuid);
        if (millis - System.currentTimeMillis() < 1) {
            group.removePlayer(player);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(messages.getString("inafk.reward")));
            getServer().dispatchCommand(Bukkit.getConsoleSender(), config.getString("reward-command").replaceAll("%player%", player.getName()));
            group.addPlayer(player);
            return;
        }
        String timed = TimeUtil.millisToString(millis);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(messages.getString("inafk.message").replaceAll("%time%", timed)));
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

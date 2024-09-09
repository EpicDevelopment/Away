package me.yirf.afk.data;

import me.yirf.afk.Afk;
import me.yirf.afk.Interface.Color;
import me.yirf.afk.gui.manager.ClickType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Shopper implements Color {

    private Afk afk;
    private FileConfiguration shopper;
    private File shopperFile;
    private final Color translate = new Color(){};

    public Shopper(Afk afk) {
        this.afk = afk;
        this.shopper = afk.getMessagesYaml();
        this.shopperFile = afk.getMessagesFile();
    }

    public String getString(String path) {
        return translate.format(shopper.getString(path));
    }

    public int getInt(String path) {
        return shopper.getInt(path);
    }

    public List<String> getStringList(String path) {
        List<String> list = (List<String>) shopper.getList(path);
        assert list != null;
        list = list.stream()
                .map(translate::format)
                .collect(Collectors.toList());
        return list;
    }

    public ClickType getType(String path) {
        String type = shopper.getString(path);
        return ClickType.fromString(type);

    }


    public void reload() {
        if (shopperFile == null) {
            shopperFile = new File(afk.getDataFolder(), "messages.yml");
        }
        shopper = YamlConfiguration.loadConfiguration(shopperFile);
    }

    public void save() {
        try {
            shopper.save(shopperFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

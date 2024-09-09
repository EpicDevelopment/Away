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
import java.util.stream.Collectors;

public class Messages {

    private Afk afk;
    private FileConfiguration messages;
    private File messagesFile;
    private final Color translate = new Color(){};

    public Messages(Afk afk) {
        this.afk = afk;
        this.messages = afk.getMessagesYaml();
        this.messagesFile = afk.getMessagesFile();
    }

    public String getString(String path) {
        return translate.format(messages.getString(path));
    }
    public int getInt(String path) {
        return messages.getInt(path);
    }

    public List<String> getStringList(String path) {
        List<String> list = (List<String>) messages.getList(path);
        assert list != null;
        list = list.stream()
                .map(translate::format)
                .collect(Collectors.toList());
        return list;
    }


    public void reload() {
        if (messagesFile == null) {
            messagesFile = new File(afk.getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void save() {
        try {
            messages.save(messagesFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

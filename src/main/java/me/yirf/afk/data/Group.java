package me.yirf.afk.data;

import me.yirf.afk.Afk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Group {

    public HashMap<UUID, Long> group = new HashMap<>();

    private Config config;

    public Group(Config config) {
        this.config = config;
    }

    public  void addPlayer(Player p) {
        long time = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(config.getInt("timer"));
        group.put(p.getUniqueId(), time);
    }

    public void removePlayer(Player p) {
        group.remove(p);
    }
}

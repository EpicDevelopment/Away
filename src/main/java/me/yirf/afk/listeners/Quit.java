package me.yirf.afk.listeners;

import me.yirf.afk.data.Coins;
import me.yirf.afk.data.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {
    private final Coins sqLite;
    private Group group;

    public Quit(Coins sqLite, Group group) {
        this.sqLite = sqLite;
        this.group = group;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        group.removePlayer(player);
    }
}

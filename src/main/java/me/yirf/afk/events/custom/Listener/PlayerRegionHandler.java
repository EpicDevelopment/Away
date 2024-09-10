package me.yirf.afk.events.custom.Listener;

import me.yirf.afk.data.Group;
import me.yirf.afk.events.RegionEnter;
import me.yirf.afk.events.RegionLeft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerRegionHandler
        implements Listener {

    Group group;

    public PlayerRegionHandler(Group group) {
        this.group = group;
    }

    @EventHandler
    public void onRegionEnter(RegionEnter event) {
        Player player = event.getPlayer();
        Bukkit.broadcastMessage("entered region>p");
        group.addPlayer(player);
    }

    @EventHandler
    public void onRegionLeft(RegionLeft event) {
        Player player = event.getPlayer();
        Bukkit.broadcastMessage("exited region >p");
        group.removePlayer(player);
    }

}

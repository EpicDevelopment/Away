package me.yirf.afk.listeners;

import me.yirf.afk.Afk;
import me.yirf.afk.data.Group;
import me.yirf.afk.data.Messages;
import me.yirf.afk.utils.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMoveEvent implements Listener {

    Messages messages;
    Group group;

    public onMoveEvent(Messages messages, Group group) {
        this.messages = messages;
        this.group = group;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (p.getWorld() != Afk.rgLoc1.getWorld()) {return;}

        if (RegionUtils.playerInCubiod(p.getLocation())) {
            if (!group.group.containsKey(p.getUniqueId())) {
                p.sendMessage(messages.getString("region.enter"));
                group.addPlayer(p);
                return;
            }
            return;
        }
        if (!RegionUtils.playerInCubiod(p.getLocation())) {
            if (group.group.containsKey(p.getUniqueId())) {
                p.sendMessage(messages.getString("region.leave"));
                group.group.remove(p.getUniqueId());
            }
        }
    }
}

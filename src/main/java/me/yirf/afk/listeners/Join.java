package me.yirf.afk.listeners;

import me.yirf.afk.data.Coins;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {
    private final Coins sqLite;

    public Join(Coins sqLite) {
        this.sqLite = sqLite;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        try {
            if (!sqLite.playerExists(player.getUniqueId())) {
                sqLite.registerPlayer(player);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

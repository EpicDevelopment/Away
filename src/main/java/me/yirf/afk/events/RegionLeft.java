package me.yirf.afk.events;

import me.yirf.afk.enums.CurrentMove;
import me.yirf.afk.events.custom.RegionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

public class RegionLeft
        extends RegionEvent
        implements Cancellable {
    private boolean cancelled = false;
    private boolean cancellable = true;

    public RegionLeft(Player player, CurrentMove movement, PlayerEvent parent) {
        super(player, movement, parent);
        Bukkit.broadcastMessage("exit region >r");
        if (movement == CurrentMove.SPAWN || movement == CurrentMove.DISCONNECT) {
            this.cancellable = false;
        }
    }

    public void setCancelled(boolean cancelled) {
        if (!this.cancellable) {
            return;
        }
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}

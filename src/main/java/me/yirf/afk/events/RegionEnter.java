package me.yirf.afk.events;

import me.yirf.afk.enums.CurrentMove;
import me.yirf.afk.events.custom.RegionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class RegionEnter
        extends RegionEvent
        implements Cancellable {
    private boolean cancelled = false;
    private boolean cancellable = true;

    public RegionEnter(@NotNull Player player, CurrentMove movementWay, PlayerEvent parent) {
        super(player, movementWay, parent);
        Bukkit.broadcastMessage("enter region >r");
        if (movementWay == CurrentMove.SPAWN || movementWay == CurrentMove.DISCONNECT) {
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

package me.yirf.afk.events.custom;

import me.yirf.afk.enums.CurrentMove;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class RegionEvent
        extends PlayerEvent {
    private static HandlerList handlerList = new HandlerList();
    private CurrentMove movementWay;
    private PlayerEvent parentEvent;

    public RegionEvent(@NotNull Player player, CurrentMove movementWay, PlayerEvent parent) {
        super(player);
        this.movementWay = movementWay;
        this.parentEvent = parent;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public CurrentMove getMovementWay() {
        return this.movementWay;
    }

    public PlayerEvent getParentEvent() {
        return this.parentEvent;
    }
}

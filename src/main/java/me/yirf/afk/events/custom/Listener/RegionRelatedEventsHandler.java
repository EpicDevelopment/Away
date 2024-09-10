package me.yirf.afk.events.custom.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import me.yirf.afk.Afk;
import me.yirf.afk.data.Group;
import me.yirf.afk.enums.CurrentMove;
import me.yirf.afk.events.RegionEnter;
import me.yirf.afk.events.RegionLeft;
import me.yirf.afk.managers.ValuesManager;
import me.yirf.afk.thread.Tasks;
import me.yirf.afk.utils.RegionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RegionRelatedEventsHandler
        implements Listener {
    private static final long MOVEMENT_THROTTLE_DELAY = 1000L;
    private final Map<UUID, Long> lastMovementTimes = new ConcurrentHashMap<UUID, Long>();
    Group group;

    public RegionRelatedEventsHandler(Group group) {
        this.group = group;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.isPlayerInRegion(player.getLocation())) {
            this.callRegionLeftEvent(player, CurrentMove.DISCONNECT, event);
        } else {
            group.removePlayer(player);
        }
        this.lastMovementTimes.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        long currentTime;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.shouldThrottleMovement(uuid, currentTime = System.currentTimeMillis())) {
            return;
        }
        this.updateRegions(player, CurrentMove.MOVE, event.getTo(), event);
        this.lastMovementTimes.put(uuid, currentTime);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.updateRegions(event.getPlayer(), CurrentMove.TELEPORT, event.getTo(), event);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.updateRegions(event.getPlayer(), CurrentMove.SPAWN, event.getPlayer().getLocation(), event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.updateRegions(event.getPlayer(), CurrentMove.SPAWN, event.getRespawnLocation(), event);
    }

    private void updateRegions(Player player, CurrentMove movementWay, Location to, PlayerEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!this.isPlayerInRegion(to)) {
                this.handlePlayerLeaveRegion(player, movementWay, event);
            } else {
                this.handlePlayerEnterRegion(player, movementWay, event);
            }
        });
    }

    private boolean isPlayerInRegion(Location location) {
        return RegionUtils.playerInCubiod(location, ValuesManager.loc1, ValuesManager.loc2);
    }

    private void handlePlayerLeaveRegion(Player player, CurrentMove movementWay, PlayerEvent event) {
        if (group.group.containsKey(player.getUniqueId())) {
            this.callRegionLeftEvent(player, movementWay, event);
        }
    }

    private void handlePlayerEnterRegion(Player player, CurrentMove movementWay, PlayerEvent event) {
        if (!group.group.containsKey(player.getUniqueId())) {
            this.callRegionEnteredEvent(player, movementWay, event);
        }
    }

    private void callRegionLeftEvent(Player player, CurrentMove movementWay, PlayerEvent event) {
        RegionLeft regionLeftEvent = new RegionLeft(player, movementWay, event);
        Tasks.sync(() -> Afk.instance.getServer().getPluginManager().callEvent(regionLeftEvent));
    }

    private void callRegionEnteredEvent(Player player, CurrentMove movementWay, PlayerEvent event) {
        RegionEnter regionEnteredEvent = new RegionEnter(player, movementWay, event);
        Tasks.sync(() -> Afk.instance.getServer().getPluginManager().callEvent(regionEnteredEvent));
    }

    private boolean shouldThrottleMovement(UUID uuid, long currentTime) {
        Long lastMovementTime = this.lastMovementTimes.get(uuid);
        return lastMovementTime != null && currentTime - lastMovementTime < 1000L;
    }
}
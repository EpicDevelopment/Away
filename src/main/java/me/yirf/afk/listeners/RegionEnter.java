package me.yirf.afk.listeners;

import com.mewin.WGRegionEvents.MovementWay;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.yirf.afk.data.Config;
import me.yirf.afk.data.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

public class RegionEnter extends RegionEnterEvent implements Cancellable {

    private Config config;
    private Group group;

    public RegionEnter(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent, Config config, Group group) {
        super(region, player, movement, parent);
        this.config = config;

        if (region.getId().equalsIgnoreCase(config.getString("region"))) {
            group.addPlayer(player);
        }
    }

}

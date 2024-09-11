package me.yirf.afk.utils;

import me.yirf.afk.Afk;
import org.bukkit.Location;

public class RegionUtils {
    static Location loc1 = Afk.rgLoc1;
    static Location loc2 = Afk.rgLoc2;

    public static boolean playerInCubiod(Location location) {
        if (loc1 == null || loc2 == null) {
            return false;
        }
        if (loc1.getWorld() != loc2.getWorld()) {
            return false;
        }
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int px = location.getBlockX();
        int py = location.getBlockY();
        int pz = location.getBlockZ();
        return loc1.getWorld() == location.getWorld() && px >= x1 && px <= x2 && py >= y1 && py <= y2 && pz >= z1 && pz <= z2;
    }
}

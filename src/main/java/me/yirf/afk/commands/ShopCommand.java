package me.yirf.afk.commands;

import me.yirf.afk.Afk;
import me.yirf.afk.data.Config;
import me.yirf.afk.data.Shopper;
import me.yirf.afk.gui.Shop;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ShopCommand implements CommandExecutor {

    private Config config;
    private Shopper shopper;
    private Afk afk;
    private Shop shop;


    public ShopCommand(Afk afk, Config config, Shopper shopper) {
        this.afk = afk;
        this.config = config;
        this.shopper = shopper;
        shop = new Shop(afk, config, shopper);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        Inventory shopGui = shop.getShop();
        Bukkit.broadcastMessage(shopGui.toString() + "nigga2");
        try {
            player.openInventory(shopGui);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}

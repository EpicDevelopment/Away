package me.yirf.afk.commands;

import me.yirf.afk.Afk;
import me.yirf.afk.data.Coins;
import me.yirf.afk.data.Config;
import me.yirf.afk.data.Messages;
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

    Config config;
    Shopper shopper;
    Afk afk;
    Shop shop;
    Coins coins;
    Messages messages;


    public ShopCommand(Afk afk, Config config, Shopper shopper, Coins coins, Messages messages) {
        this.afk = afk;
        this.config = config;
        this.shopper = shopper;
        this.coins = coins;
        this.messages = messages;
        shop = new Shop(afk, config, shopper, coins, messages);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;;
        try {
            shop.openShop(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}

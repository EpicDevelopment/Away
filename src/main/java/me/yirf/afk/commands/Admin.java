package me.yirf.afk.commands;

import me.yirf.afk.data.Coins;
import me.yirf.afk.data.Config;
import me.yirf.afk.data.Messages;
import me.yirf.afk.data.Shopper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Admin implements TabExecutor {

    Coins coins;
    Config config;
    Messages messages;
    Shopper shopper;

    public Admin(Coins coins, Config config, Messages messages, Shopper shopper) {
        this.coins = coins;
        this.config = config;
        this.messages = messages;
        this.shopper = shopper;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {


        if (args.length == 0){
            sendUsage(commandSender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        OfflinePlayer target;
        int amount = 0;

        switch (subCommand) {
            case "reload":
                long start = System.currentTimeMillis();
                config.reload();
                messages.reload();
                shopper.reload();
                long end = System.currentTimeMillis();
                long duration = end - start;
                commandSender.sendMessage(ChatColor.GREEN + "All configs reloaded in " + ChatColor.UNDERLINE + ChatColor.DARK_GREEN + duration + "ms");
                break;
            case "getcoins":
                if (args.length != 2) {
                    sendUsage(commandSender);
                    return true;
                }
                target = Bukkit.getOfflinePlayer(args[1]);
                getCoins(commandSender, target);
                break;

            case "setcoins":
            case "addcoins":
                if (args.length != 3) {
                    sendUsage(commandSender);
                    return true;
                }
                try {
                    amount = Integer.parseInt(args[1]);
                    if (amount < 1) {
                        commandSender.sendMessage(ChatColor.RED + "You can only use positive numbers");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + "Invalid amount");
                    return true;
                }
                target = Bukkit.getOfflinePlayer(args[2]);
                if (subCommand.equals("setcoins")) {
                    setCoins(commandSender, target, amount);
                } else {
                    addCoins(commandSender, target, amount);
                }
                break;

            default:
                sendUsage(commandSender);
                return true;
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "AWAY HELP");
        sender.sendMessage(ChatColor.RED + "/away <setCoins/addCoins> <amount> <player>");
        sender.sendMessage(ChatColor.RED + "/away <getCoins> <player>");
        sender.sendMessage(ChatColor.RED + "/away <reload>");
    }

    private void getCoins(CommandSender sender, OfflinePlayer target) {
        try {
            if (!coins.playerExists(target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return;
            }
            int bal = coins.getCoins(target);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has " + bal + " coins");
        } catch (SQLException ex) {
            sender.sendMessage(ChatColor.RED + "An error occurred while fetching coins");
            ex.printStackTrace();
        }
    }

    private void setCoins(CommandSender sender, OfflinePlayer target, int amount) {
        try {
            if (!coins.playerExists(target.getUniqueId())) {
                coins.registerPlayer(target);
            }
            coins.setCoins(target, amount);
            sender.sendMessage(ChatColor.GREEN + "You set " + target.getName() + "'s coins to " + amount + ".");
        } catch (SQLException ex) {
            sender.sendMessage(ChatColor.RED + "An error occurred while setting coins");
            ex.printStackTrace();
        }
    }

    private void addCoins(CommandSender sender, OfflinePlayer target, int amount) {
        try {
            if (!coins.playerExists(target.getUniqueId())) {
                coins.registerPlayer(target);
            }
            int newBalance = coins.getCoins(target) + amount;
            coins.setCoins(target, newBalance);
            sender.sendMessage(ChatColor.GREEN + "You added " + amount + " coins to " + target.getName() + "'s balance.");
        } catch (SQLException ex) {
            sender.sendMessage(ChatColor.RED + "An error occurred while adding coins");
            ex.printStackTrace();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> tab = new ArrayList<String>();
        if (args.length == 1) {
            tab.add("addcoins");
            tab.add("setcoins");
            tab.add("getcoins");
            tab.add("reload");
            tab.add("help");
            return tab;
        }

        switch (args[0]) {
            case ("getcoins"):
                for (Player p : Bukkit.getOnlinePlayers()) {
                    tab.add(p.getName());
                }
                return tab;
            case("setcoins"):
            case("addcoins"):
                if (args.length == 3) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        tab.add(player.getName());
                        return tab;
                    }
                }
                tab.add("<amount>");
                return tab;
        }
        return tab;
    }
}

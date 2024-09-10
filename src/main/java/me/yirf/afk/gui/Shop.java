package me.yirf.afk.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.yirf.afk.Afk;
import me.yirf.afk.data.Coins;
import me.yirf.afk.data.Config;
import me.yirf.afk.data.Messages;
import me.yirf.afk.data.Shopper;
import me.yirf.afk.enums.ClickType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.List;

import static me.yirf.afk.Afk.spiGUI;

public class Shop {

    Afk afk;
    Inventory afkShop;
    Config config;
    FileConfiguration configYaml;
    Shopper shopper;
    Coins coins;
    Messages messages;

    public Shop(Afk afk, Config config, Shopper shopper, Coins coins, Messages messages) {
        this.afk = afk;
        this.config = config;
        this.configYaml = afk.getConfigYaml();
        this.shopper = shopper;
        this.coins = coins;
        this.messages = messages;
    }

    private ItemStack buildItem(String pH) {
        Material material = Material.getMaterial(shopper.getString("shop.items." + pH + ".material").toUpperCase());
        String name = shopper.getString("shop.items." + pH + "name");
        List<String> lore = shopper.getStringList("shop.items." + pH + "name");
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        return item;
    }

    public void openShop(Player player) {

        String title = shopper.getString("shop.gui.title");
        List<String> guiConfig = shopper.getStringList("shop.gui.format");
        if (guiConfig == null) {
            afk.getLogger().warning("Unable to find shop.gui.format in shop.yml!");
            return;
        }
        if (title.isEmpty()) {
            afk.getLogger().warning("Unable to find shop.gui.title in shop.yml!");
            title = "Afk Shop";
        }

        int totalSlots = guiConfig.size() * 9;
        if (totalSlots % 9 != 0 || totalSlots / 9 > 6) {
            afk.getLogger().warning("Your gui format for shop.yml isn't proper.");
            return;
        }
        int rows = totalSlots / 9;
        SGMenu gui = spiGUI.create(title, rows);

        int totalLength = guiConfig.stream().mapToInt(String::length).sum();

        char[] slots = new char[totalLength];

        int index = 0;
        for (String s : guiConfig) {
            for (char c : s.toCharArray()) {
                slots[index] = c;
                index++;
            }
        }
        for (int i = 0; i < slots.length; i++) {
            char slotKey = slots[i];
            String path = "shop.items." + slotKey;
            ClickType clickType = ClickType.fromString(shopper.getString(path + ".type"));
            Material material = org.bukkit.Material.getMaterial(shopper.getString(path + ".material"));
            String name = shopper.getString(path + ".name");
            List<String> lore = shopper.getStringList(path + ".lore");
            int price = shopper.getInt(path + ".price");
            SGButton button;
            ItemStack item;
            if (material.isAir() == true) {
                item = new ItemBuilder(material).build();
            } else {
                item = new ItemBuilder(material)
                        .name(name)
                        .lore(lore)
                        .amount(shopper.getInt(path + ".amount"))
                        .build();
            }

            button = new SGButton(item);

            if (ClickType.fromString(shopper.getString(path + ".type")) != null) {
                button.withListener(inventoryClickEvent -> {

                    int bal = 0;
                    if (clickType.equals(ClickType.COMMAND) || clickType.equals(ClickType.ITEM)) {
                        try {
                            bal = coins.getCoins(afk.getServer().getOfflinePlayer(player.getUniqueId()));

                        } catch (SQLException ex) {
                            afk.getLogger().warning("Unable to get " + player.getName() + "'s coins!");
                            ex.printStackTrace();
                            return;
                        }
                    }
                    if (bal < price) {
                        player.sendMessage(messages.getString("shop.notEnough"));
                        return;
                    }

                    if (clickType.equals(ClickType.COMMAND)) {
                        player.sendMessage(messages.getString("shop.purchase").replaceAll("%price%", price + ""));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), shopper.getString(path + ".command").replace("{player}", player.getName()));
                        player.closeInventory();
                        removeCoins(player, price);
                        return;
                    }
                    if (clickType.equals(ClickType.ITEM)) {
                        player.sendMessage(messages.getString("shop.purchase").replaceAll("%price%", price + ""));
                        removeCoins(player, price);
                        player.getInventory().addItem(item);
                    }
                    else if (clickType.equals(ClickType.LEAVE)) {
                        player.closeInventory();
                        return;
                    }

                    String stringType = shopper.getString(path + ".type");
                    if (ClickType.fromString(stringType).equals(ClickType.COMMAND)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), shopper.getString(path + ".command").replace("{player}", player.getName()));
                    }
                });
            }
            gui.setButton(i, button);

            if (shopper.getString(path + ".type").equals("air")) {
                SGButton air = new SGButton(
                        new ItemBuilder(Material.AIR)
                                .build()
                );
                gui.setButton(i, air);
            }
        }
        player.openInventory(gui.getInventory());
    }

    private void removeCoins(Player player, int amount) {
        try {
            if (!coins.playerExists(player.getUniqueId())) {
                coins.registerPlayer(player);
            }
            int newBalance = coins.getCoins(player) - amount;
            coins.setCoins(player, newBalance);
        } catch (SQLException ex) {
            player.sendMessage(ChatColor.RED + "An error occurred while removing coins please report this!");
            ex.printStackTrace();
        }
    }
}


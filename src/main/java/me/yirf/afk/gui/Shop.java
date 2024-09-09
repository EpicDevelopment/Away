package me.yirf.afk.gui;

import me.yirf.afk.Afk;
import me.yirf.afk.Interface.Color;
import me.yirf.afk.data.Config;
import me.yirf.afk.data.Shopper;
import me.yirf.afk.gui.manager.ClickType;
import me.yirf.afk.gui.manager.GuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;

public class Shop{
    private Afk afk;
    private Inventory afkShop;
    private Config config;
    private FileConfiguration configYaml;
    private Shopper shopper;

    public Shop(Afk afk, Config config, Shopper shopper) {
        this.afk = afk;
        this.config = config;
        this.configYaml = afk.getConfigYaml();
        this.shopper = shopper;
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

    public void buildShop() {
        String title = shopper.getString("shop.gui.title");
        GuiBuilder guiBuilder = new GuiBuilder().setTitle(title);

        List<String> format = shopper.getStringList("shop.gui.format");
        for (String s : format) {
            guiBuilder.addRow(s);
        }
        ConfigurationSection sec = configYaml.getConfigurationSection("shop.items");
        Set<String> pH = sec.getKeys(false);
        for (String s : pH) {
            guiBuilder.setPlaceholder(s, buildItem(s));
        }
        afkShop = guiBuilder.build(afk);



//        ConfigurationSection sec = configYaml.getConfigurationSection("shop.items");
//        for (String s : sec.getKeys(false)) {
//            ClickType.clickTypes type = config;
//            if (config.getString(s).equalsIgnoreCase("type")) {
//                if
//            }
//        }
    }
//        ItemStack xItem = new ItemStack(Material.DIAMOND);
//        ItemStack oItem = new ItemStack(Material.EMERALD);
//
//        shopgui = new GuiBuilder()
//                .setPlaceholder('X', xItem)
//                .setPlaceholder('O', oItem)
//                .setTitle("Shop GUI")
//                .addRow("XXXXXXXXX")
//                .addRow("XXXXOXXXX")
//                .addRow("XXXXXXXXX")
//                .setClickHandler('X', (player, event) -> {
//                    player.sendMessage("You clicked a diamond!");
//                })
//                .setClickHandler('O', (player, event) -> {
//                    player.sendMessage("You clicked an emerald!");
//                })
//                .build(afk);
//    }

    public Inventory getShop() {
        return afkShop;
    }
}

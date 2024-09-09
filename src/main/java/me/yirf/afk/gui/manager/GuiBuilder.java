package me.yirf.afk.gui.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class GuiBuilder {
    private final Map<String, ItemStack> placeholders;
    private final List<String> rows;
    public String title;
    private final Map<Character, BiConsumer<Player, InventoryClickEvent>> clickHandlers;


    public GuiBuilder() {
        this.placeholders = new HashMap<>();
        this.rows = new ArrayList<>();
        this.title = "Custom GUI";
        this.clickHandlers = new HashMap<>();
    }

    public GuiBuilder setPlaceholder(String symbol, ItemStack item) {
        placeholders.put(symbol, item);
        return this;
    }

    public GuiBuilder addRow(String row) {
        if (row.length() != 9) {
            throw new IllegalArgumentException("Each row must have exactly 9 characters");
        }
        rows.add(row);
        return this;
    }

    public GuiBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public GuiBuilder setClickHandler(char symbol, BiConsumer<Player, InventoryClickEvent> handler) {
        clickHandlers.put(symbol, handler);
        return this;
    }

    public Inventory build(JavaPlugin plugin) {
        int size = rows.size() * 9;
        if (size > 54) {
            throw new IllegalStateException("GUI cannot have more than 6 rows (54 slots)");
        }
        if (size == 0) {
            throw new IllegalStateException("GUI must have at least one row");
        }

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            String row = rows.get(rowIndex);
            for (int colIndex = 0; colIndex < 9; colIndex++) {
                char symbol = row.charAt(colIndex);
                ItemStack item = placeholders.get(symbol);
                if (item != null) {
                    inventory.setItem(rowIndex * 9 + colIndex, item);
                }
            }
        }

        // Register the click listener
        Bukkit.getPluginManager().registerEvents(new GuiClickListener(this), plugin);

        return inventory;
    }

    private static class GuiClickListener implements Listener {
        private final GuiBuilder builder;

        public GuiClickListener(GuiBuilder builder) {
            this.builder = builder;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player)) return;
            if (!event.getView().getTitle().equals(builder.title)) return;

            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            int row = slot / 9;
            int col = slot % 9;

            if (row < builder.rows.size()) {
                char symbol = builder.rows.get(row).charAt(col);
                BiConsumer<Player, InventoryClickEvent> handler = builder.clickHandlers.get(symbol);
                if (handler != null) {
                    handler.accept(player, event);
                }
            }
        }
    }
}
package de.pickaxeenchants.menus;

import de.backpack.listener.EconomyAPI;
import de.pickaxeenchants.api.*;
import de.pickaxeenchants.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccessoryMenu implements Listener {

    private Inventory inventory;
    private static final String INVENTORY_NAME = "Pickaxe Accessories";

    private static final String INVENTORY_MERGE_NAME = "Accessories Merge";

    private static final int INVENTORY_ROWS = 6;

    private List<BeaconEnchant> beaconEnchants;

    private EconomyAPI economy = de.backpack.main.Main.economyAPI;
    UserManager userManager = de.pickaxeenchants.main.Main.userManager;

    EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;
    private List<Enchant> enchants = enchantInitiazer.getEnchants();

    public void enchantInventory(Player player) {

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

        Inventory inventory = Bukkit.createInventory(null, INVENTORY_ROWS * 9, INVENTORY_NAME);


        for (int i = 0; i < 9*INVENTORY_ROWS; i++) {
            ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = pane.getItemMeta();
            meta.setDisplayName("");
            pane.setItemMeta(meta);
            inventory.setItem(i, pane);
        }

        for (int i = 10; i <= 16; i++) {
            ItemStack air = new ItemStack(Material.AIR);
            inventory.setItem(i, air);
        }

        for (int i = 20; i <= 24; i++) {
            ItemStack air = new ItemStack(Material.AIR);
            inventory.setItem(i, air);
        }

        for (Map.Entry<Enchant, Accessory> entry : playerEnchantments.getAccessoryHashMap().entrySet()) {
            ItemStack accessoryItem = entry.getValue().createCustomItem();
            if (accessoryItem != null) {
                inventory.addItem(accessoryItem);
            }
        }

        ItemStack merge_item = new ItemStack(Material.ANVIL);
        ItemMeta merge_meta = merge_item.getItemMeta();
        merge_meta.setDisplayName("§c§lAccessory Merge");
        ArrayList<String> merge_lore = new ArrayList<>();
        merge_lore.add("");
        merge_lore.add("§fHere you merge same");
        merge_lore.add("§ftypes of Accessories");
        merge_lore.add("");
        merge_lore.add("§fLeft-Click §7to open");
        merge_meta.setLore(merge_lore);
        merge_item.setItemMeta(merge_meta);

        inventory.setItem(40, merge_item);


        player.openInventory(inventory);
    }

    public void mergeMenu(Player player) {

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

        Inventory inventory = Bukkit.createInventory(null, INVENTORY_ROWS * 9, INVENTORY_MERGE_NAME);


        for (int i = 0; i < 9 * INVENTORY_ROWS; i++) {
            ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = pane.getItemMeta();
            meta.setDisplayName("");
            pane.setItemMeta(meta);
            inventory.setItem(i, pane);
        }

        for (int i = 20; i <= 23; i++) {
            ItemStack air = new ItemStack(Material.AIR);
            inventory.setItem(i, air);
        }

        for (Map.Entry<Integer, Accessory> entry : playerEnchantments.getMergeAccessoryMap().entrySet()) {
            ItemStack accessoryItem = entry.getValue().createCustomItem();
            if (accessoryItem != null) {
                inventory.setItem(19 + entry.getKey(), accessoryItem);
            }
        }

        ItemStack accessory_item;
        if (!playerEnchantments.canMerge()) {
            accessory_item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta accessory_meta = accessory_item.getItemMeta();
            accessory_meta.setDisplayName("§c§lAccessory???");
            ArrayList<String> accessory_lore = new ArrayList<>();
            accessory_lore.add("");
            accessory_lore.add("§fBoost: ???");
            accessory_lore.add("");
            accessory_lore.add("§fLeft-Click §7to merge");
            accessory_meta.setLore(accessory_lore);
            accessory_item.setItemMeta(accessory_meta);
        } else {
            accessory_item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta accessory_meta = accessory_item.getItemMeta();
            accessory_meta.setDisplayName("§c§lAccessory " + playerEnchantments.getMergeAccessoryMap().get(1).getEnchant().getName() + " " + playerEnchantments.getMergeAccessoryMap().get(1).getLevel()+1);
            ArrayList<String> accessory_lore = new ArrayList<>();
            accessory_lore.add("");
            accessory_lore.add("§fBoost: ???");
            accessory_lore.add("");
            accessory_lore.add("§fLeft-Click §7to merge");
            accessory_meta.setLore(accessory_lore);
            accessory_item.setItemMeta(accessory_meta);
        }

        inventory.setItem(25, accessory_item);


        player.openInventory(inventory);
    }

    @EventHandler
    public void onAddAccessory(InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase(INVENTORY_NAME)) {
            return;
        }

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null || clickedItem.getItemMeta().getDisplayName() == null) {
            return;
        }


        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.equalsIgnoreCase("§c§lAccessory Merge")) {
            Player player = (Player) e.getWhoClicked();
            mergeMenu(player);
            e.setCancelled(true);
        }

        if (displayName.contains("Accessory") && !displayName.contains("Merge")) {
            Player player = (Player) e.getWhoClicked();
            if (e.getRawSlot() > player.getInventory().getSize()) {
                PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

                String enchantName = displayName.substring(4, displayName.indexOf(" Accessory"));
                int level = Integer.parseInt(displayName.substring(displayName.indexOf("Tier ") + 5));
                String boostString = clickedItem.getItemMeta().getLore().get(1);
                String boostValue = boostString.split(": ")[1].replace("§6%", "");
                float boost = Float.parseFloat(boostValue);

                Enchant enchant = null;
                for (Enchant i : enchants) {

                    if (i.getName().equals(enchantName)) {
                        enchant = i;

                        break;
                    }
                }

                if (enchant != null) {

                    if (!playerEnchantments.getAccessoryHashMap().containsKey(enchant)) {
                        Accessory accessory = new Accessory(enchant, level, boost);
                        playerEnchantments.addAccessory(enchant, accessory);
                        e.setCancelled(true);
                        clickedItem.setAmount(0);
                        enchantInventory(player);
                    }
                }
                e.setCancelled(true);
            } else {
                PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());
                Enchant enchant = null;
                for (Map.Entry<Enchant, Accessory> entry : playerEnchantments.getAccessoryHashMap().entrySet()) {
                    if(entry.getValue().getEnchant().getName().equalsIgnoreCase(displayName.substring(4, displayName.indexOf(" Accessory")))){
                        enchant = entry.getKey();
                    }
                }
                playerEnchantments.removeAccessory(enchant);
                enchantInventory(player);
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMergeAccessory(InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase(INVENTORY_MERGE_NAME)) {
            return;
        }

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null || clickedItem.getItemMeta().getDisplayName() == null) {
            return;
        }


        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.equalsIgnoreCase("§c§lAccessory Merge")) {
            Player player = (Player) e.getWhoClicked();
            mergeMenu(player);
            e.setCancelled(true);
        }

        if(e.getSlot() == 25){
            PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(e.getWhoClicked().getUniqueId());
            if(playerEnchantments.canMerge()){
                playerEnchantments.mergeAccessory();
                e.getWhoClicked().closeInventory();
            }

        }

        if (displayName.contains("Accessory") && !displayName.contains("Merge")) {
            Player player = (Player) e.getWhoClicked();
            if (e.getRawSlot() > player.getInventory().getSize()) {
                PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

                String enchantName = displayName.substring(4, displayName.indexOf(" Accessory"));
                int level = Integer.parseInt(displayName.substring(displayName.indexOf("Tier ") + 5));
                String boostString = clickedItem.getItemMeta().getLore().get(1);
                String boostValue = boostString.split(": ")[1].replace("§6%", "");
                float boost = Float.parseFloat(boostValue);

                Enchant enchant = null;
                for (Enchant i : enchants) {

                    if (i.getName().equals(enchantName)) {
                        enchant = i;

                        break;
                    }
                }

                if (enchant != null) {

                    if (!playerEnchantments.getMergeAccessoryMap().containsKey(enchant)) {
                        Accessory accessory = new Accessory(enchant, level, boost);
                        playerEnchantments.addMergeAccessory(accessory);
                        System.out.println(playerEnchantments.getMergeAccessoryMap().entrySet());
                        e.setCancelled(true);
                        if(playerEnchantments.getMergeAccessoryMap().size()<=4) {
                            clickedItem.setAmount(0);
                        }
                        mergeMenu(player);
                    }
                }
                e.setCancelled(true);
            } else if(!displayName.equalsIgnoreCase("§c§lAccessory Merge")){
                PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());
                Enchant enchant = null;
                int id = 0;
                for (Map.Entry<Integer, Accessory> entry : playerEnchantments.getMergeAccessoryMap().entrySet()) {
                    if(entry.getValue().getEnchant().getName().equalsIgnoreCase(displayName.substring(4, displayName.indexOf(" Accessory")))){
                        enchant = entry.getValue().getEnchant();
                        id=entry.getKey();
                    }
                }
                playerEnchantments.removeMergeAccessory(id);
                mergeMenu(player);
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
}

package de.pickaxeenchants.menus;

import de.backpack.apfloat.Apfloat;
import de.backpack.api.PlayerAPI;
import de.backpack.api.PlayerListeners;
import de.backpack.listener.EconomyAPI;
import de.backpack.listener.UnlimitedNumber;
import de.pickaxeenchants.api.*;
import de.pickaxeenchants.enchants.Fortune;
import de.pickaxeenchants.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static de.pickaxeenchants.main.Main.userManager;

public class EnchantMenu {

    private Inventory inventory;
    private static final String INVENTORY_NAME = "PickaxeEnchants";

    private static final String MAIN_INVENTORY_NAME = "Pickaxe Menu";
    private static final int INVENTORY_ROWS = 6;
    private List<Enchant> enchants;

    private List<BeaconEnchant> beaconEnchants;

    private EconomyAPI economy = de.backpack.main.Main.economyAPI;

    public EnchantMenu(){

    }

    public void enchantInventory(Player player) {

        EnchantInitiazer enchantInitiazer = Main.enchantInitiazer;
        this.enchants = enchantInitiazer.getEnchants();

        Inventory inventory = Bukkit.createInventory(null, INVENTORY_ROWS * 9, INVENTORY_NAME);

        int t = 0;

        for (int i = 0; i < INVENTORY_ROWS * 9; i++) {
            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("");
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }

        BigDecimal cost;

        int slot = 10;
        int row = 1;
        for (Enchant enchant : enchants) {

            if (slot > 52) { // Wenn das Inventar voll ist, breche die Schleife ab
                break;
            }
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44 || slot == 53) { // Wenn der letzte Slot der Reihe erreicht ist, gehe zur nächsten Reihe
                row++;
                slot = 10 + (row - 1) * 9;
            }

            ItemStack item = new ItemStack(enchant.getMaterial());
            ItemMeta meta = getEnchantItemMeta(enchant, player);
            item.setItemMeta(meta);


            inventory.setItem(slot, item);


            // Gehe zur nächsten Reihe, wenn die aktuelle voll ist
            slot++;
        }

        setInventory(inventory);


    }

    public void pickaxeInventory(Player player) {
        EnchantInitiazer enchantInitiazer = Main.enchantInitiazer;
        UserManager userManager = Main.userManager;
        this.enchants = enchantInitiazer.getEnchants();
        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_ROWS * 9, MAIN_INVENTORY_NAME);

        int t = 0;

        for (int i = 0; i < INVENTORY_ROWS * 9; i++) {
            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("");
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }

        ItemStack token_enchant_item = new ItemStack(Material.SUNFLOWER);
        ItemMeta token_enchant_meta = token_enchant_item.getItemMeta();
        token_enchant_meta.setDisplayName("§6§lToken Enchants");
        ArrayList<String> token_enchant_lore = new ArrayList<>();
        token_enchant_lore.add("");
        token_enchant_lore.add("§fHere you can view and");
        token_enchant_lore.add("§fupgrade Token Enchants");
        token_enchant_lore.add("");
        token_enchant_lore.add("§fLeft-Click §7to open");
        token_enchant_meta.setLore(token_enchant_lore);
        token_enchant_item.setItemMeta(token_enchant_meta);


        ItemStack forge_item = new ItemStack(Material.ANVIL);
        ItemMeta meta = forge_item.getItemMeta();
        meta.setDisplayName("§c§lForge");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§fThrough researching, your pickaxe");
        lore.add("§fwill gain various enchantment buffs.");
        lore.add("");
        lore.add("§7price: §f" + userManager.buffCost() + " Beacons");
        lore.add("");
        if(userManager.getPickaxeLevel(player)<25){
            lore.add("§cRequires pickaxe level 25 to unlock");
        }
        for (Enchant enchant : playerEnchantments.getBuffedEnchants()) {
            int buff = playerEnchantments.getBuff(enchant);
            if (buff < 0) {
                lore.add("§7§l- §f" + enchant.getName() + " §c" + buff + "§c%");
            } else {
                lore.add("§7§l- §f" + enchant.getName() + " §a" + buff + "§a%");
            }
        }
        lore.add("");
        lore.add("§fLeft-Click §7to forge");
        meta.setLore(lore);
        forge_item.setItemMeta(meta);

        ItemStack beacon_item = new ItemStack(Material.BEACON);
        ItemMeta meta1 = beacon_item.getItemMeta();
        meta1.setDisplayName("§b§lBeacon Enchants");
        ArrayList<String> lore1 = new ArrayList<>();
        lore1.add("");
        lore1.add("§fBeacon Enchants make you feel like a god");
        lore1.add("");
        lore1.add("§fLeft-Click §7to open Beacon Enchant Menu");
        meta1.setLore(lore1);
        beacon_item.setItemMeta(meta1);

        ItemStack skin_item = new ItemStack(Material.PAINTING);
        ItemMeta meta2 = skin_item.getItemMeta();
        meta2.setDisplayName("§b§lSkins");
        ArrayList<String> lore2 = new ArrayList<>();
        lore2.add("");
        lore2.add("§fSkins give your pickaxe");
        lore2.add("§fa new look and cool new superpowers");
        lore2.add("§fLeft-Click §7to change your Pickaxe Skin");
        lore2.add("");
        meta2.setLore(lore2);
        skin_item.setItemMeta(meta2);

        ItemStack accessory_item = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta accessory_meta = accessory_item.getItemMeta();
        accessory_meta.setDisplayName("§b§lAccessories");
        ArrayList<String> accessory_lore = new ArrayList<>();
        accessory_lore.add("");
        accessory_lore.add("§fAccessories give you ");
        accessory_lore.add("§fa boost to your enchantments");
        accessory_lore.add("");
        accessory_lore.add("§fLeft-Click §7to change your Pickaxe Skin");
        accessory_lore.add("");
        accessory_meta.setLore(accessory_lore);
        accessory_item.setItemMeta(accessory_meta);

        inventory.setItem(12, token_enchant_item);
        inventory.setItem(25, skin_item);
        inventory.setItem(22, forge_item);
        inventory.setItem(14, beacon_item);
        inventory.setItem(19, accessory_item);

        setInventory(inventory);


    }

    public void beaconEnchantInventory(Player player) {

        EnchantInitiazer enchantInitiazer = Main.enchantInitiazer;
        this.beaconEnchants = enchantInitiazer.getBeaconEnchants();

        Inventory inventory = Bukkit.createInventory(null, INVENTORY_ROWS * 9, INVENTORY_NAME);

        int t = 0;

        for (int i = 0; i < INVENTORY_ROWS * 9; i++) {
            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("");
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }


        int slot = 10;
        int row = 1;
        for (BeaconEnchant enchant : beaconEnchants) {



            if (slot > 52) {
                break;
            }
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44 || slot == 53) {
                row++;
                slot = 10 + (row - 1) * 9;
            }

            ItemStack item = new ItemStack(enchant.getMaterial());
            ItemMeta meta = getBeaconEnchantItemMeta(enchant, player);
            item.setItemMeta(meta);


            inventory.setItem(slot, item);

            slot++;
        }



        ItemStack back_item = new ItemStack(Material.ARROW);
        ItemMeta meta = back_item.getItemMeta();
        meta.setDisplayName("§c§lBack to Enchant Menu");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§fLeft-Click §7to go back!");
        meta.setLore(lore);
        back_item.setItemMeta(meta);

        inventory.setItem(49, back_item);

        setInventory(inventory);
    }

    public ItemMeta getEnchantItemMeta(Enchant enchant, Player player) {

        EnchantInitiazer enchantInitiazer = Main.enchantInitiazer;

        UserManager userManager = Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());


        BigDecimal baseCost = BigDecimal.valueOf(enchant.getBaseCost());

        BigDecimal costFactor = BigDecimal.valueOf(enchant.getCostFactor());

        int level_can_afford = userManager.caclLevelAll(enchant, player);

        Apfloat cost_can_afford = userManager.calcCost(enchant, player, level_can_afford);

        UnlimitedNumber cost_can_afford_number = new UnlimitedNumber(String.valueOf(cost_can_afford));

        UnlimitedNumber total_price_number;

        BigDecimal cost;

        try {
            cost = baseCost.multiply(costFactor.pow(playerEnchantments.getEnchantLevel(enchant) + 1 - 1));
        } catch (Exception e) {
            cost = BigDecimal.valueOf(0);
        }

        int level = 10;
        BigDecimal totalCost10 = baseCost.multiply(costFactor.pow(playerEnchantments.getEnchantLevel(enchant)));
        BigDecimal cost10;

        for(int x = 1; x<level; x++) {
            try {
                cost10 = baseCost.multiply(costFactor.pow(playerEnchantments.getEnchantLevel(enchant) + 1 - 1 + x));
                totalCost10 = totalCost10.add(cost10);
            } catch (Exception e) {
                break;
            }
        }
        Apfloat costInCents10 = userManager.calcCost(enchant, player, 10);

        String level_cost_string10 = String.valueOf(costInCents10);
        UnlimitedNumber total_price_number10 = new UnlimitedNumber(level_cost_string10);


        Apfloat costInCents = userManager.calcCost(enchant, player, 1);

        String level_cost_string = String.valueOf(costInCents);
        total_price_number = new UnlimitedNumber(level_cost_string);

        double chance = playerEnchantments.calcActivateChance(enchant)*100;

        ItemStack item = new ItemStack(enchant.getMaterial());
        ItemMeta meta = item.getItemMeta();

        String enchant_name = new StringBuilder(enchant.getName()).toString().replace('_', ' ');

        meta.setDisplayName("§b§l" + enchant_name);
        ArrayList<String> lore = new ArrayList<>();
        String[] data = enchant.getDescription().split(" ");
        StringBuilder nextString = new StringBuilder();
        for (String s : data) {
            if (s.length() > 25) {
                continue;
            }
            if (nextString.length() + s.length() + 1 > 30) {
                lore.add("§f"+ nextString.toString());
                nextString = new StringBuilder(ChatColor.WHITE + s + " ");
            } else {
                nextString.append(s).append(" ");
            }
        }
        lore.add(nextString.toString());

        lore.add("");

        String types = ""; // Initialisiere die Zeichenkette
        for (EnchantTypes t : enchant.getEnchantTypes()) {
            types += t.toString() + ", "; // Füge jeden Typen mit einem Komma hinzu
        }
// Entferne das letzte Komma und Leerzeichen, wenn es mindestens einen Typen gibt
        if (!types.isEmpty()) {
            types = types.substring(0, types.length() - 2);
        }
        lore.add("§fType: §e" + types);


        if(userManager.getPickaxeLevel().get(player.getUniqueId())<enchant.getUnlock_level()) {
            lore.add("");
            lore.add("§cRequires pickaxe level " + enchant.getUnlock_level() + " to unlock");
        } else {
            lore.add("");
            lore.add("§e§lEnchant Statistics");
            if(playerEnchantments.isMaxLevel(enchant)) {
                lore.add("§e§l| §fLevel: §e" + playerEnchantments.getEnchantLevel(enchant) + "§8/§c" + enchant.getMaxLevel() + " §7(§e§lMAXED§7)");
            } else {
                lore.add("§e§l| §fLevel: §e" + playerEnchantments.getEnchantLevel(enchant) + "§8/§c" + enchant.getMaxLevel());
            }
            lore.add("§e§l| §fPrice: §e" + total_price_number.format() + " §eTokens");

            if(!enchant.getName().equalsIgnoreCase("Fortune")) {
                lore.add("§e§l| §fproc-chance: §e" + new DecimalFormat("#.#####").format(chance) + "§e%");
            } else {
                Fortune fortune = new Fortune();
                lore.add("§e§l| §fmulti: §e" + new DecimalFormat("#.####").format(fortune.blocksMulti(player)) + "§ex");
            }
            lore.add("§e§l| §fProc Count: §e" + enchant.getProcs());
            lore.add("");
            lore.add("§e§lEnchant Upgrading");
            lore.add("§fLeft-Click §7to upgrade §f1 level §7for §b" + total_price_number.format() + "§b Tokens");
            lore.add("§fRight-Click §7to upgrade §f10 levels §7for §b" + total_price_number10.format() + "§b Tokens");
            lore.add("§fQ §7to upgrade §f" + userManager.caclLevelAll(enchant, player) + " levels §7for §b" + cost_can_afford_number.format() + "§b Tokens");
        }
        meta.setLore(lore);

        return meta;
    }

    public ItemMeta getBeaconEnchantItemMeta(BeaconEnchant enchant, Player player) {

        EnchantInitiazer enchantInitiazer = Main.enchantInitiazer;

        UserManager userManager = Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());


        BigDecimal baseCost = BigDecimal.valueOf(enchant.getBaseCost());

        BigDecimal costFactor = BigDecimal.valueOf(enchant.getCostFactor());


        UnlimitedNumber total_price_number;

        BigInteger costInCents10 = BigInteger.valueOf(userManager.calcCostBeaconEnchant(enchant, player, 10));

        String level_cost_string10 = String.valueOf(costInCents10);
        UnlimitedNumber total_price_number10 = new UnlimitedNumber(level_cost_string10);


        BigInteger costInCents = BigInteger.valueOf(userManager.calcCostBeaconEnchant(enchant, player, 1));

        String level_cost_string = String.valueOf(costInCents);
        total_price_number = new UnlimitedNumber(level_cost_string);

        double chance = (enchant.getBaseChance() + (enchant.getChanceIncrease() * playerEnchantments.getBeaconEnchantLevel(enchant)))*100;

        if(playerEnchantments.getBeaconEnchantLevel(enchant)==0){
            chance=0;
        }

        ItemStack item = new ItemStack(enchant.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§l" + enchant.getName());
        ArrayList<String> lore = new ArrayList<>();
        String[] data = enchant.getDescription().split(" ");
        StringBuilder nextString = new StringBuilder();
        for (String s : data) {
            if (s.length() > 25) {
                continue;
            }
            if (nextString.length() + s.length() + 1 > 30) {
                lore.add("§f"+ nextString.toString());
                nextString = new StringBuilder(ChatColor.WHITE + s + " ");
            } else {
                nextString.append(s).append(" ");
            }
        }
        lore.add(nextString.toString());


        lore.add("");
        lore.add("§eEnchant Statistics");
        lore.add("§7Level: §f" + playerEnchantments.getBeaconEnchantLevel(enchant) + "§8/§c" + enchant.getMaxLevel());
        lore.add("§7Price: §f" + total_price_number.format() + " §fBeacons");

        if(!enchant.getName().equalsIgnoreCase("Fortune")) {
            lore.add("§7proc-chance: §b" + new DecimalFormat("#.#####").format(chance) + "§b%");
        } else {
            Fortune fortune = new Fortune();
            lore.add("§7multi: §b" + new DecimalFormat("#.####").format(fortune.blocksMulti(player)) + "§bx");
        }
        lore.add("");
        lore.add("§eEnchant Upgrading");
        lore.add("§fLeft-Click §7to upgrade §f1 level §7for §b" + total_price_number.format() + "§b Beacons");
        lore.add("§fRight-Click §7to upgrade §f10 levels §7for §b" + total_price_number10.format() + "§b Beacons");
        meta.setLore(lore);

        return meta;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

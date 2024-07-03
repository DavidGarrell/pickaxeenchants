package de.pickaxeenchants.events;

import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.listener.UnlimitedNumber;
import de.pickaxeenchants.api.*;
import de.pickaxeenchants.forge.PickaxeForges;
import de.pickaxeenchants.items.EnchantPickaxe;
import de.pickaxeenchants.main.Main;
import de.pickaxeenchants.menus.AccessoryMenu;
import de.pickaxeenchants.menus.EnchantMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnchantMenuEvents implements Listener {

    private List<Enchant> enchants;
    private List<BeaconEnchant> beaconEnchants;


    @EventHandler

    public void enchantBuyEvent(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        EnchantInitiazer enchantInitiazer = Main.enchantInitiazer;

        UserManager userManager = Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();
        this.beaconEnchants = enchantInitiazer.getBeaconEnchants();

        EnchantMenu enchantMenu = new EnchantMenu();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

        EconomyAPI economyAPI = de.backpack.main.Main.economyAPI;
        if (e.getView().getTitle().equalsIgnoreCase("Pickaxe Menu")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§b§lBeacon Enchants")) {

                    enchantMenu.beaconEnchantInventory(player);
                    player.openInventory(enchantMenu.getInventory());

                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lToken Enchants")) {
                    enchantMenu.enchantInventory(player);
                    player.openInventory(enchantMenu.getInventory());
                }

                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lBack to Enchant Menu")) {
                    enchantMenu.enchantInventory(player);
                    player.openInventory(enchantMenu.getInventory());
                }

                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§b§lAccessories")) {
                    AccessoryMenu accessoryMenu = new AccessoryMenu();
                    accessoryMenu.enchantInventory(player);
                }

                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lForge")) {
                    if (userManager.getPickaxeLevel(player) >= 25) {
                        if (de.backpack.main.Main.economyAPI.getBeacons(player) >= (userManager.buffCost())) {

                            de.backpack.main.Main.economyAPI.setBeacons(player, de.backpack.main.Main.economyAPI.getBeacons(player) - userManager.buffCost());


                            player.sendMessage("Forge");
                            PickaxeForges pickaxeForges = new PickaxeForges();
                            pickaxeForges.forgeEnchants(player);
                            EnchantPickaxe enchantPickaxe = new EnchantPickaxe();
                            player.getInventory().getItemInMainHand().setItemMeta(enchantPickaxe.enchantPickaxeMeta(player));

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
                            e.getCurrentItem().setItemMeta(meta);
                        }
                    }
                }
            }
        }
        if (e.getView().getTitle().equalsIgnoreCase("PickaxeEnchants")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {

                for (Enchant enchant : enchants) {

                    String enchant_name = String.valueOf(new StringBuilder(String.valueOf(enchant.getName()).replace("_", " ")));

                    if (("§b§l" + enchant_name).equalsIgnoreCase(e.getCurrentItem().getItemMeta().getDisplayName())) {

                        player.sendMessage(enchant_name);

                        if (userManager.getPickaxeLevel().get(player.getUniqueId()) >= enchant.getUnlock_level()) {

                            if (e.getClick().equals(ClickType.LEFT)) {

                                if (de.backpack.main.Main.economyAPI.getTokens(player).compareTo(userManager.calcCost(enchant, player, 1)) >= 0 && !playerEnchantments.isMaxLevel(enchant)) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                    UnlimitedNumber unlimitedNumber = new UnlimitedNumber(userManager.calcCost(enchant, player, 1).toString(true));
                                    de.backpack.main.Main.economyAPI.removeTokens(player, userManager.calcCost(enchant, player, 1));
                                    player.sendMessage("§6Enchant §7| §fyou purchased 1 level of §e" + enchant_name + "§f for §e" + unlimitedNumber.format() + " §eTokens");
                                    playerEnchantments.upgradeEnchant(enchant, 1);
                                    e.getCurrentItem().setItemMeta(enchantMenu.getEnchantItemMeta(enchant, player));
                                }

                            }
                            if (e.getClick().equals(ClickType.RIGHT)) {
                                if (de.backpack.main.Main.economyAPI.getTokens(player).compareTo(userManager.calcCost(enchant, player, 10)) >= 0 && !playerEnchantments.isMaxLevel(enchant)) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                    UnlimitedNumber unlimitedNumber = new UnlimitedNumber(userManager.calcCost(enchant, player, 10).toString(true));
                                    player.sendMessage("§6Enchant §7| §fyou purchased 10 level's of §e" + enchant_name + "§f for §e" + unlimitedNumber.format() + " §eTokens");
                                    de.backpack.main.Main.economyAPI.removeTokens(player, userManager.calcCost(enchant, player, 10));
                                    playerEnchantments.upgradeEnchant(enchant, 10);
                                    e.getCurrentItem().setItemMeta(enchantMenu.getEnchantItemMeta(enchant, player));

                                }
                            }

                            if (e.getClick().equals(ClickType.DROP)) {
                                int level = userManager.caclLevelAll(enchant, player);
                                if (level > 0) {
                                    UnlimitedNumber unlimitedNumber = new UnlimitedNumber(userManager.calcCost(enchant, player, level).toString(true));
                                    de.backpack.main.Main.economyAPI.removeTokens(player, userManager.calcCost(enchant, player, level));
                                    playerEnchantments.upgradeEnchant(enchant, level);
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                    player.sendMessage("§6Enchant §7| §fyou purchased " + level + " level's of §e" + enchant_name + "§f for §e" + unlimitedNumber.format() + " §eTokens");
                                    e.getCurrentItem().setItemMeta(enchantMenu.getEnchantItemMeta(enchant, player));
                                }
                            }
                            e.setCancelled(true);
                        }


                    }


                    EnchantPickaxe enchantPickaxe = new EnchantPickaxe();


                    player.getInventory().getItemInMainHand().setItemMeta(enchantPickaxe.enchantPickaxeMeta(player));

                }

                for (BeaconEnchant enchant : beaconEnchants) {

                    if (("§b§l" + enchant.getName()).equals(Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getDisplayName())) {

                        if (e.getClick().equals(ClickType.LEFT)) {

                            if (de.backpack.main.Main.economyAPI.getBeacons(player) >= userManager.calcCostBeaconEnchant(enchant, player, 1)) {
                                de.backpack.main.Main.economyAPI.setBeacons(player, de.backpack.main.Main.economyAPI.getBeacons(player) - (userManager.calcCostBeaconEnchant(enchant, player, 1)));
                                playerEnchantments.upgradeBeaconEnchant(enchant, 1);
                            }

                        }
                        if (e.getClick().equals(ClickType.RIGHT)) {
                            if (de.backpack.main.Main.economyAPI.getBeacons(player) >= userManager.calcCostBeaconEnchant(enchant, player, 10)) {
                                de.backpack.main.Main.economyAPI.setBeacons(player, de.backpack.main.Main.economyAPI.getBeacons(player) - (userManager.calcCostBeaconEnchant(enchant, player, 10)));
                                playerEnchantments.upgradeBeaconEnchant(enchant, 10);

                            }
                        }

                        e.getCurrentItem().setItemMeta(enchantMenu.getBeaconEnchantItemMeta(enchant, player));

                        EnchantPickaxe enchantPickaxe = new EnchantPickaxe();


                        player.getInventory().getItemInMainHand().setItemMeta(enchantPickaxe.enchantPickaxeMeta(player));
                    }
                }

            }
            e.setCancelled(true);
        }
    }
}



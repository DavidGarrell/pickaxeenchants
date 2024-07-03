package de.pickaxeenchants.items;

import de.backpack.listener.EconomyAPI;
import de.pickaxeenchants.api.*;
import de.pickaxeenchants.main.Main;
import de.pickaxeenchants.menus.EnchantMenu;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class EnchantPickaxe implements Listener {

    private List<Enchant> enchants;
    private List<BeaconEnchant> beaconEnchants;
    @EventHandler

    public void onJoin(PlayerJoinEvent e){

        Player player = e.getPlayer();

        UserManager userManager = Main.userManager;
        if(userManager.getPickaxeLevel().get(player.getUniqueId()) == null) {
            userManager.setPickaxeLevel(player, 0);
        }

        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = enchantPickaxeMeta(player);
        item.setItemMeta(meta);
        player.getInventory().setItem(0, item);



    }

    @EventHandler

    public void onRightClickEnchantPickaxe(PlayerInteractEvent e){

        Player player = e.getPlayer();

        EnchantMenu enchantMenu = new EnchantMenu();

        if(player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE){
            if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){

                enchantMenu.pickaxeInventory(player);
                player.openInventory(enchantMenu.getInventory());
            }
        }
    }




    public ItemMeta enchantPickaxeMeta(Player player){

        EnchantInitiazer enchantInitiazer = Main.enchantInitiazer;

        UserManager userManager = Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();
        this.beaconEnchants = enchantInitiazer.getBeaconEnchants();
        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

        EconomyAPI economyAPI = de.backpack.main.Main.economyAPI;

        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.addEnchant(Enchantment.DIG_SPEED, 100, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);


        meta.setDisplayName("§e§l" + player.getName() + "§e§l's §f§lPickaxe");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§b§lPickaxe Stats");
        lore.add("§fLevel: " + userManager.getPickaxeLevel(player));
        lore.add("§fBlocks Destroyed: " + economyAPI.getTotalBlocksBreak(player));
        //lore.add("§fMastery: " + userManager.getMastery(player));
        lore.add("");
        lore.add("§c§lPickaxe Enchantments");

        for (Enchant enchant : enchants) {
            if(playerEnchantments.getEnchantLevel(enchant) > 0) {
                lore.add(" §c§l| §f" + enchant.getName() + " §f" + playerEnchantments.getEnchantLevel(enchant));
            }
        }

        for (BeaconEnchant enchant : beaconEnchants) {
            if(playerEnchantments.getBeaconEnchantLevel(enchant) > 0) {
                lore.add(" §6§l| §f" + enchant.getName() + " §f" + playerEnchantments.getBeaconEnchantLevel(enchant));
            }
        }

        lore.add("");
        if (!playerEnchantments.getEnchantBuffs().isEmpty()) {
            lore.add("§a§lPickaxe Forges");
        for (Enchant enchant : playerEnchantments.getBuffedEnchants()) {
                int buff = playerEnchantments.getBuff(enchant);

            if (buff < 0) {
                lore.add(" §a§l| §f" + enchant.getName() + " §c" + buff + "§c%");
            } else {
                lore.add(" §a§l| §f" + enchant.getName() + " §a" + buff + "§a%");
            }

            }
        }
        meta.setLore(lore);

        return meta;
    }
}

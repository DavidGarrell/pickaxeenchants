package de.pickaxeenchants.listeners;

import de.pickaxeenchants.items.EnchantPickaxe;
import de.pickaxeenchants.menus.EnchantMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Objects;

public class InteractPickaxe implements Listener {



    /*
    @EventHandler

    public void dropPickaxe(PlayerDropItemEvent event) {


        Player player = event.getPlayer();
        EnchantPickaxe enchantPickaxe = new EnchantPickaxe();

        if (Objects.requireNonNull(event.getItemDrop().getCustomName()).contains(enchantPickaxe.enchantPickaxeMeta(player).getDisplayName())) {
            event.setCancelled(true);
        } else {

        }


    }

    @EventHandler

    public void onPlayerInventoryClick(InventoryClickEvent event){

        Player player = (Player) event.getWhoClicked();
        EnchantPickaxe enchantPickaxe = new EnchantPickaxe();


        if(event.getCurrentItem().getItemMeta()!=null) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains(enchantPickaxe.enchantPickaxeMeta(player).getDisplayName())) {
                event.setCancelled(true);
                EnchantMenu enchantMenu = new EnchantMenu();
                enchantMenu.enchantInventory(player);
                player.openInventory(enchantMenu.getInventory());


            } else {

            }
        }

    }

     */

}

package de.pickaxeenchants.listeners;

import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.items.EnchantPickaxe;
import de.pickaxeenchants.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class UpdatePickaxeMeta implements Listener {

    UserManager userManager = Main.userManager;


    @EventHandler

    public void onBreak(BlockBreakEvent e){

        Player player = e.getPlayer();
        EnchantPickaxe enchantPickaxe = new EnchantPickaxe();

        if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(enchantPickaxe.enchantPickaxeMeta(player).getDisplayName())){

            e.getPlayer().getInventory().getItemInMainHand().setItemMeta(enchantPickaxe.enchantPickaxeMeta(player));



            userManager.addPickaxeXP(player, 1);

            if(userManager.canPickaxeLevelUp(player)){
                userManager.addPickAxeLevel(player, 1);
            }



        }
    }
}

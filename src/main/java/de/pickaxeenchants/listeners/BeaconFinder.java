package de.pickaxeenchants.listeners;

import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.enchants.CalculateDestoyedBlocksByEnchants;
import de.pickaxeenchants.items.EnchantPickaxe;
import de.pickaxeenchants.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class BeaconFinder implements Listener {

    UserManager userManager = Main.userManager;
    private List<Enchant> enchants;

    @EventHandler

    public void onBreak(BlockBreakEvent e) {

        Player player = e.getPlayer();
        EnchantPickaxe enchantPickaxe = new EnchantPickaxe();
        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(e.getPlayer().getUniqueId());


        if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(enchantPickaxe.enchantPickaxeMeta(player).getDisplayName())) {

            int level = 0;

            Enchant en = null;

            for (Enchant enchant : enchants) {
                if (enchant.getName().equalsIgnoreCase("Beacon_Finder")) {
                    level = playerEnchantments.getEnchantLevel(enchant);
                    en = enchant;
                }
            }
            int amount = 1 + (level / 100);

            if (Math.random() < playerEnchantments.calcActivateChance(en)) {
                de.backpack.main.Main.economyAPI.setBeacons(player, de.backpack.main.Main.economyAPI.getBeacons(player) + amount);

            }

        }
    }
}



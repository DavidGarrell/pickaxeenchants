package de.pickaxeenchants.enchants;

import de.backpack.apfloat.Apfloat;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.util.List;
import java.util.Map;

public class TokenFinder implements Listener {

    private List<Enchant> enchants;

    private long baseTokens = 0;
    private long factorPerLevel = 30000;

    private Enchant enchantment;

    @EventHandler

    public void onBreak(BlockBreakEvent e){

        Player player = e.getPlayer();
        if(skyblock.main.Main.islandManager.island.get(e.getPlayer().getUniqueId())!=null) {
            if (skyblock.main.Main.islandManager.island.get(e.getPlayer().getUniqueId()).checkIfBlockIsInMine(e.getBlock().getLocation())) {


                EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

                UserManager userManager = de.pickaxeenchants.main.Main.userManager;

                this.enchants = enchantInitiazer.getEnchants();

                PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

                double chance = 0;

                for (Enchant enchant : enchants) {
                    if (enchant.getName().equalsIgnoreCase("Token_Finder")) {
                        enchantment = enchant;
                        chance = enchant.getBaseChance();
                        if (playerEnchantments.getEnchantLevel(enchant) == 0) {
                            chance = 0;
                        }
                    }
                }

                if(Math.random()<chance) {

                    enchantment.procEnchantment();
                    EconomyAPI economyAPI = Main.economyAPI;

                    Apfloat amount = new Apfloat(calcTokenAmount(player));

                    if(e.getBlock().getType().equals(Material.EMERALD_BLOCK)){

                        userManager.addTokensFormEnchant(player, amount.multiply(new Apfloat(10)), null);
                    } else {
                        userManager.addTokensFormEnchant(player, amount, null);
                    }


                }
            }
        }
    }

    public long calcTokenAmount(Player player){

        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());


        int tlevel = 0;

        Enchant e = null;

        for (Enchant enchant : enchants) {
            if (enchant.getName().equalsIgnoreCase("Token_Finder")) {
                tlevel = playerEnchantments.getEnchantLevel(enchant);
                e = enchant;
            }
        }

        long tokensAmount = baseTokens+(factorPerLevel*tlevel);

        double buffMultiplier = playerEnchantments.calcBuffs(e);

        return (long) (tokensAmount *buffMultiplier);

    }
}

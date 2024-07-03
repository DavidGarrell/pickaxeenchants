package de.pickaxeenchants.enchants;

import de.backpack.apfloat.Apfloat;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.util.List;
import java.util.Map;

public class TokenMerchant implements Listener {

    private List<Enchant> enchants;

    private long baseTokens = 0;
    private long factorPerLevel = 30000;

    private int multi = 10000;

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
                    if (enchant.getName().equalsIgnoreCase("Token_Merchant")) {
                        chance = enchant.getBaseChance();
                        if (playerEnchantments.getEnchantLevel(enchant) == 0) {
                            chance = 0;
                        }
                    }
                }

                if(Math.random()<chance) {

                    EconomyAPI economyAPI = Main.economyAPI;

                    Apfloat amount = new Apfloat(calcTokenAmount(player));

                    economyAPI.addTokens(player, amount);

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

        for (Enchant enchant : enchants) {
            if (enchant.getName().equalsIgnoreCase("Token_Finder")) {
                tlevel = playerEnchantments.getEnchantLevel(enchant);
            }
        }


        long tokensAmount = baseTokens+(factorPerLevel*tlevel);


        double buffMultiplier = 0;
        Map<Enchant, Integer> enchantBuffs = playerEnchantments.getEnchantBuffs();
        for (Enchant enchant : enchantBuffs.keySet()) {
            if (enchant.getName().equalsIgnoreCase("Token_Finder")) {
                buffMultiplier += enchantBuffs.get(enchant);
            }
        }

        long totalTokensAmount = (long) (tokensAmount * (1 + buffMultiplier / 100));

        return totalTokensAmount*multi;

    }
}

package de.pickaxeenchants.enchants;

import de.backpack.apfloat.Apfloat;
import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import skyblock.api.Island;
import skyblock.api.IslandManager;
import skyblock.api.IslandMaterials;

import java.util.List;
import java.util.Map;

public class Fortune implements Listener {


    private List<Enchant> enchants;
    private List<BackPackAPI> backpacks;

    public double blocksMulti(Player player){
        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());

        int fortuneLevel = 0;

        for (Enchant enchant : enchants) {
            if (enchant.getName().equalsIgnoreCase("Fortune")) {
                fortuneLevel = playerEnchantments.getEnchantLevel(enchant);
            }
        }

        double fortuneMultiplier = 1 + ((double) fortuneLevel / 200);

        // Berechne den Buff Multiplier in Prozent (als Wert zwischen -100 und 100)
        double buffMultiplier = 0;
        Map<Enchant, Integer> enchantBuffs = playerEnchantments.getEnchantBuffs();
        for (Enchant enchant : enchantBuffs.keySet()) {
            if (enchant.getName().equalsIgnoreCase("Fortune")) {
                buffMultiplier += enchantBuffs.get(enchant);
            }
        }

        buffMultiplier+= userManager.calculateMasteryEnchantBuff(player);

        // Wende den Buff Multiplier auf den Fortune Multiplier an
        double totalMultiplier = fortuneMultiplier * (1 + buffMultiplier / 100);

        return totalMultiplier;

    }

    @EventHandler

    public void onBlockBreak(BlockBreakEvent e) {

        Player player = e.getPlayer();
        Material material;

        material = e.getBlock().getType();


        EconomyAPI economyAPI = Main.economyAPI;

        economyAPI.addTotalBlocks_break(player, 1);

        BackPack backPack = Main.backPack;
        if (skyblock.main.Main.islandManager.island.get(e.getPlayer().getUniqueId()) != null) {
            if (skyblock.main.Main.islandManager.island.get(e.getPlayer().getUniqueId()).checkIfBlockIsInMine(e.getBlock().getLocation())) {

                IslandManager islandManager = skyblock.main.Main.islandManager;
                Island island = islandManager.island.get(player.getUniqueId());
                UserManager userManager = de.pickaxeenchants.main.Main.userManager;
                userManager.addMoneyFormEnchant(player, new Apfloat(1), null);

                backPack.getPlayer_backpacks().get(player.getUniqueId()).addBlocks(material, 1);
                economyAPI.addBlocks(e.getPlayer(), 1);
                economyAPI.addXPBalance(e.getPlayer(), (int) blocksMulti(player));
            } else {
                if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                    e.setCancelled(true);

                }
            }
        }
    }

}

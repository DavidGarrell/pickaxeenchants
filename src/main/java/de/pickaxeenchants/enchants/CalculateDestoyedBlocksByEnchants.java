package de.pickaxeenchants.enchants;

import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import skyblock.api.IslandManager;

import java.util.List;
import java.util.Map;

import static de.pickaxeenchants.enchants.ExplosiveEnchantEvent.Explosive;
import static de.pickaxeenchants.enchants.ExplosiveEnchantEvent.plugin;

public class CalculateDestoyedBlocksByEnchants {

    static Fortune fortune = new Fortune();

    public int calculateDestroyedBlocksExplosive(int blocks, int level, Enchant enchant, Player player) {
        double baseChance = enchant.getBaseChance(); // Replace with the actual base chance value
        double chanceIncrease = enchant.getChanceIncrease(); // Replace with the actual chance increase value

        double chance = baseChance + (chanceIncrease * level);
        if (level == 0) {
            chance = 0;
        }

        double buffMultiplier = 0; // Calculate buffMultiplier as you did in your code

        int radius_1 = 4;
        int radius_2 = level / 625;
        int radius = radius_1 + radius_2;

        int volume = (2 * radius + 1) * (2 * radius + 1) * (radius_1 + radius_2 + 1);
        volume /= 2;

        // Calculate the potential number of blocks obtained with chance
        int potentialBlocks = (int) ((volume * fortune.blocksMulti(player)) * (blocks*chance));

        return potentialBlocks;
    }

    public int calculateDestroyedBlocksStrike(int blocks, int level, Enchant enchant, Player player) {
        double baseChance = enchant.getBaseChance(); // Replace with the actual base chance value
        double chanceIncrease = enchant.getChanceIncrease(); // Replace with the actual chance increase value

        double chance = baseChance + (chanceIncrease * level);
        if (level == 0) {
            chance = 0;
        }

        double buffMultiplier = 0; // Calculate buffMultiplier as you did in your code

        int radius_1 = 4;
        int radius_2 = level / 500;
        int radius = radius_1 + radius_2;

        int destroyedBlocks = 0;
        for (double x = -radius; x <= radius; x++) {
            for (double y = -radius - 45; y <= 0; y++) {
                for (double z = -radius; z <= radius; z++) {
                    double distance = (x * x + z * z + 0.25 * y * y);

                    if (distance < radius * radius && !(false && distance < ((radius - 1) * (radius - 1)))) {
                        destroyedBlocks++;
                    }
                }
            }
        }

        // Calculate the potential number of blocks obtained with chance
        int potentialBlocks = (int) ((destroyedBlocks * fortune.blocksMulti(player)) * (blocks*chance));

        return potentialBlocks;
    }
    public int calculateDestroyedBlocksLayer(int blocks, int level, Enchant enchant, Player player) {
        double baseChance = enchant.getBaseChance(); // Replace with the actual base chance value
        double chanceIncrease = enchant.getChanceIncrease(); // Replace with the actual chance increase value

        double chance = baseChance + (chanceIncrease * level);
        if (level == 0) {
            chance = 0;
        }
        IslandManager islandManager = skyblock.main.Main.islandManager;
        int value = islandManager.island.get(player.getUniqueId()).getISLAND_SIZE()*islandManager.island.get(player.getUniqueId()).getISLAND_SIZE();
        return (int) ((value*fortune.blocksMulti(player)) * (blocks*chance));
    }
}

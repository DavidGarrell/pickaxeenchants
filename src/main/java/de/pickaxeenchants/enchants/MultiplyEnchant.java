package de.pickaxeenchants.enchants;

import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class MultiplyEnchant {


    private List<Enchant> enchants;

    public double multi(){
        double min = 1.0;
        double max = 1.5;

        Random random = new Random();
        double multi = min + random.nextDouble() * (max - min);

        return multi;
    }

    public double proc_chance(Player player){
        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());
        int level_multiply = 0;

        for(Enchant enchant : enchants) {
            if(enchant.getName().equalsIgnoreCase("Multiply")){
                level_multiply = playerEnchantments.getEnchantLevel(enchant);
            }
        }
        return 0.0000025 * level_multiply;
    }
}

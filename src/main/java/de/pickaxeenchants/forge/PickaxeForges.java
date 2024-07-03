package de.pickaxeenchants.forge;

import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.main.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.pickaxeenchants.main.Main.enchantInitiazer;
import static de.pickaxeenchants.main.Main.userManager;

public class PickaxeForges {
    private final List<Enchant> enchants;
    private final Random random;

    public PickaxeForges() {
        enchants = enchantInitiazer.getEnchants();
        random = new Random();
    }

    public void addEnchant(Enchant enchant) {
        enchants.add(enchant);
    }

    public List<Enchant> getRandomEnchants(int count) {
        List<Enchant> randomEnchants = new ArrayList<>();
        int size = enchants.size();

        if (count > size) {
            count = size; // Limit the count to the available enchants
        }

        List<Enchant> availableEnchants = new ArrayList<>(enchants); // Create a copy of enchants list

        for (int i = 0; i < count; i++) {
            Enchant enchant = availableEnchants.remove(random.nextInt(availableEnchants.size()));
            randomEnchants.add(enchant);
        }

        return randomEnchants;
    }

    public int getRandomBuff() {
        int minValue = -20;
        int maxValue = 40;
        int firstPart = random.nextInt(maxValue - minValue) + minValue;
        int secondPart = random.nextInt(maxValue) + 1;
        return random.nextBoolean() ? firstPart : secondPart;
    }

    public void forgeEnchants(Player player) {

        int enchantsCount = random.nextInt(4) + 1; // Random count between 1 and 4
        List<Enchant> randomEnchants = getRandomEnchants(enchantsCount);

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player);

        playerEnchantments.getEnchantBuffs().clear();
        playerEnchantments.getBuffedEnchants().clear();

        for (Enchant enchant : randomEnchants) {
            int buff = getRandomBuff();
            playerEnchantments.setEnchantBuffs(enchant, buff);
            playerEnchantments.setBuffedEnchants(enchant);
            player.sendMessage(enchant.getName() + " -> " + buff);
        }
    }
}

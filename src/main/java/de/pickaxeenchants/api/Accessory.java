package de.pickaxeenchants.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Random;

public class Accessory {

    Enchant enchant;
    int level;
    float boost;

    int max_level = 4;

    public Accessory(Enchant enchant, int level) {
        this.enchant = enchant;
        this.level = level;
        this.boost = generateBoost();
        System.out.println(boost);
        System.out.println(getLevel());
    }
    public Accessory(Enchant enchant, int level, float boost) {
        this.enchant = enchant;
        this.level = level;
        this.boost = boost;
    }

    public Enchant getEnchant() {
        return enchant;
    }

    public void setEnchant(Enchant enchant) {
        this.enchant = enchant;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public ItemStack createCustomItem() {
        ItemStack itemStack;
        switch (getLevel()) {
            case 1:
                itemStack = new ItemStack(Material.COAL);
                break;
            case 2:
                itemStack = new ItemStack(Material.IRON_INGOT);
                break;
            case 3:
                itemStack = new ItemStack(Material.GOLD_INGOT);
                break;
            case 4:
                itemStack = new ItemStack(Material.DIAMOND);
                break;
            default:
                itemStack = new ItemStack(Material.STONE);
                break;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            String enchant_name = getEnchant().getName();
            meta.setDisplayName("§6§l" + enchant_name + " Accessory Tier " + getLevel());
            ArrayList<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§6Boost: " + boost + "§6%");
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    public float generateBoost() {
        Random random = new Random();
        float minBoost, maxBoost;

        int t = getLevel();

        switch (t) {
            case 1:
                minBoost = 1.0f;
                maxBoost = 12.0f;
                break;
            case 2:
                minBoost = 8.0f;
                maxBoost = 20.0f;
                break;
            case 3:
                minBoost = 15.0f;
                maxBoost = 30.0f;
                break;
            case 4:
                minBoost = 25.0f;
                maxBoost = 40.0f;
                break;
            default:
                minBoost = 0.0f;
                maxBoost = 0.0f;
                break;
        }
        boost = minBoost + random.nextFloat() * (maxBoost - minBoost);
        boost = (float) (Math.round(boost * 100.0) / 100.0);
        return boost;
    }

    public int getMax_level() {
        return max_level;
    }
    public boolean isMaxLevel(){
        return (getLevel() == getMax_level());
    }
}

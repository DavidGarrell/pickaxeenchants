package de.pickaxeenchants.api;

import org.bukkit.Material;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Enchant {


    private final String name;
    private final Material material;
    private final int maxLevel;

    private final String description;

    private final double baseChance;
    private final double chanceIncrease;

    private final long baseCost;
    private final long costFactor;

    private long procs;

    private Accessory accessory;

    private final List<EnchantTypes> enchantTypes;

    private final int unlock_level;

    public Enchant(String name, Material material, int maxLevel, String description, double baseChance, double chanceIncrease, long baseCost, long costFactor, int unlock_level, List<EnchantTypes> enchantTypes) {
        this.name = name;
        this.material = material;
        this.maxLevel = maxLevel;
        this.description = description;
        this.baseChance = baseChance;
        this.baseCost = baseCost;
        this.chanceIncrease = chanceIncrease;
        this.costFactor = costFactor;
        this.unlock_level = unlock_level;
        this.enchantTypes = enchantTypes;
    }



    public String getName() {
        return name;
    }
    public Material getMaterial() {
        return material;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getDescription() {
        return description;
    }

    public double getBaseChance() {
        return baseChance;
    }

    public double getChanceIncrease() {
        return chanceIncrease;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public double getCostFactor() {
        return costFactor;
    }

    public int getUnlock_level() {
        return unlock_level;
    }

    public List<EnchantTypes> getEnchantTypes() {
        return enchantTypes;
    }

    public void procEnchantment(){
        procs+=1;
    }

    public long getProcs() {
        return procs;
    }

    public void setProcs(long procs) {
        this.procs = procs;
    }

    public Accessory getAccessory() {
        return accessory;
    }

    public void setAccessory(Accessory accessory) {
        this.accessory = accessory;
    }
}



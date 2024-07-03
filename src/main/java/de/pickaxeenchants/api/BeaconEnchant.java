package de.pickaxeenchants.api;

import org.bukkit.Material;

public class BeaconEnchant {

    private final String name;
    private final Material material;
    private final int maxLevel;

    private final String description;

    private final double baseChance;
    private final double chanceIncrease;

    private final double baseCost;
    private final double costFactor;

    public BeaconEnchant(String name, Material material, int maxLevel, String description, double baseChance, double chanceIncrease, double baseCost, double costFactor) {
        this.name = name;
        this.material = material;
        this.maxLevel = maxLevel;
        this.description = description;
        this.baseChance = baseChance;
        this.baseCost = baseCost;
        this.chanceIncrease = chanceIncrease;
        this.costFactor = costFactor;
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

}

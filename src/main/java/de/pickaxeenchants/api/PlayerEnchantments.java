package de.pickaxeenchants.api;

import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.pickaxeenchants.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerEnchantments {

    private final Map<Enchant, Integer> enchantLevels;
    private Map<Enchant, Integer> enchantBuffs;

    private HashMap<Enchant, Accessory> accessoryHashMap;

    private HashMap<Integer, Accessory> mergeAccessoryMap = new HashMap<>();

    ArrayList<Enchant> buffedEnchants;
    private final Map<BeaconEnchant, Integer> beaconEnchantLevels;

    private final Player player;

    public PlayerEnchantments(Player player) {
        this.enchantLevels = new HashMap<>();
        this.beaconEnchantLevels = new HashMap<>();
        this.player = player;
        this.buffedEnchants = new ArrayList<>();
        this.accessoryHashMap = new HashMap<>();

        for(Enchant enchant : Main.enchantInitiazer.getEnchants()){
            if(enchant.getName().equalsIgnoreCase("Token_Finder")) {
                upgradeEnchant(enchant, 100);
            }
            if(enchant.getName().equalsIgnoreCase("Layer")) {
                upgradeEnchant(enchant, 25);
            }
        }
    }
    public Player getPlayer() {
        return player;
    }

    public void setEnchantBuffs(Enchant enchant, Integer enchantBuff) {
        enchantBuffs.put(enchant, enchantBuff);
    }

    public ArrayList<Enchant> getBuffedEnchants() {
        return buffedEnchants;
    }

    public void setBuffedEnchants(Enchant buffedEnchantments) {
        buffedEnchants.add(buffedEnchantments);
    }

    public int getBuff(Enchant enchant) {
        return enchantBuffs.getOrDefault(enchant, 1);
    }

    public Map<Enchant, Integer> getEnchantBuffs() {
        if (enchantBuffs == null) {
            enchantBuffs = new HashMap<>();
        }
        return enchantBuffs;
    }

    public void setEnchantLevel(Enchant enchant, int level) {
        enchantLevels.put(enchant, level);
    }
    public int getEnchantLevel(Enchant enchant) {
        return enchantLevels.getOrDefault(enchant, 0);
    }
    public boolean hasTalt(Enchant enchant) {
        return enchantLevels.containsKey(enchant);
    }

    public void upgradeEnchant(Enchant enchant, int levelIncrease) {
        int currentLevel = enchantLevels.getOrDefault(enchant, 0);
        int newLevel = currentLevel + levelIncrease;
        if(newLevel<=enchant.getMaxLevel()) {
            enchantLevels.put(enchant, newLevel);
        }
    }

    public void upgradeBeaconEnchant(BeaconEnchant enchant, int levelIncrease) {
        int currentLevel = beaconEnchantLevels.getOrDefault(enchant, 0);
        int newLevel = currentLevel + levelIncrease;
        if(newLevel<=enchant.getMaxLevel()) {
            beaconEnchantLevels.put(enchant, newLevel);
        }
    }

    public void addBlocksToBackPack(Player player, Material material, Long amount){

        BackPack backPack = de.backpack.main.Main.backPack;
        BackPackAPI backPackAPI = backPack.getPlayer_backpacks().get(player.getUniqueId());
        backPackAPI.addBlocks(material, amount);
    }


    public void resetEnchant() {
        enchantLevels.clear();
    }


    public boolean isMaxLevel(Enchant enchant){
        int currentLevel = enchantLevels.getOrDefault(enchant, 0);
        return currentLevel == enchant.getMaxLevel();
    }

    public void removeEnchant(Enchant enchant) {
        enchantLevels.remove(enchant);
    }


    public void setBeaconEnchantLevel(BeaconEnchant enchant, int level) {
        beaconEnchantLevels.put(enchant, level);
    }
    public int getBeaconEnchantLevel(BeaconEnchant enchant) {
        return beaconEnchantLevels.getOrDefault(enchant, 0);
    }

    public double calcActivateChance(Enchant enchant){

        double chance = 0;
        chance = (enchant.getBaseChance() + (enchant.getChanceIncrease() * getEnchantLevel(enchant)));

        if(getEnchantLevel(enchant)==0){
            chance=0;
        }

        if(getBuff(enchant)>0){
            chance*= calcBuffs(enchant);
        }


        if(chance*100>=100) {
            chance=1;
        }

        return chance;
    }

    public float calcBuffs(Enchant enchant){

        UserManager userManager = Main.userManager;

        float e_buff = getBuff(enchant);
        float buff = e_buff;

        if(getAccessoryHashMap().containsKey(enchant)) {
            buff *= 1+(getAccessoryHashMap().get(enchant).boost);
        }


        return 1+(buff/100);
    }

    public void addAccessory(Enchant enchant, Accessory accessory){
        enchant.setAccessory(accessory);

        if(!accessoryHashMap.containsKey(enchant)) {
            accessoryHashMap.put(enchant, accessory);
        }
    }

    public void removeAccessory(Enchant enchant){
        if (accessoryHashMap.containsKey(enchant)){
            player.getInventory().addItem(accessoryHashMap.get(enchant).createCustomItem());
            accessoryHashMap.remove(enchant);
        }
    }

    public HashMap<Enchant, Accessory> getAccessoryHashMap() {
        return accessoryHashMap;
    }

    public void addMergeAccessory(Accessory accessory){

        if(mergeAccessoryMap.size() <4){
            mergeAccessoryMap.put(mergeAccessoryMap.size()+1, accessory);
        }
    }

    public void removeMergeAccessory(int id){

        player.getInventory().addItem(mergeAccessoryMap.get(id).createCustomItem());
        mergeAccessoryMap.remove(id);

    }

    public void mergeAccessory(){
        Accessory accessory = new Accessory(getMergeAccessoryMap().get(1).getEnchant(), getMergeAccessoryMap().get(1).getLevel()+1);
        getPlayer().getInventory().addItem(accessory.createCustomItem());
        getMergeAccessoryMap().clear();
    }

    public HashMap<Integer, Accessory> getMergeAccessoryMap() {
        return mergeAccessoryMap;
    }

    public boolean canMerge() {
        if (getMergeAccessoryMap().isEmpty() || getMergeAccessoryMap().size() != 4) {
            return false;
        }


        Map.Entry<Integer, Accessory> firstEntry = getMergeAccessoryMap().entrySet().iterator().next();
        Enchant firstEnchant = firstEntry.getValue().getEnchant();
        int firstLevel = firstEntry.getValue().getLevel();


        for (Map.Entry<Integer, Accessory> entry : getMergeAccessoryMap().entrySet()) {
            Accessory accessory = entry.getValue();
            if (accessory.getEnchant() != firstEnchant || accessory.getLevel() != firstLevel || accessory.isMaxLevel()) {
                return false;
            }
        }

        return true; // Alle Accessoires haben denselben Verzauberungstyp und dasselbe Level
    }
}




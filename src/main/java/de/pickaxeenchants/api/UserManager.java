package de.pickaxeenchants.api;

import de.backpack.apfloat.Apfloat;
import de.backpack.apfloat.ApfloatMath;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import skyblock.api.Island;
import skyblock.api.IslandManager;
import skyblock.main.Main;

import java.util.HashMap;
import java.util.UUID;

import static de.backpack.main.Main.economyAPI;

public class UserManager {
    private HashMap<UUID, PlayerEnchantments> playerPlayerEnchantments = new HashMap<>();
    private HashMap<UUID, Integer> mastery = new HashMap<>();

    private HashMap<UUID, Integer> pickaxeLevel = new HashMap<>();

    private HashMap<UUID, Integer> pickaxeXP = new HashMap<>();

    private HashMap<UUID, Apfloat> tokens_gain_minute = new HashMap<>();
    private HashMap<UUID, Apfloat> money_gain_minute = new HashMap<>();


    double baseCostMastery = 10;
    double costFactorMastery = 1.1;

    double baseBeaconFinder = 0.5;

    public HashMap<UUID, PlayerEnchantments> getPlayerPlayerEnchantments() {
        return playerPlayerEnchantments;
    }

    public void setPlayerPlayerEnchantments(Player player, PlayerEnchantments playerEnchantments) {
        playerPlayerEnchantments.put(player.getUniqueId(), playerEnchantments);
    }

    public void setPickaxeLevel(Player player, int level){
        pickaxeLevel.put(player.getUniqueId(),level);
    }

    public void addPickAxeLevel(Player player, int amount){

        UUID uuid = player.getUniqueId();
        pickaxeLevel.replace(uuid, pickaxeLevel.get(uuid)+amount);
        pickaxeXP.put(uuid, 0);
        player.setLevel(pickaxeLevel.get(uuid));

    }
    public void setPickaxeXP(Player player, int xp){
        pickaxeXP.put(player.getUniqueId(),xp);
    }
    public void addPickaxeXP(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int currentXP = getPickaxeXP(player);
        int newXP = currentXP + amount;

        float expToLevel = calcPickaxeXPneed(player);
        float expProgress = (float) newXP / expToLevel;

        // Ensure the experience progress is within the valid range
        expProgress = Math.min(Math.max(expProgress, 0.0f), 1.0f);

        player.setExp(expProgress);
        pickaxeXP.replace(uuid, newXP);
    }

    public void setPlayerMastery(Player player, Integer playerMastery) {
        mastery.put(player.getUniqueId(), playerMastery);
    }


    public Apfloat calcCostTest(int level, int base, int factor) {
        Apfloat baseCost = new Apfloat(base);
        Apfloat totalCost = Apfloat.ZERO;
        Apfloat multi_factor = new Apfloat(1.0020);

        for (int x = 0; x < level; x++) {
            totalCost = totalCost.add(baseCost.add(new Apfloat(factor).multiply(ApfloatMath.pow(multi_factor,x))));
        }

        return totalCost.truncate();
    }

    public Apfloat calcCostOldVersion(Enchant enchant, Player player, int level){

        PlayerEnchantments playerEnchantments = getPlayerPlayerEnchantments().get(player.getUniqueId());

        Apfloat baseCost = new Apfloat(enchant.getBaseCost());
        Apfloat costFactor = new Apfloat(enchant.getCostFactor());

        Apfloat totalCost = Apfloat.ZERO;


        for (int x = 0; x < level; x++) {
            Apfloat levelCost = baseCost.multiply((ApfloatMath.pow(costFactor, playerEnchantments.getEnchantLevel(enchant) + x)));
            totalCost = totalCost.add(levelCost);
        }

        return totalCost.truncate();
    }

    public Apfloat calcCost(Enchant enchant, Player player, int level) {
        PlayerEnchantments playerEnchantments = getPlayerPlayerEnchantments().get(player.getUniqueId());

        Apfloat baseCost = new Apfloat(enchant.getBaseCost());
        Apfloat costFactor = new Apfloat(enchant.getCostFactor());
        Apfloat totalCost = Apfloat.ZERO;


        int enchant_level = playerEnchantments.getEnchantLevel(enchant);

        for (int x = 0; x < level; x++) {
            totalCost = totalCost.add(baseCost.add(costFactor.multiply(new Apfloat(enchant_level+x))));
        }

        return totalCost.truncate();
    }


    public int caclLevelAll(Enchant enchant, Player player) {
        PlayerEnchantments playerEnchantments = getPlayerPlayerEnchantments().get(player.getUniqueId());

        Apfloat baseCost = new Apfloat(enchant.getBaseCost());
        Apfloat costFactor = new Apfloat(enchant.getCostFactor());
        Apfloat totalCost = Apfloat.ZERO;
        int amount = 0;


        int enchant_level = playerEnchantments.getEnchantLevel(enchant);
        int enchant_maxlevel = enchant.getMaxLevel();

        for (int x = 0; x < enchant_maxlevel; x++) {
            totalCost = totalCost.add(baseCost.add(costFactor.multiply(new Apfloat(enchant_level+x))));
            if(totalCost.compareTo(economyAPI.getTokens(player)) <= 0 && amount+enchant_level<enchant_maxlevel){
                amount++;
            } else {
                break;
            }
        }



        return amount;
    }

    public long calcCostBeaconEnchant(BeaconEnchant enchant, Player player, int level){

        PlayerEnchantments playerEnchantments = getPlayerPlayerEnchantments().get(player.getUniqueId());

        double baseCost = enchant.getBaseCost();
        double costFactor = enchant.getCostFactor();

        double totalCost = 0.0;

        for (int x = 0; x < level; x++) {
            double levelCost = baseCost * Math.pow(costFactor, playerEnchantments.getBeaconEnchantLevel(enchant) + x);
            totalCost += levelCost;
        }

        return (int) totalCost;
    }
    public long calcCostMastery(Player player){

        double baseCost = baseCostMastery;
        double costFactor = costFactorMastery;

        double totalCost = 0.0;

        double levelCost = baseCost * Math.pow(costFactor, getMastery(player) + 1);
        totalCost += levelCost;

        return (long) totalCost;
    }

    public int calcPickaxeXPneed(Player player){

        int level = pickaxeLevel.get(player.getUniqueId());
        int xp = pickaxeXP.get(player.getUniqueId());

        int xpneed = (int) (100*(1.15*level));


        return xpneed;
    }

    public void upgradeMastery(Player player){
        mastery.put(player.getUniqueId(), mastery.get(player.getUniqueId()) + 1);
    }

    public int getMastery(Player player){
        return mastery.get(player.getUniqueId());
    }
    public int getPickaxeLevel(Player player){
        return pickaxeLevel.get(player.getUniqueId());
    }
    public int getPickaxeXP(Player player){
        return pickaxeXP.get(player.getUniqueId());
    }

    public HashMap<UUID, Integer> getPickaxeLevel() {
        return pickaxeLevel;
    }

    public int buffCost(){
        return 5000;
    }



    public float calculateMasteryBeaconBuff(Player player){

        return (float) (1+getMastery(player)*0.1);
    }

    public float calculateMasteryEnchantBuff(Player player){

        return (float) (1+getMastery(player)*0.001);
    }

    public double calculateMasteryChance(Player player){

        return baseBeaconFinder*calculateMasteryBeaconBuff(player)/100;
    }

    public boolean canPickaxeLevelUp(Player player){

        return pickaxeXP.get(player.getUniqueId()) >= calcPickaxeXPneed(player);

    }

    public Apfloat getTokensGainPerMinute(Player player){
        return tokens_gain_minute.getOrDefault(player.getUniqueId(), new Apfloat(0));
    }

    public void addTokensGainPerMinute(Player player, Apfloat tokens){

        if(!tokens_gain_minute.containsKey(player.getUniqueId())){
            tokens_gain_minute.put(player.getUniqueId(), tokens);
        } else {
            tokens_gain_minute.replace(player.getUniqueId(), getTokensGainPerMinute(player).add(tokens));
        }
    }

    public void resetTokensGainPerMinute(Player player){
        if(tokens_gain_minute.containsKey(player.getUniqueId())){
            tokens_gain_minute.replace(player.getUniqueId(), new Apfloat(0));
        }
    }

    public Apfloat getMoneyGainPerMinute(Player player){
        return money_gain_minute.getOrDefault(player.getUniqueId(), new Apfloat(0));
    }
    public void addMoneyGainPerMinute(Player player, Apfloat money){

        if(!money_gain_minute.containsKey(player.getUniqueId())){
            money_gain_minute.put(player.getUniqueId(), money);
        } else {
            money_gain_minute.replace(player.getUniqueId(), getMoneyGainPerMinute(player).add(money));
        }
    }

    public void resetMoneyGainPerMinute(Player player){
        if(money_gain_minute.containsKey(player.getUniqueId())){
            money_gain_minute.replace(player.getUniqueId(), new Apfloat(0));
        }
    }
    public void addTokensFormEnchant(Player player, Apfloat amount, Enchant enchant){

        Apfloat multi = economyAPI.calcPrestigeMultiplier(player);

        Apfloat tokens = amount.multiply(multi);

        economyAPI.addTokens(player, tokens);

        addTokensGainPerMinute(player, tokens);

    }

    public void addMoneyFormEnchant(Player player, Apfloat amount, Enchant enchant){

        Apfloat multi = new Apfloat(1);

        IslandManager islandManager = Main.islandManager;
        Island island = islandManager.island.get(player.getUniqueId());

        multi = economyAPI.calcPrestigeMultiplier(player);

        Apfloat money = amount.multiply(island.calcBaseValueAverage().multiply(multi));

        economyAPI.addCashBalance(player, money);

        addMoneyGainPerMinute(player, money);

    }

}

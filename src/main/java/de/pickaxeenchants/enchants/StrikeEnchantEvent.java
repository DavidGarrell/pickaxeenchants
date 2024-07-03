package de.pickaxeenchants.enchants;

import de.backpack.apfloat.Apfloat;
import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.listener.UnlimitedNumber;
import de.backpack.main.Main;
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StrikeEnchantEvent implements Listener {
    private List<Enchant> enchants;
    private double blocks_amount = 0;
    private int blocks_xp = 0;
    private List<BackPackAPI> backpacks;

    private Island island;

    public static Plugin plugin;

    private int tokens_per_block = 100000000;

    public StrikeEnchantEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)

    public void onBreak(BlockBreakEvent event) {


        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());

        EconomyAPI economyAPI = Main.economyAPI;



        if (event instanceof ExplosiveEnchant) return;
        //Make sure we didn't call it ourselves.
        if (event.isCancelled())
            return;

        int level = 0;

        int level_multiply = 0;
        Enchant e = null;
        for(Enchant enchant : enchants) {

            if(enchant.getName().equalsIgnoreCase("Strike")){
                level = playerEnchantments.getEnchantLevel(enchant);
                e=enchant;
            }
        }
        double buffMultiplier = playerEnchantments.calcBuffs(e);

        int radius_1 = 4;
        int radius_2 = level /500;
        int radius = radius_1 + radius_2;
        radius= (int) (radius*(1 + buffMultiplier / 100));


        Material material = event.getBlock().getType();


        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();
        if (Math.random() < playerEnchantments.calcActivateChance(e)) {

            Objects.requireNonNull(event.getBlock().getLocation().getWorld()).strikeLightning(event.getBlock().getLocation());

            double multiply = 1;

            if(Math.random() < multiplyEnchant.proc_chance(event.getPlayer())){
                multiply =multiplyEnchant.multi();
                event.getPlayer().sendMessage("§e§lStrike Multiply: §f" + new DecimalFormat("#.###").format(multiply));
            }
            Location l1 = event.getBlock().getLocation();
            for (Location loc : Explosive(l1, (int) (radius*multiply), false, event.getPlayer())) {
                BlockChanger.setBlock(loc, Material.AIR);
                blocks_amount+=tokens_per_block;
                blocks_xp+=1;
                if (Math.random() < 0.0004) {
                    loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
                }
            }

            economyAPI.addXPBalance(event.getPlayer(), blocks_xp);
            economyAPI.addBlocks(event.getPlayer(), blocks_xp);
            userManager.addMoneyFormEnchant(event.getPlayer(), new Apfloat(blocks_amount/tokens_per_block), e);

            for (Location loc : Explosive1(l1, (int) (radius*multiply)+1, false, event.getPlayer()))
                if (Math.random() < 0.4) BlockChanger.setBlock(loc, Material.AIR);
            for (Location loc : Explosive1(l1, (int) (radius*multiply)+2, false, event.getPlayer()))
                if (Math.random() < 0.25) BlockChanger.setBlock(loc, Material.AIR);

            if(material == Material.EMERALD_BLOCK) {
                blocks_amount *= 10;

            }
            UnlimitedNumber unlimitedNumber = new UnlimitedNumber(String.valueOf(blocks_amount));
            event.getPlayer().sendMessage("§e§lStrike: §fhas received " + unlimitedNumber.format() + " §fTokens!");


            userManager.addTokensFormEnchant(event.getPlayer(), new Apfloat(blocks_amount), e);
            blocks_amount=0;
            blocks_xp=0;

        }


    }

    public static List<Location> Explosive(Location centerBlock, int radius, boolean hollow, Player player) {
        List<Location> circleBlocks = new ArrayList<Location>();
        int bX = centerBlock.getBlockX();
        int bY = centerBlock.getBlockY();
        int bZ = centerBlock.getBlockZ();
        IslandManager islandManager = skyblock.main.Main.islandManager;

        for (double x = bX - radius; x <= bX + radius; x++) {
            for (double y = bY - radius - 45; y <= bY; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {

                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * 0.5 * (bY - y) * 0.5));

                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location loc = new Location(centerBlock.getWorld(), x, y, z);
                        if (loc.getBlock().getType() != Material.BEDROCK && loc.getBlock().getType() != Material.AIR && islandManager.island.get(player.getUniqueId()).checkIfBlockIsInMine(loc)) {
                            circleBlocks.add(loc);
                        }
                    }
                }
            }
        }
        return circleBlocks;
    }
    public static List<Location> Explosive1(Location centerBlock, int radius, boolean hollow, Player player) {
        List<Location> circleBlocks = new ArrayList<Location>();
        int bX = centerBlock.getBlockX();
        int bY = centerBlock.getBlockY();
        int bZ = centerBlock.getBlockZ();
        IslandManager islandManager = skyblock.main.Main.islandManager;

        for (double x = bX - radius; x <= bX + radius; x++) {
            for (double y = bY - 3; y <= bY; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {

                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * 0.5 * (bY - y) * 0.5));

                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location loc = new Location(centerBlock.getWorld(), x, y, z);
                        if (loc.getBlock().getType() != Material.BEDROCK && loc.getBlock().getType() != Material.AIR && islandManager.island.get(player.getUniqueId()).checkIfBlockIsInMine(loc)) {
                            circleBlocks.add(loc);
                        }
                    }
                }
            }
        }
        return circleBlocks;
    }
}


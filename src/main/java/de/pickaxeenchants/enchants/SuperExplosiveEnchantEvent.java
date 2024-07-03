package de.pickaxeenchants.enchants;

import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.*;
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

public class SuperExplosiveEnchantEvent implements Listener {
    private List<BeaconEnchant> enchants;
    private double blocks_amount = 0;
    private int blocks_xp = 0;
    private List<BackPackAPI> backpacks;

    private Island island;

    public static Plugin plugin;

    public SuperExplosiveEnchantEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)

    public void onBreak(BlockBreakEvent event) {


        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getBeaconEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());

        EconomyAPI economyAPI = Main.economyAPI;



        if (event instanceof ExplosiveEnchant) return;
        //Make sure we didn't call it ourselves.
        if (event.isCancelled())
            return;

        int level = 0;
        double chance = 0;

        int level_multiply = 0;

        for(BeaconEnchant enchant : enchants) {

            if(enchant.getName().equalsIgnoreCase("Super Explosive")){
                level = playerEnchantments.getBeaconEnchantLevel(enchant);
                chance = (enchant.getBaseChance() + (enchant.getChanceIncrease() * playerEnchantments.getBeaconEnchantLevel(enchant)));

                if(playerEnchantments.getBeaconEnchantLevel(enchant)==0){
                    chance=0;
                }
            }
        }



        int radius = 32;

        Fortune fortune = new Fortune();

        double multi = fortune.blocksMulti(event.getPlayer());

        Material material = event.getBlock().getType();

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        if (Math.random() < chance) {

            double multiply = 1;

            if(Math.random() < multiplyEnchant.proc_chance(event.getPlayer())){
                multiply =multiplyEnchant.multi();
                multi*=multiply;
                event.getPlayer().sendMessage("§6§lSuper Explosive Multiply: §f" + new DecimalFormat("#.###").format(multiply));
            }
            Location l1 = event.getBlock().getLocation();
            for (Location loc : Explosive(l1, (int) (radius*multiply), false, event.getPlayer())) {
                BlockChanger.setBlock(loc, Material.AIR);
                blocks_amount+=1*multi;
                blocks_xp+=1;
                if (Math.random() < 0.0004) {
                    loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
                }
            }


            for (Location loc : Explosive1(l1, (int) (radius*multiply)+1, false, event.getPlayer()))
                if (Math.random() < 0.4) BlockChanger.setBlock(loc, Material.AIR);
            for (Location loc : Explosive1(l1, (int) (radius*multiply)+2, false, event.getPlayer()))
                if (Math.random() < 0.25) BlockChanger.setBlock(loc, Material.AIR);

            event.getPlayer().sendMessage("§6§lSuper Explosive: §fhas received " + (int) blocks_amount + " §fblocks!");


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

                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * 0.9 * (bY - y) * 0.9));

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

                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * 0.9 * (bY - y) * 0.9));

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

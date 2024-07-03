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
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static skyblock.main.Main.islandManager;

public class LaserEnchantEvent implements Listener {
    private List<Enchant> enchants;
    private double blocks_amount = 0;
    private int blocks_xp = 0;
    private List<BackPackAPI> backpacks;
    private Island island;
    public static Plugin plugin;
    private static final int DRAGON_BREATH_RANGE = 20;
    private static int DRAGON_BREATH_DURATION = 20;

    private boolean active = false;

    private int tokens_per_block = 250000000;

    public LaserEnchantEvent(Plugin plugin) {
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
        if (event.isCancelled()) return;

        int level = 0;
        int level_multiply = 0;

        Enchant e = null;

        for (Enchant enchant : enchants) {
            if (enchant.getName().equalsIgnoreCase("Laser")) {
                level = playerEnchantments.getEnchantLevel(enchant);
                e = enchant;
            }
            if (enchant.getName().equalsIgnoreCase("Multiply")) {
                level_multiply = playerEnchantments.getEnchantLevel(enchant);
            }
        }

        double buffMultiplier = playerEnchantments.calcBuffs(e);
        int radius_1 = 4;
        int radius_2 = level / 625;
        int radius = radius_1 + radius_2;
        radius= (int) (radius*buffMultiplier);

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();
        Material material = event.getBlock().getType();

        if (Math.random() < playerEnchantments.calcActivateChance(e)) {
            if (!active) {
                active = true;
                event.getPlayer().sendMessage("§e§lLaser: §factivated");
                long FINAL_DRAGON_BREATH_DURATION = DRAGON_BREATH_DURATION+level;

                double multiply = 1 * userManager.calculateMasteryEnchantBuff(event.getPlayer());
                if (Math.random() < multiplyEnchant.proc_chance(event.getPlayer())) {
                    multiply += multiplyEnchant.multi()-1;
                    event.getPlayer().sendMessage("§e§lMultiply: §f" + new DecimalFormat("#.###").format(multiply));
                }

                economyAPI.addXPBalance(event.getPlayer(), blocks_xp);
                economyAPI.addBlocks(event.getPlayer(), (long) (blocks_xp));


                Enchant finalE = e;
                double finalMultiply = multiply;
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    active = false;

                    blocks_amount*= finalMultiply;

                    EmeraldRushEnchant emeraldRushEnchant = new EmeraldRushEnchant();
                    if(material == Material.EMERALD_BLOCK) {
                        blocks_amount *= emeraldRushEnchant.getMultiplier();

                    }
                    UnlimitedNumber unlimitedNumber = new UnlimitedNumber(String.valueOf(blocks_amount));
                    event.getPlayer().sendMessage("§e§lLaser: §fhas received " + unlimitedNumber.format() + " §fTokens!");
                    
                    userManager.addTokensFormEnchant(event.getPlayer(), new Apfloat(blocks_amount), finalE);
                    userManager.addMoneyFormEnchant(event.getPlayer(), new Apfloat(blocks_amount/tokens_per_block), finalE);

                    blocks_amount = 0;
                    blocks_xp = 0;

                }, (long) (FINAL_DRAGON_BREATH_DURATION*finalMultiply * 1L));
            }
        }
    }

    @EventHandler

    public void onLaserEnchant(BlockBreakEvent event) {

        if (active) {

            Enchant e = null;
            UserManager userManager = de.pickaxeenchants.main.Main.userManager;
            PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());


            for (Enchant enchant : enchants) {
                if (enchant.getName().equalsIgnoreCase("Laser")) {

                    e = enchant;
                }
            }

            double buffMultiplier = playerEnchantments.calcBuffs(e);
            int radius = 12;
            radius= (int) (radius*buffMultiplier);

            int finalRadius = radius;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

                Location playerLocation = event.getPlayer().getLocation();

                if(islandManager.island.get(event.getPlayer().getUniqueId()).checkIfBlockIsInMine(event.getBlock().getLocation())) {
                    Location directionLocation = playerLocation.clone().add(playerLocation.getDirection().multiply(2));

                    Location l1 = event.getBlock().getLocation();

                    Location l2 = event.getPlayer().getLocation();

                    l2.add(0, 1, 0);

                    Vector v = event.getPlayer().getEyeLocation().getDirection();

                    double x = v.getX();
                    double y = v.getY();
                    double z = v.getZ();

                    //Particle

                    int length = finalRadius+50;
                    double particleDistance = 0.05;

                    for (double waypoint = 1; waypoint < length; waypoint += particleDistance) {


                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 2.75F), true);
                        l2.getWorld().spawnParticle(Particle.LAVA, l2, 1);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 3.75F), true);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.YELLOW, 4.75F), true);

                        l2.add(x, y - 0.5, z);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 2.75F), true);
                        l2.getWorld().spawnParticle(Particle.LAVA, l2, 1);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 3.75F), true);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.YELLOW, 4.75F), true);
                        l2.add(x, y - 0.5, z);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 2.75F), true);
                        l2.getWorld().spawnParticle(Particle.LAVA, l2, 1);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 3.75F), true);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.YELLOW, 4.75F), true);
                        l2.add(x, y - 0.5, z);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 2.75F), true);
                        l2.getWorld().spawnParticle(Particle.LAVA, l2, 1);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 3.75F), true);
                        l2.getWorld().spawnParticle(Particle.REDSTONE, l2.getX(), l2.getY(), l2.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.YELLOW, 4.75F), true);
                        l2.add(x, y - 0.5, z);
                    }


                    for (int i = 0; i < 20; i++) {

                        for (Location loc : Explosive(l1, (int) finalRadius-i/2, false, event.getPlayer())) {
                            BlockChanger.setBlock(loc, Material.AIR);

                            blocks_amount+=tokens_per_block;
                            blocks_xp += 1;
                        }
                        for (Location loc : Explosive1(l1, (int) finalRadius+1-i/2, false, event.getPlayer())) {
                            if (Math.random() < 0.15) BlockChanger.setBlock(loc, Material.AIR);
                            blocks_amount+=tokens_per_block;
                            blocks_xp += 1;

                        }

                        l1.add(x*1.5, y - 2, z*1.5);

                    }
                }
            }, 1L);

        }

    }

    public static List<Location> Explosive(Location centerBlock, int radius, boolean hollow, Player player) {
        List<Location> circleBlocks = new ArrayList<Location>();
        int bX = centerBlock.getBlockX();
        int bY = centerBlock.getBlockY();
        int bZ = centerBlock.getBlockZ();
        IslandManager islandManager = skyblock.main.Main.islandManager;

        for (double x = bX - radius; x <= bX + radius; x++) {
            for (double y = bY - radius - 45; y <= bY +radius; y++) {
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
            for (double y = bY - radius; y <= bY + radius; y++) {
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

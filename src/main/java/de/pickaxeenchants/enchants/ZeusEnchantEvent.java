package de.pickaxeenchants.enchants;

import de.backpack.apfloat.Apfloat;
import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.*;
import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ZeusEnchantEvent implements Listener {
    public static Plugin plugin;

    private List<BeaconEnchant> enchants;
    private double blocks_amount = 0;
    private int blocks_xp = 0;
    private List<BackPackAPI> backpacks;
    Fortune fortune = new Fortune();

    public ZeusEnchantEvent(Plugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {


        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getBeaconEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());
        EconomyAPI economyAPI = Main.economyAPI;

        Player player = event.getPlayer();
        Material material = event.getBlock().getType();


        // Get the location of the block that was broken
        final Location location = event.getBlock().getLocation();

        // Get the world where the block was broken
        final World world = location.getWorld();

        // Spawn the meteor particles above the block
        int level = 0;
        double chance = 0;
        for (BeaconEnchant enchant : enchants) {

            if (enchant.getName().equalsIgnoreCase("Zeus")) {
                level = playerEnchantments.getBeaconEnchantLevel(enchant);
                chance = (enchant.getBaseChance() + (enchant.getChanceIncrease() * playerEnchantments.getBeaconEnchantLevel(enchant)));

                if(playerEnchantments.getBeaconEnchantLevel(enchant)==0){
                    chance=0;
                }
            }
        }



        int num = 150;
        long delay = 2; // Delay between each lightning strike in ticks (0.5 seconds)

        if (Math.random() < chance) {

            IslandManager islandManager = skyblock.main.Main.islandManager;
            Island island = islandManager.island.get(player.getUniqueId());

            Location loc = island.getCenterBlockLocation();

            BukkitTask task = null; // Declaring task variable

            for (int i = 0; i < num; i++) {
                final int index = i;
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                task = scheduler.runTaskLater(plugin, () -> {
                    summonLightning(loc, 30, false, event.getPlayer());
                    if (index == num - 1) {
                        BukkitScheduler finalScheduler = scheduler;
                        finalScheduler.runTaskLater(plugin, () -> {
                            userManager.addMoneyFormEnchant(player, new Apfloat(blocks_amount), null);
                            event.getPlayer().sendMessage("§6§lZeus: §fhas received " + (int) blocks_amount + " §fblocks!");
                            blocks_amount = 0;
                        }, 12L);
                    }
                }, delay * i);
            }
        }
    }

    public void summonLightning(Location center, int radius, boolean hollow, Player player) {
        World world = center.getWorld();
        Random random = new Random();

        double offsetX = random.nextDouble() * radius * 2 - radius;
        double offsetZ = random.nextDouble() * radius * 2 - radius;

        Location targetLocation = center.clone().add(offsetX, 0, offsetZ);

        LightningStrike lightning = world.strikeLightning(targetLocation);
        lightning.setVisualFire(false);
        explodeHighestBlock(lightning.getLocation(), radius, false, player);
    }

    public void explodeHighestBlock(Location targetLocation, int radius, boolean hollow, Player player) {
        World world = targetLocation.getWorld();
        double multi = fortune.blocksMulti(player);
        int highestY = world.getHighestBlockYAt(targetLocation);
        Location highestBlockLocation = new Location(world, targetLocation.getX(), highestY, targetLocation.getZ());
        Block highestBlock = world.getBlockAt(highestBlockLocation);


        IslandManager islandManager = skyblock.main.Main.islandManager;
        Island island = islandManager.island.get(player.getUniqueId());
        long xp_per_proc = island.calcXPNeedNextLevel()/10;
        int xp = 0;


        Location l1 = highestBlock.getLocation();
        for (Location loc : Explosive(l1, 17, false, player)) {
            BlockChanger.setBlock(loc, Material.AIR);
            blocks_amount+=1;
        }
        for (Location loc : Explosive(l1, 18, false, player)) {
            if(Math.random()<0.3) {
                BlockChanger.setBlock(loc, Material.AIR);
            }
        }
        xp+=xp_per_proc;
        EconomyAPI economyAPI = de.backpack.main.Main.economyAPI;
        economyAPI.addXPBalance(player, xp);
    }



    public static List<Location> Explosive(Location centerBlock, int radius, boolean hollow, Player player) {
        List<Location> circleBlocks = new ArrayList<Location>();
        int bX = centerBlock.getBlockX();
        int bY = centerBlock.getBlockY();
        int bZ = centerBlock.getBlockZ();
        IslandManager islandManager = skyblock.main.Main.islandManager;

        for (double x = bX - radius; x <= bX + radius; x++) {
            for (double y = bY - radius; y <= bY +radius; y++) {
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

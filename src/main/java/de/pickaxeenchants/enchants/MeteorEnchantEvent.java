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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MeteorEnchantEvent implements Listener {
    public static Plugin plugin;

    private List<Enchant> enchants;
    private double blocks_amount = 0;
    private int blocks_xp = 0;
    private int tokens_per_block = 100000000;
    private int xp_per_block = 1;

    private boolean active = false;
    private List<BackPackAPI> backpacks;


    public MeteorEnchantEvent(Plugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){


        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());
        EconomyAPI economyAPI = Main.economyAPI;

        Player player = event.getPlayer();

        final Location location = event.getBlock().getLocation();

        final World world = location.getWorld();

        int level = 0;

        Enchant e = null;

        for (Enchant enchant : enchants) {
            if (enchant.getName().equalsIgnoreCase("Meteor")) {
                level = playerEnchantments.getEnchantLevel(enchant);
                e = enchant;
            }
        }


        Material material = event.getBlock().getType();
        double buffMultiplier = playerEnchantments.calcBuffs(e);

        int num = (int) ((level/5)*(buffMultiplier));

        if (Math.random() < playerEnchantments.calcActivateChance(e)) {

            if (!active) {
                active=true;

                Enchant finalE = e;
                new BukkitRunnable() {
                    int count = 0;

                    @Override
                    public void run() {
                        if (count < num + 1) {
                            int x = (int) (location.getX() + (Math.random() * 40) - 5);
                            int z = (int) (location.getZ() + (Math.random() * 40) - 5);
                            int y = world.getHighestBlockAt(x, z).getY();
                            Location targetLocation = new Location(world, x, y, z);
                            Meteor(targetLocation, event.getPlayer(), material, world);
                            count++;
                        } else {
                            this.cancel(); // Die Aufgabe nachdem die Zählvariable num erreicht wurde abbrechen
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                EmeraldRushEnchant emeraldRushEnchant = new EmeraldRushEnchant();
                                if(material == Material.EMERALD_BLOCK) {
                                    blocks_amount *= emeraldRushEnchant.getMultiplier();

                                }
                                UnlimitedNumber unlimitedNumber = new UnlimitedNumber(String.valueOf(blocks_amount));
                                event.getPlayer().sendMessage("§e§lMeteor: §fhas received " + unlimitedNumber.format() + " §fTokens!");

                                economyAPI.addXPBalance(player, blocks_xp);
                                economyAPI.addBlocks(player, blocks_xp);

                                userManager.addTokensFormEnchant(event.getPlayer(), new Apfloat(blocks_amount), finalE);
                                userManager.addMoneyFormEnchant(event.getPlayer(), new Apfloat(blocks_amount/tokens_per_block), finalE);
                                blocks_amount = 0;
                                blocks_xp = 0;
                                active = false;
                            }, 30L); // Nachricht und Belohnungen nach einer Verzögerung von 30 Tick senden
                        }
                    }
                }.runTaskTimer(plugin, 10L, 15L);
            }
        }
    }

    public void Meteor(Location location, Player player, Material material, World world){

        EconomyAPI economyAPI = Main.economyAPI;
        Fortune fortune = new Fortune();
        new BukkitRunnable() {

            double startY = location.getY() + 40.0;
            double endY = world.getHighestBlockAt(location).getY() + 0.5;
            double t = 0.0;
            double v = 0.6;
            double g = 0.3;

            int radius = 11;

            @Override
            public void run() {
                double y = startY - 0.5 * g * t * t;

                if (y < endY) {
                    // Break the block and create a small explosion when the meteor hits the ground
                    world.createExplosion(location.clone().add(0.5, 0.5, 0.5), 2F, false, false);
                    Location l1 = location;
                    for (Location loc : Explosive(l1, radius, false, player)) {
                        BlockChanger.setBlock(loc, Material.AIR);
                        blocks_amount+=tokens_per_block;
                        blocks_xp += xp_per_block;
                    }
                    for (Location loc : Explosive1(l1, radius + 1, false, player))
                        if (Math.random() < 0.4) BlockChanger.setBlock(loc, Material.AIR);
                    for (Location loc : Explosive1(l1, radius + 2, false, player))
                        if (Math.random() < 0.25) BlockChanger.setBlock(loc, Material.AIR);

                    cancel();
                    return;
                }

                // Spawn the meteor particles as they fall
                world.spawnParticle(Particle.SMOKE_LARGE, new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5), 10, 0.5, 0.5, 0.5, 0.1);
                world.spawnParticle(Particle.CRIT, new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5), 5, 0.45, 0.45, 0.45, 0.01);
                world.spawnParticle(Particle.FLAME, new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5), 5, 0.45, 0.45, 0.45, 0.01);
                world.spawnParticle(Particle.BLOCK_CRACK, new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5), 10, 0.2, 0.2, 0.2, 0.05, Material.STONE.createBlockData());

                t += v;
            }

        }.runTaskTimer(plugin, 0L, 1L);

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
            for (double y = bY - 3; y <= bY +radius; y++) {
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

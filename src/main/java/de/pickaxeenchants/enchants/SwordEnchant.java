package de.pickaxeenchants.enchants;

import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SwordEnchant implements Listener {
    public static Plugin plugin;


    public SwordEnchant(Plugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Location location = event.getBlock().getLocation();
        final World world = location.getWorld();

        // Spawn the sword particles above the block
        world.spawnParticle(Particle.CRIT_MAGIC, location.clone().add(0.5, 1.5, 0.5), 50, 0.2, 1.0, 0.2, 1.0);

        new BukkitRunnable() {
            double startY = location.getY() + 40.0;
            double endY = location.getY() + 0.5;
            double t = 0.0;
            double v = 0.6;
            double g = 0.3;
            int radius = 2;

            @Override
            public void run() {
                double y = startY - 0.5 * g * t * t;

                if (y < endY) {
                    // Break the block and create a small explosion when the sword hits the ground
                    world.createExplosion(location.clone().add(0.5, 0.5, 0.5), 2F, false, false);
                    Block block = location.getBlock();
                    block.breakNaturally();
                    Location l1 = event.getBlock().getLocation();
                    for (Location loc : Explosive(l1, radius, false)) {
                        BlockChanger.setBlock(loc, Material.AIR);
                    }

                    cancel();
                    return;
                }

                // Spawn the sword particles as they fall
                world.spawnParticle(Particle.CRIT_MAGIC, new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5), 1, 0.0, 0.0, 0.0, 0.0);
                world.spawnParticle(Particle.FIREWORKS_SPARK, new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5), 1, 0.0, 0.0, 0.0, 0.0);
                world.spawnParticle(Particle.EXPLOSION_NORMAL, new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5), 1, 0.0, 0.0, 0.0, 0.0);
                new BukkitRunnable() {
                    double y = startY;
                    double t = 0.0;
                    double v = 0.1;
                    double g = 0.3;

                    int radius = 1;

                    @Override
                    public void run() {
                        y -= v;
                        Location currentLoc = new Location(world, location.getX() + 0.5, y, location.getZ() + 0.5);

                        if (currentLoc.getBlock().getType() != Material.AIR) {
                            // Break the block and create a small explosion when the sword hits the ground
                            world.createExplosion(location.clone().add(0.5, 0.5, 0.5), 1.5F, false, false);

                            for (Location loc : Explosive(currentLoc, radius, false)) {
                                BlockChanger.setBlock(loc, Material.AIR);
                            }
                            cancel();
                            return;
                        }

                        // Spawn the sword particles as they fall
                        world.spawnParticle(Particle.CRIT_MAGIC, currentLoc, 1, 0.1, 0.1, 0.1, 0.0);
                        world.spawnParticle(Particle.FLAME, currentLoc, 1, 0.1, 0.1, 0.1, 0.0);
                        world.spawnParticle(Particle.BLOCK_DUST, currentLoc, 5, 0.3, 0.3, 0.3, 0.1, Material.IRON_BLOCK.createBlockData());

                        t += v;
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }
        };
    }

    public static List<Location> Explosive(Location centerBlock, int radius, boolean hollow) {
        List<Location> circleBlocks = new ArrayList<Location>();
        int bX = centerBlock.getBlockX();
        int bY = centerBlock.getBlockY();
        int bZ = centerBlock.getBlockZ();

        for (double x = bX - radius; x <= bX + radius; x++) {
            for (double y = bY - radius; y <= bY + radius; y++) {
                for (double z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * (bY - y)));

                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location loc = new Location(centerBlock.getWorld(), x, y, z);
                        if (loc.getBlock().getType() != Material.BEDROCK && loc.getBlock().getType() != Material.AIR) {
                            circleBlocks.add(loc);
                        }
                    }
                }
            }
        }
        return circleBlocks;
    }
}
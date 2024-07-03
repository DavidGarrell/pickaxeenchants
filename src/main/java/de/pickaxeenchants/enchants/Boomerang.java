package de.pickaxeenchants.enchants;

import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import skyblock.api.IslandManager;

import java.util.ArrayList;
import java.util.List;

import static skyblock.main.Main.islandManager;

public class Boomerang implements Listener {


    public static Plugin plugin;


    public Boomerang(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler

    public void onDragonBreathEnchant(BlockBreakEvent event) {

        double chance = 0;

        if (Math.random() < chance) {

            Player p = event.getPlayer();

            int radius = 10;
            Location center = event.getBlock().getLocation();

            ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
            Location dest = p.getLocation().add(p.getLocation().getDirection().multiply(10));

            event.setCancelled(true);
            stand.hasArms();
            stand.setGravity(false);
            stand.setItemInHand(new ItemStack(Material.BONE));

            stand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(120), Math.toRadians(0)));

            Vector v = event.getPlayer().getEyeLocation().getDirection();

            new BukkitRunnable() {
                double angle = 0;
                double y = center.getBlockY();

                public void run() {
                    double radians = Math.toRadians(angle);

                    double x = center.getX() + radius * Math.cos(radians);
                    double z = center.getZ() + radius * Math.sin(radians);

                    if (angle <= 180) {
                        if (v.getY() < 0) {
                            y += v.getY() / 2;
                        } else {
                            y -= v.getY() / 2;
                        }
                    } else if (angle >= 180) {
                        if (v.getY() < 0) {
                            y -= v.getY() / 2;
                        } else {
                            y += v.getY() / 2;
                        }
                    }
                    Location newPosition = new Location(center.getWorld(), x, y, z);
                    stand.teleport(newPosition);
                    angle += 5;
                    if (angle >= 360) {
                        angle = 0;
                        cancel();
                        stand.remove();
                    }

                    for (Location loc : Explosive(newPosition, (int) 5, false, event.getPlayer())) {
                        BlockChanger.setBlock(loc, Material.AIR);
                    }
                    for (Location loc : Explosive(newPosition, (int) 6, true, event.getPlayer())) {
                        if (Math.random() < 0.4) BlockChanger.setBlock(loc, Material.AIR);
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);
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
}

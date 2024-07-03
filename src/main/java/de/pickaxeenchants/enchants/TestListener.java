package de.pickaxeenchants.enchants;

import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;

public class TestListener implements Listener {
    private final int LENGTH = 7; // Länge des Quaders
    private final int WIDTH = 40; // Breite des Quaders
    private final int DEPTH = 35; // Tiefe des Quaders
    private final int MAX_HEIGHT = 5; // Maximale Höhe des Spitzens

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        Random random = new Random();
        for (int x = location.getBlockX() - WIDTH; x <= location.getBlockX() + WIDTH; x++) {
            for (int y = location.getBlockY() - DEPTH; y <= location.getBlockY(); y++) {
                int currentDepth = location.getBlockY() - y;
                int currentLength = (int) (LENGTH * (1 - (double) currentDepth / DEPTH));
                for (int z = location.getBlockZ() - currentLength; z <= location.getBlockZ() + currentLength; z++) {
                    Location loc = new Location(location.getWorld(), x, y, z);
                    BlockChanger.setBlock(loc, Material.AIR);
                }
            }
        }

        // Jetzt erzeugen wir Spitzen auf dem Boden des Lochs
        for (int x = location.getBlockX() - WIDTH; x <= location.getBlockX() + WIDTH; x++) {
            for (int z = location.getBlockZ() - LENGTH; z <= location.getBlockZ() + LENGTH; z++) {
                Location loc = new Location(location.getWorld(), x, location.getBlockY(), z);
                if (event.getBlock().getType() == Material.AIR) {
                    if (loc.getBlock().getType() != Material.AIR) {
                        if (random.nextBoolean()) {
                            int height = random.nextInt(MAX_HEIGHT) + 1;
                            for (int i = 1; i <= height; i++) {
                                BlockChanger.setBlock(loc.clone().add(0, i - 1, 0), Material.STONE);
                            }
                        }
                    }
                }
            }
        }
    }
}
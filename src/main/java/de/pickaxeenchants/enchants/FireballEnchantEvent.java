package de.pickaxeenchants.enchants;

import de.backpack.apfloat.Apfloat;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static skyblock.main.Main.islandManager;

public class FireballEnchantEvent implements Listener {

    private final static String ENCHANT_NAME = "Fireball";
    private final String procMessage = "§e§l " + ENCHANT_NAME +  ": §fhas received %s Blocks!";
    private long tokens_per_block = 5000000000L;
    private int xp_per_block = 1;
    private int blocks_xp = 0;
    private int radius = 6;

    public static Plugin plugin;
    EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;
    private List<Enchant> enchants = enchantInitiazer.getEnchants();
    UserManager userManager = de.pickaxeenchants.main.Main.userManager;

    public static List<Entity> flying_blocks = new ArrayList<Entity>();


    public FireballEnchantEvent(Plugin plugin) {
        FireballEnchantEvent.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)

    public void onBreak(BlockBreakEvent event) {

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());

        if (event instanceof FireballEnchant) return;

        if (event.isCancelled())
            return;

        int level = 0;
        Enchant e = null;

        for(Enchant enchant : enchants) {
            if(enchant.getName().equalsIgnoreCase("Fireball")){
                level = playerEnchantments.getEnchantLevel(enchant);
                e = enchant;
            }
        }

        double buffMultiplier = playerEnchantments.calcBuffs(e);

        radius= (int) (radius*buffMultiplier);

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        if (Math.random() < playerEnchantments.calcActivateChance(e)) {
            Material material = event.getBlock().getType();
            activateFireballEnchant(event.getPlayer(), material, event.getBlock().getLocation(), radius, e);
        }
    }

    public void activateFireballEnchant(Player player, Material material, Location location, int radius, Enchant enchant){
        UserManager userManager = de.pickaxeenchants.main.Main.userManager;
        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();
        double multiply = 1 * userManager.calculateMasteryEnchantBuff(player);

        if(Math.random() < multiplyEnchant.proc_chance(player)){
            multiply +=multiplyEnchant.multi()-1;

            player.sendMessage("§e§lFireball Multiply: §f" + new DecimalFormat("#.###").format(multiply));
        }
        summonFireball(player, enchant);

    }

    public void sendProcMessage(Player player, Long amount){
        UnlimitedNumber unlimitedNumber = new UnlimitedNumber(String.valueOf(amount));
        player.sendMessage(String.format(procMessage, unlimitedNumber.format()));
    }

    public static List<Location> Fireball(Location centerBlock, int radius, boolean hollow, Player player) {
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

    private void summonFireball(Player player, Enchant enchant) {
        Location location = player.getLocation().add(0, 2, 0);
        new BukkitRunnable() {
            double particleDistance = 0.2;
            double waypoint = 0;
            double x, y, z;
            Vector v = islandManager.island.get(player.getUniqueId()).getIslandLocation().toVector().subtract(location.toVector()).normalize().multiply(2);
            double blocks_amount = 0;
            @Override
            public void run() {
                if (location.getBlock().getType() == Material.AIR) {
                    x = v.getX();
                    z = v.getZ();
                    y = -0.04 * Math.pow(waypoint, 2) + 1.5; // Quadratische Funktion für die y-Koordinate

                    location.getWorld().spawnParticle(Particle.REDSTONE, location.getX(), location.getY(), location.getZ(), 2, 0, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 1.75F), true);
                    location.getWorld().spawnParticle(Particle.LAVA, location, 1);
                    location.getWorld().spawnParticle(Particle.REDSTONE, location.getX(), location.getY(), location.getZ(), 2, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1.75F), true);
                    location.getWorld().spawnParticle(Particle.REDSTONE, location.getX(), location.getY(), location.getZ(), 2, 0, 0, 0, 0, new Particle.DustOptions(Color.GRAY, 1.75F), true);
                    location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.getX(), location.getY(), location.getZ(), 5, 0, 0, 0, 0);
                    location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location.getX(), location.getY(), location.getZ(), 5, 0, 0, 0, 0);

                    location.add(x/4, y/4, z/4); // Bewege entlang der Parabel
                    waypoint += particleDistance;
                } else {
                    for (Location loc : Fireball(location, radius, false, player)) {
                        blocks_amount += 1;
                        BlockChanger.setBlock(loc, Material.AIR);
                    }
                    userManager.addTokensFormEnchant(player, new Apfloat(blocks_amount*tokens_per_block), enchant);
                    enchant.procEnchantment();
                    sendProcMessage(player, (long) blocks_amount);
                    blocks_amount=0;
                    blocks_xp=0;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}

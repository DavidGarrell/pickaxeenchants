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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExplosiveEnchantEvent implements Listener {
    private double blocks_amount = 0;

    private final String procMessage = "§e§lExplosive: §fhas received %s Blocks!";
    private int blocks_per_proc = 1;
    private int xp_per_block = 1;
    private int blocks_xp = 0;

    public static Plugin plugin;
    EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;
    private List<Enchant> enchants = enchantInitiazer.getEnchants();
    UserManager userManager = de.pickaxeenchants.main.Main.userManager;

    public static List<Entity> flying_blocks = new ArrayList<Entity>();


    public ExplosiveEnchantEvent(Plugin plugin) {
        ExplosiveEnchantEvent.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)

    public void onBreak(BlockBreakEvent event) {

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());

        if (event instanceof ExplosiveEnchant) return;

        if (event.isCancelled())
            return;

        int level = 0;
        Enchant e = null;

        for(Enchant enchant : enchants) {
            if(enchant.getName().equalsIgnoreCase("Explosive")){
                level = playerEnchantments.getEnchantLevel(enchant);
                e = enchant;
            }
        }

        double buffMultiplier = playerEnchantments.calcBuffs(e);

        int radius_1 = 3;
        int radius_2 = level / 2000;
        int radius = radius_1 + radius_2;
        radius= (int) (radius*buffMultiplier);

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        if (Math.random() < playerEnchantments.calcActivateChance(e)) {
            Material material = event.getBlock().getType();
            activateExplosiveEnchant(event.getPlayer(), material, event.getBlock().getLocation(), radius, e);
        }
    }

    public void activateExplosiveEnchant(Player player, Material material, Location location, int radius, Enchant enchant){
        UserManager userManager = de.pickaxeenchants.main.Main.userManager;
        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();
        EconomyAPI economyAPI = Main.economyAPI;
        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());



        double multiply = 1 * userManager.calculateMasteryEnchantBuff(player);

        if(Math.random() < multiplyEnchant.proc_chance(player)){
            multiply +=multiplyEnchant.multi()-1;

            player.sendMessage("§e§lExplosive Multiply: §f" + new DecimalFormat("#.###").format(multiply));
        }

        for (Location loc : Explosive(location, (int) (radius*multiply), false, player)) {
            if(Math.random()< 0.2) {
                spawnFlyingBlock(loc.getBlock());
            }
            BlockChanger.setBlock(loc, Material.AIR);
            blocks_amount+=blocks_per_proc;
            blocks_xp+=xp_per_block;
            if (Math.random() < 0.04) {
                loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
            }
        }
        economyAPI.addXPBalance(player, blocks_xp);
        economyAPI.addBlocks(player, blocks_xp);
        userManager.addMoneyFormEnchant(player, new Apfloat(blocks_amount), enchant);

        for (Location loc : Explosive(location, (int) (radius*multiply)+1, false, player))
            if (Math.random() < 0.4) BlockChanger.setBlock(loc, Material.AIR);
        for (Location loc : Explosive(location, (int) (radius*multiply)+2, false, player))
            if (Math.random() < 0.25) BlockChanger.setBlock(loc, Material.AIR);

        EmeraldRushEnchant emeraldRushEnchant = new EmeraldRushEnchant();
        if(material == Material.EMERALD_BLOCK) {
            blocks_amount *= emeraldRushEnchant.getMultiplier();

        }
        sendProcMessage(player, (long) blocks_amount);
        playerEnchantments.addBlocksToBackPack(player, material, (long) blocks_amount);


        blocks_amount=0;
        blocks_xp=0;

    }

    public void sendProcMessage(Player player, Long amount){
        UnlimitedNumber unlimitedNumber = new UnlimitedNumber(String.valueOf(amount));
        player.sendMessage(String.format(procMessage, unlimitedNumber.format()));
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
    private void spawnFlyingBlock(Block b) {
        float x = -0.5F + (float)(Math.random() * 1.0D); // Geschwindigkeit in x-Richtung
        float y = -0.5F + (float)(Math.random() * 1.0D + 0.5D); // Geschwindigkeit in y-Richtung
        float z = -0.5F + (float)(Math.random() * 1.0D); // Geschwindigkeit in z-Richtung

        Location spawnLocation = b.getLocation();
        org.bukkit.entity.FallingBlock fb = b.getWorld().spawnFallingBlock(spawnLocation, b.getType(), b.getData());
        fb.setDropItem(false);
        ((Entity) fb).setVelocity(new Vector(x, y, z));
        flying_blocks.add(fb);
    }

    @EventHandler
    public void killFallingBlocks(EntityChangeBlockEvent e) {

        if (e.getEntityType() == EntityType.FALLING_BLOCK) {

            if (flying_blocks.contains(e.getEntity())) {

                final FallingBlock fl = (FallingBlock) e.getEntity();

                if (fl.isOnGround()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {
                            fl.getWorld().getBlockAt(fl.getLocation()).setType(Material.AIR);

                        }
                    }, 1l);

                }
            }
        }
    }
}



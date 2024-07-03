package de.pickaxeenchants.enchants;

import de.backpack.apfloat.Apfloat;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.listener.UnlimitedNumber;
import de.backpack.main.Main;
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import skyblock.api.IslandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static skyblock.main.Main.islandManager;


public class DeepEnchantEvent implements Listener {

    private double blocks_amount = 0;

    public static Plugin plugin;
    private List<BackPackAPI> backpacks;
    private List<Enchant> enchants;
    private int tokens_per_block = 100000000;

    boolean active = false;

    public DeepEnchantEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)

    public void onBreak(BlockBreakEvent event) {

        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());

        EconomyAPI economyAPI = Main.economyAPI;

        if (event instanceof DeepEnchant) return;
        //Make sure we didn't call it ourselves.
        if (event.isCancelled())
            return;

        Location l1 = event.getBlock().getLocation();

        double chance = 0;
        Enchant e = null;
        for (Enchant enchant : enchants) {

            if (enchant.getName().equalsIgnoreCase("Deep")) {
               e = enchant;
            }
        }
        double buffMultiplier = playerEnchantments.calcBuffs(e);

        Material material = event.getBlock().getType();

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        if (Math.random() < playerEnchantments.calcActivateChance(e)) {
            if (!active) {
                active = true;
                Wither wither = l1.getWorld().spawn(l1, Wither.class);
                wither.setInvulnerable(true);
                wither.setAI(false);
                wither.setTarget(null);
                wither.setCustomName("§6§lDeep Enchant");

                AtomicInteger rotationCount = new AtomicInteger(0);
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                Enchant finalE = e;
                scheduler.scheduleSyncDelayedTask(plugin, () -> {
                    Vector dir = new Vector(0, -1, 0);
                    int taskId = scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                        int x = 1;
                        WitherSkull skull = wither.launchProjectile(WitherSkull.class, dir);
                        skull.setMetadata("witherSkull", new FixedMetadataValue(plugin, event.getPlayer().getName()));
                        int currentRotation = rotationCount.getAndIncrement(); // Erhöhe die Anzahl der Rotationen
                        int newYaw = currentRotation * 45; // Berechne die neue Yaw für die Rotation
                        wither.setRotation(newYaw, 1); // Setze die neue Yaw für die Rotation
                    }, 0L, 10L); // Fire every second (20 ticks)
                    scheduler.scheduleSyncDelayedTask(plugin, () -> {
                        wither.remove();
                        scheduler.cancelTask(taskId);
                    }, 200L); // Remove wither after 10 seconds (200 ticks)
                    scheduler.scheduleSyncDelayedTask(plugin, () -> {

                        EmeraldRushEnchant emeraldRushEnchant = new EmeraldRushEnchant();
                        if(material == Material.EMERALD_BLOCK) {
                            blocks_amount *= emeraldRushEnchant.getMultiplier();

                        }
                        UnlimitedNumber unlimitedNumber = new UnlimitedNumber(String.valueOf(blocks_amount));
                        event.getPlayer().sendMessage("§e§lDeep: §fhas received " + unlimitedNumber.format() + " §fTokens!");

                        userManager.addTokensFormEnchant(event.getPlayer(), new Apfloat(blocks_amount), finalE);
                        userManager.addMoneyFormEnchant(event.getPlayer(), new Apfloat(blocks_amount/tokens_per_block), finalE);
                        blocks_amount = 0;
                        active=false;
                    }, 280L);
                }, 40L); // Delay before making the wither shoot (40 ticks)
            }
        }
    }

    @EventHandler
    public void onBlockHit(ProjectileHitEvent event) {

        if (event.getEntity() instanceof WitherSkull) {
            event.setCancelled(true);
            WitherSkull projectile = (WitherSkull) event.getEntity();
            if (projectile.hasMetadata("witherSkull")) {
                Location loc = projectile.getLocation();
                projectile.setVisualFire(false);
                String playerName = projectile.getMetadata("witherSkull").get(0).asString();
                Player player = Bukkit.getPlayer(playerName);

                EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

                UserManager userManager = de.pickaxeenchants.main.Main.userManager;

                this.enchants = enchantInitiazer.getEnchants();

                PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(player.getUniqueId());
                int level = 0;

                int level_multiply = 0;

                double chance = 0;

                Enchant e = null;
                for(Enchant enchant : enchants) {

                    if(enchant.getName().equalsIgnoreCase("Deep")) {
                        e = enchant;
                        level = playerEnchantments.getEnchantLevel(enchant);
                    }
                }
                double buffMultiplier = playerEnchantments.calcBuffs(e);
                int radius_1 = 4;
                int radius_2 = level / 10;
                int radius = radius_1 + radius_2;
                radius= (int) (radius*buffMultiplier);

                player.sendMessage("Test");

                if (player != null) {
                    for (Location locExp : Explosive(loc, radius, false, player)) {
                        BlockChanger.setBlock(locExp, Material.AIR);
                        blocks_amount += tokens_per_block;
                        if (Math.random() < 0.0004) {
                            locExp.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, locExp, 1);
                        }
                    }
                    for (Location locExp : Explosive(loc, radius + 1, false, player))
                        if (Math.random() < 0.4) BlockChanger.setBlock(locExp, Material.AIR);
                    for (Location locExp : Explosive(loc, radius + 2, false, player))
                        if (Math.random() < 0.25) BlockChanger.setBlock(locExp, Material.AIR);
                    event.getEntity().remove(); // Projectile entfernen
                    event.getEntity().getWorld().createExplosion(loc, 4, true, false); // Explosion auslösen
                }
            }
        }
    }

    public static List<Location> Explosive(Location centerBlock, int radius, boolean hollow, Player player) {
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

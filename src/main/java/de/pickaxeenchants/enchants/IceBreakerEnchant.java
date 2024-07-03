package de.pickaxeenchants.enchants;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.platform.BlockInteractEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.registry.LegacyMapper;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IceBreakerEnchant implements Listener {
    private List<Enchant> enchants;
    private double blocks_amount = 0;

    private int tokens_per_block = 350000000;
    private int xp_per_block = 1;

    private int blocks_xp = 0;
    private List<BackPackAPI> backpacks;

    private Island island;
    private boolean active = false;


    public static Plugin plugin;

    List<Block> packedIceBlocks = new ArrayList<>();

    List<Location> blockLocations;

    public IceBreakerEnchant(Plugin plugin) {
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
        //Make sure we didn't call it ourselves.
        if (event.isCancelled())
            return;

        int level = 0;

        Enchant e = null;

        for(Enchant enchant : enchants) {

            if(enchant.getName().equalsIgnoreCase("Ice_Breaker")){
                level = playerEnchantments.getEnchantLevel(enchant);
                e = enchant;
            }
        }

        double buffMultiplier = playerEnchantments.calcBuffs(e);

        int radius = 4;

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        if (Math.random() < playerEnchantments.calcActivateChance(e)) {

            Material material = event.getBlock().getType();

            double multiply = 1 * userManager.calculateMasteryEnchantBuff(event.getPlayer());

            if(Math.random() < multiplyEnchant.proc_chance(event.getPlayer())){
                multiply +=multiplyEnchant.multi()-1;

                event.getPlayer().sendMessage("§e§lIce Breaker Multiply: §f" + new DecimalFormat("#.###").format(multiply));
            }
            Location l1 = event.getBlock().getLocation();
            activateIceBreaker(l1, event.getPlayer(), radius);
        }


    }




    public void activateIceBreaker(Location location, Player player, int radius){

        if (!active) {
            active = true;
            List<Block> packedIceBlocks = new ArrayList<>();
            blockLocations = new ArrayList<>(Disc(location, radius, player));
            // Blöcke nacheinander mit Verzögerung setzen
            int ticksDelay = 1; // Verzögerung zwischen den Blöcken (1 Tick)
            final int[] index = {0};

            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            BukkitRunnable blockPlacer = new BukkitRunnable() {
                @Override
                public void run() {
                    if (index[0] >= blockLocations.size()) {
                        this.cancel();
                        scheduler.runTaskLater(plugin, () -> {
                            for (Block block : packedIceBlocks) {
                                block.setType(Material.AIR); // Setze den Block auf Luft
                            }
                            active = false; // Setze den Eisbrecher wieder auf inaktiv, wenn alle Blöcke geändert wurden
                        }, 100L); // 100 ticks sind ungefähr 5 Sekunden (20 Ticks pro Sekunde)
                        return;
                    }

                    Location loc = blockLocations.get(index[0]);
                    Block block = loc.getBlock();
                    block.setType(Material.BLUE_ICE);
                    packedIceBlocks.add(block);
                    index[0]++;
                }
            };

            blockPlacer.runTaskTimer(plugin, ticksDelay, ticksDelay);
        }
    }

    public static List<Location> Disc(Location centerBlock, int radius, Player player) {
        List<Location> circleBlocks = new ArrayList<Location>();
        int bX = centerBlock.getBlockX();
        int bY = centerBlock.getBlockY();
        int bZ = centerBlock.getBlockZ();
        IslandManager islandManager = skyblock.main.Main.islandManager;

        for (double x = bX - radius; x <= bX + radius; x++) {
            for (double y = bY; y <= bY; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {

                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * 0.9 * (bY - y) * 0.9));

                    if (distance < radius * radius) {
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

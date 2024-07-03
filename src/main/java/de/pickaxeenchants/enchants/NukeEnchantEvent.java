package de.pickaxeenchants.enchants;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.*;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.List;

public class NukeEnchantEvent implements Listener {

    private double blocks_amount = 0;

    private int blocks_xp = 0;

    private static int start_island_level;

    private List<BeaconEnchant> enchants;
    private List<BackPackAPI> backpacks;

    public static Plugin plugin;

    public NukeEnchantEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)

    public void onBreak(BlockBreakEvent event) {

        if (event instanceof LayerEnchant) return;
        //Make sure we didn't call it ourselves.
        if (event.isCancelled())
            return;


        EnchantInitiazer enchantInitiazer = de.pickaxeenchants.main.Main.enchantInitiazer;

        UserManager userManager = de.pickaxeenchants.main.Main.userManager;

        this.enchants = enchantInitiazer.getBeaconEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());

        EconomyAPI economyAPI = Main.economyAPI;

        World world = event.getBlock().getWorld();

        IslandManager islandManager = skyblock.main.Main.islandManager;

        Fortune fortune = new Fortune();

        double multi = fortune.blocksMulti(event.getPlayer());

        int level = 0;

        double chance = 0;

        start_island_level = islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_LEVEL();

        for(BeaconEnchant enchant : enchants) {

            if(enchant.getName().equalsIgnoreCase("Nuke")){
                level = playerEnchantments.getBeaconEnchantLevel(enchant);
                chance = (enchant.getBaseChance() + (enchant.getChanceIncrease() * playerEnchantments.getBeaconEnchantLevel(enchant)));

                if(playerEnchantments.getBeaconEnchantLevel(enchant)==0){
                    chance=0;
                }
            }
        }


        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        if (Math.random() < chance) {

            Island island = islandManager.island.get(event.getPlayer().getUniqueId());



            if(Math.random() < multiplyEnchant.proc_chance(event.getPlayer())){
                double multiply =multiplyEnchant.multi();
                multi*=multiply;
                event.getPlayer().sendMessage("§e§lNuke Multiply: §f" + new DecimalFormat("#.###").format(multiply));
            }
            Material material = event.getBlock().getType();

            Location location = islandManager.island.get(event.getPlayer()).getIslandLocation();

            int startX = location.getBlockX() - island.getISLAND_SIZE() / 2;
            int startY = island.getIslandLocation().getBlockY() - island.getISLAND_HIGH() -1; // Verschiebung um die Höhe des Cubes nach unten
            int startZ = location.getBlockZ() - island.getISLAND_SIZE() / 2;



            int airBlocks = 0;
            for (int x = startX; x < startX + island.getISLAND_SIZE(); x++) {
                for (int z = startZ; z < startZ + island.getISLAND_SIZE(); z++) {
                    for(int y = startY; y < startY + island.getISLAND_HIGH() - 1; y++)
                        if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
                            blocks_amount++;

                            if (Math.random() < 0.05) {
                                Location location1 = new Location(world, x, y+1, z);
                                location1.getWorld().spawnParticle(Particle.FLAME, location1, 10, 0.5, 0.5, 0.5, 0.1);

                            }
                        }
                }
            }

            blocks_amount=blocks_amount*multi*0.1;
            blocks_xp = (int) blocks_amount;

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 1.0f, 1.0f);

            event.getPlayer().sendMessage("§b§lNuke: §fhas received " + (int) blocks_amount + " §fblocks");


            if(island.getISLAND_LEVEL()==start_island_level) {
                island.resetIsland(event.getPlayer());
            }


            blocks_amount = 0;
            blocks_xp = 0;

        }
    }
}

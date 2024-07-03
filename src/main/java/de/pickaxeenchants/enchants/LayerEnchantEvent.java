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
import de.backpack.apfloat.Apfloat;
import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.*;
import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class LayerEnchantEvent implements Listener {

    private double blocks_amount = 0;

    private int blocks_xp = 0;

    private List<Enchant> enchants;
    private List<BackPackAPI> backpacks;

    private boolean message_allow = true;
    private String message_proc = "§cEnchant §7| §aLayer activates!";

    public static Plugin plugin;

    private boolean active = false;

    public LayerEnchantEvent(Plugin plugin) {
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

        this.enchants = enchantInitiazer.getEnchants();

        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(event.getPlayer().getUniqueId());

        EconomyAPI economyAPI = Main.economyAPI;

        World world = event.getBlock().getWorld();

        IslandManager islandManager = skyblock.main.Main.islandManager;

        Fortune fortune = new Fortune();

        double multi = fortune.blocksMulti(event.getPlayer());

        int level = 0;
        Enchant e = null;
        for(Enchant enchant : enchants) {
            if(enchant.getName().equalsIgnoreCase("Layer")) {
                level = playerEnchantments.getEnchantLevel(enchant);
                e = enchant;
            }

        }

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();
        double buffMultiplier = playerEnchantments.calcBuffs(e);

        if (Math.random() < playerEnchantments.calcActivateChance(e)) {
            if(Math.random() < multiplyEnchant.proc_chance(event.getPlayer())){
                double multiply =multiplyEnchant.multi();
                multi*=multiply;
                event.getPlayer().sendMessage("§e§lMultiply: §f" + new DecimalFormat("#.###").format(multiply));
            }

            System.out.println(playerEnchantments.calcActivateChance(e));

            e.procEnchantment();
            /*
            Location block_loc = event.getBlock().getLocation();
            int block_x = block_loc.getBlockX();
            int block_y = block_loc.getBlockY();
            int block_z = block_loc.getBlockZ();

            for (int x = block_x; x < block_x + 3; x++) {
                for (int z = block_z; z < block_z +3; z++) {
                    Location loc = new Location(world, x, block_y, z);
                    loc.getBlock().setType(Material.BARRIER);
                }
            }

             */

            Location location = islandManager.island.get(event.getPlayer().getUniqueId()).getIslandLocation();

            int startX = location.getBlockX() - islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE() / 2;
            int startY = event.getBlock().getLocation().getBlockY(); // Verschiebung um die Höhe des Cubes nach unten
            int startZ = location.getBlockZ() - islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE() / 2;

            int airBlocks = 0;
            for (int x = startX; x < startX + islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE(); x++) {
                    for (int z = startZ; z < startZ + islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE(); z++) {
                        if (world.getBlockAt(x, startY, z).getType() == Material.AIR) {
                            airBlocks++;
                        }
                    }

            }

            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(event.getPlayer().getWorld()), -1);
            BlockVector3 vector1 = BlockVector3.at(startX, startY, startZ);
            BlockVector3 vector2 = BlockVector3.at(startX + islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE() - 1, startY, startZ + islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE() - 1);
            CuboidRegion region = new CuboidRegion(new BukkitWorld(event.getPlayer().getWorld()), vector1, vector2);
            session.setFastMode(true);
            session.disableBuffering();
            BlockType air = BlockTypes.AIR;
            session.setBlocks((Region) region, air.getDefaultState());
            blocks_amount = (session.getBlockChangeCount()-airBlocks)*(multi+(buffMultiplier/100));
            blocks_xp = session.getBlockChangeCount()-airBlocks;
            session.flushSession();



            economyAPI.addXPBalance(event.getPlayer(), (long) blocks_xp);
            economyAPI.addBlocks(event.getPlayer(), (int) blocks_xp);
            userManager.addMoneyFormEnchant(event.getPlayer(), new Apfloat(blocks_amount), e);


            /*
            for (int x = startX; x < startX + islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE(); x++) {
                for (int z = startZ; z < startZ + islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE(); z++) {
                    Location location2 = new Location(world, x, startY, z);
                    if (Math.random() < 0.025) {
                        Location location1 = new Location(world, x, startY, z);
                        location1.getWorld().spawnParticle(Particle.CLOUD, location1, 5, 0.5, 0.1, 0.5, 0.1, null, true);

                    }
                }
            }

             */

            updatePlayerEnvironment(event.getPlayer());

            if(message_allow){
                event.getPlayer().sendMessage(message_proc);
            }

            blocks_amount = 0;
            blocks_xp = 0;

        }
    }
    public void updatePlayerEnvironment(Player player) {
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        int radius = 10; // Radius um den Spieler herum, den du aktualisieren möchtest

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLocation = playerLocation.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();
                    // Aktualisiere den Block, um ihn erneut zu rendern
                    block.getState().update(true, false);
                }
            }
        }
    }
}


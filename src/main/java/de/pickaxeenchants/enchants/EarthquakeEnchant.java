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
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import skyblock.api.Island;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class EarthquakeEnchant implements Listener {
    public static Plugin plugin;

    public EarthquakeEnchant(Plugin plugin) {
        this.plugin = plugin;
    }

    private double blocks_amount = 0;

    private int blocks_xp = 0;

    private List<Enchant> enchants;
    private List<BackPackAPI> backpacks;

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

        for (Enchant enchant : enchants) {

            if (enchant.getName().equalsIgnoreCase("Earthquake")) {
                level = playerEnchantments.getEnchantLevel(enchant);
                e=enchant;
            }
        }

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        double buffMultiplier = playerEnchantments.calcBuffs(e);


        if (Math.random() < playerEnchantments.calcActivateChance(e)) {

            Island island = islandManager.island.get(event.getPlayer().getUniqueId());


            if (Math.random() < multiplyEnchant.proc_chance(event.getPlayer())) {
                double multiply = multiplyEnchant.multi();
                multi *= multiply;
                event.getPlayer().sendMessage("§e§lMultiply: §f" + new DecimalFormat("#.###").format(multiply));
            }

            Location location = island.getIslandLocation();

            int startX = location.getBlockX() - island.getISLAND_SIZE() / 2;
            int startZ = event.getBlock().getLocation().getBlockZ();

            int airBlocks = 0;

            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(event.getPlayer().getWorld()), -1);
            BlockVector3 vector1 = BlockVector3.at(location.getBlockX() + islandManager.island.get(event.getPlayer().getUniqueId()).getISLAND_SIZE() / 2, island.getIslandLocation().getBlockY(), startZ - 1);
            BlockVector3 vector2 = BlockVector3.at(startX, island.getIslandLocation().getBlockY() - island.getISLAND_HIGH(), startZ + 1);
            CuboidRegion region = new CuboidRegion(new BukkitWorld(event.getPlayer().getWorld()), vector1, vector2);
            BlockType cobble = BlockTypes.AIR;

            for (int x = vector1.getBlockX(); x >= vector2.getBlockX(); x--) {
                for (int z = vector1.getBlockZ(); z <= vector2.getBlockZ(); z++) {
                    for (int y = vector1.getBlockY(); y >= vector2.getBlockY(); y--) {
                        if (world.getBlockAt(x, y, z).getType() == Material.AIR) {
                            airBlocks++;
                        }
                    }
                }
            }
                session.setFastMode(true);
                BlockState blockState = cobble.getDefaultState();
                BaseBlock block = blockState.toBaseBlock();
                Pattern p = block;
                session.disableBuffering();
                session.setBlocks((Region) region, p);
                blocks_amount = (session.getBlockChangeCount() - airBlocks) * multi;
                blocks_xp = session.getBlockChangeCount() - airBlocks;
                session.flushSession();

                economyAPI.addXPBalance(event.getPlayer(), (long) blocks_xp);
                economyAPI.addBlocks(event.getPlayer(), (int) blocks_xp);

                userManager.addMoneyFormEnchant(event.getPlayer(), new Apfloat(blocks_amount), e);

                blocks_amount = 0;
                blocks_xp = 0;


        }
    }
}

package de.pickaxeenchants.enchants;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
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
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import skyblock.api.Island;
import skyblock.api.IslandGenerator;
import skyblock.api.IslandManager;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class EmeraldRushEnchantEvent implements Listener {

    private double blocks_amount = 0;

    private int blocks_xp = 0;

    private List<Enchant> enchants;
    private List<BackPackAPI> backpacks;

    public static Plugin plugin;

    public EmeraldRushEnchantEvent(Plugin plugin) {
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
        double chance = 0;

        for(Enchant enchant : enchants) {

            if(enchant.getName().equalsIgnoreCase("Emerald_Rush")){
                level = playerEnchantments.getEnchantLevel(enchant);
                chance = (enchant.getBaseChance() + (enchant.getChanceIncrease() * playerEnchantments.getEnchantLevel(enchant)));
            }
            if(level==0){
                chance =0;
            }
        }

        MultiplyEnchant multiplyEnchant = new MultiplyEnchant();

        double buffMultiplier = 0;
        Map<Enchant, Integer> enchantBuffs = playerEnchantments.getEnchantBuffs();
        for (Enchant enchant : enchantBuffs.keySet()) {
            if (enchant.getName().equalsIgnoreCase("Emerald_Rush")) {
                buffMultiplier += enchantBuffs.get(enchant);
            }
        }

        chance=chance*(1 + buffMultiplier / 100);

        if (Math.random() < chance) {

            Island island = islandManager.island.get(event.getPlayer().getUniqueId());
            island.emeraldRush(event.getPlayer());
            event.getPlayer().sendMessage("Emerald Rush");

        }
    }
}

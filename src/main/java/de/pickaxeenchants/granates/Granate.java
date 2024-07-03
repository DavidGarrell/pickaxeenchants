package de.pickaxeenchants.granates;

import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.listener.EconomyAPI;
import de.backpack.main.Main;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.block.BlockChanger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import skyblock.api.IslandManager;
import skyblock.api.IslandMaterials;

import java.util.ArrayList;
import java.util.List;

public class Granate implements Listener {

    private Player throwingPlayer;
    private double blocks_amount = 0;
    private int blocks_xp = 0;
    private List<BackPackAPI> backpacks;
    public void throwGrenade(Player p, String rarity) {


        this.throwingPlayer = p;
        TNTPrimed tnt = p.getWorld().spawn(p.getEyeLocation(), TNTPrimed.class);
        tnt.setFuseTicks(40);
        tnt.setVelocity(p.getEyeLocation().getDirection().multiply(1.5));

        if(rarity.equalsIgnoreCase("tnt-1")){
            tnt.setCustomName("tnt-1");
        }
        if(rarity.equalsIgnoreCase("tnt-2")){
            tnt.setCustomName("tnt-2");
        }
        if(rarity.equalsIgnoreCase("tnt-3")){
            tnt.setCustomName("tnt-3");
        }
        if(rarity.equalsIgnoreCase("tnt-4")){
            tnt.setCustomName("tnt-4");
        }
        if(rarity.equalsIgnoreCase("tnt-5")){
            tnt.setCustomName("tnt-5");
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        event.setCancelled(true);
        Entity entity = event.getEntity();
        UserManager userManager = de.pickaxeenchants.main.Main.userManager;
        IslandManager islandManager = skyblock.main.Main.islandManager;

        int size = 10;

        EconomyAPI economyAPI = Main.economyAPI;
        if (entity instanceof TNTPrimed) {
            if (entity.getCustomName().equalsIgnoreCase("tnt-1")) {
                size = 5;
            }
            if (entity.getCustomName().equalsIgnoreCase("tnt-2")) {
                size = 10;
            }
            if (entity.getCustomName().equalsIgnoreCase("tnt-3")) {
                size = 15;
            }
            if (entity.getCustomName().equalsIgnoreCase("tnt-4")) {
                size = 20;
            }
            if (entity.getCustomName().equalsIgnoreCase("tnt-5")) {
                size = 30;
            }
                TNTPrimed tntPrimed = (TNTPrimed) entity;
                Player player = throwingPlayer;

                Location explosionCenter = event.getLocation();
                for (Location loc : Explosive(explosionCenter, size, false, player)) {
                    BlockChanger.setBlock(loc, Material.AIR);
                    blocks_amount += 1;
                    blocks_xp += 1;
                }
                economyAPI.addXPBalance(player, blocks_xp);
                economyAPI.addBlocks(player, blocks_xp);


                blocks_amount = 0;
                blocks_xp = 0;


        }
    }

    @EventHandler
    public void throwGranateListener(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_APPLE)) {
            throwGrenade(player, "tnt-1");
        }
        if (player.getInventory().getItemInMainHand().getType().equals(Material.FIREWORK_STAR)) {
            throwGrenade(player, "tnt-2");
        }
        if (player.getInventory().getItemInMainHand().getType().equals(Material.FIRE_CHARGE)) {
            throwGrenade(player, "tnt-3");
        }
        if (player.getInventory().getItemInMainHand().getType().equals(Material.POPPED_CHORUS_FRUIT)) {
            throwGrenade(player, "tnt-4");
        }
        if (player.getInventory().getItemInMainHand().getType().equals(Material.TNT)) {
            throwGrenade(player, "tnt-5");
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

                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y)  * (bY - y)));

                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location loc = new Location(centerBlock.getWorld(), x, y, z);
                        if (loc.getBlock().getType() != Material.BEDROCK && loc.getBlock().getType() != Material.AIR && islandManager.island.get(player).checkIfBlockIsInMine(loc)) {
                            circleBlocks.add(loc);
                        }
                    }
                }
            }
        }
        return circleBlocks;
    }

}

package de.pickaxeenchants.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.backpack.apfloat.Apfloat;
import de.backpack.api.BackPack;
import de.backpack.api.BackPackAPI;
import de.backpack.api.BlockValues;
import de.backpack.listener.EconomyAPI;
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.enchants.CalculateDestoyedBlocksByEnchants;
import de.pickaxeenchants.enchants.Fortune;
import de.pickaxeenchants.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import skyblock.api.Island;
import skyblock.api.IslandManager;
import skyblock.api.IslandMaterials;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Pouch implements Listener {

    public static Plugin plugin;

    public Pouch(Plugin plugin) {
        this.plugin = plugin;
    }

    UserManager userManager = Main.userManager;

    private List<Enchant> enchants;
    private List<BackPackAPI> backpacks;

    @EventHandler

    public void onClickPouch(PlayerInteractEvent e){

        if(e.getItem() != null && e.getItem().getItemMeta() != null) {
            if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6Token Pouch Tier I")) {

                EconomyAPI economyAPI = de.backpack.main.Main.economyAPI;
                Apfloat tokens = new Apfloat("1000000000000");

                economyAPI.addTokens(e.getPlayer(), tokens);

                displayTitleAnimation(e.getPlayer(), Long.parseLong(tokens.toString(true)));
                e.setCancelled(true);
                e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
            }
        }
    }

    @EventHandler

    public void onJoin(PlayerJoinEvent e){

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        // Set the custom texture for the player head
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ0NDk4YTBmZTI3ODk1NmUzZDA0MTM1ZWY0YjEzNDNkMDU0OGE3ZTIwOGM2MWIxZmI2ZjNiNGRiYzI0MGRhOCJ9fX0="));
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        skullMeta.setDisplayName("§6Token Pouch Tier I");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Right-Click to open!");
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);

        e.getPlayer().getInventory().addItem(skull);



    }
    private void displayTitleAnimation(Player player, long possibleBlocks) {
        int steps = 20; // Number of animation steps
        int stepDuration = 2; // Number of ticks for each step (1 second)

        long stepValue = possibleBlocks / steps;

        new BukkitRunnable() {
            int remainingSteps = steps;

            @Override
            public void run() {
                if (remainingSteps > 0) {
                    // Calculate the displayed number for the current step
                    long displayedBlocks = stepValue * (steps - remainingSteps + 1);

                    // Create the title text
                    String titleText = "" + ChatColor.YELLOW + displayedBlocks;

                    // Display the title
                    player.sendTitle("", titleText, 2, stepDuration, 2);

                    remainingSteps--;

                } else {
                    this.cancel(); // Stop the task when animation is complete
                }
            }
        }.runTaskTimer(plugin, 0, stepDuration); // Run the animation task
    }
}

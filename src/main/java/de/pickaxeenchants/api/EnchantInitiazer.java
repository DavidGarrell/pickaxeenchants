package de.pickaxeenchants.api;

import de.backpack.listener.UnlimitedNumber;
import de.pickaxeenchants.items.AccessoryBox;
import de.pickaxeenchants.main.Main;
import org.apfloat.Apfloat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class EnchantInitiazer implements Listener {


    private final ArrayList<Enchant> enchants = new ArrayList<>();
    private final ArrayList<BeaconEnchant> beaconEnchants = new ArrayList<>();
    PlayerEnchantments playerEnchantments;

    @EventHandler

    public void setPlayerEnchantments(PlayerJoinEvent e){


        Player player = (Player) e.getPlayer();
        UserManager userManager = Main.userManager;
        if(!userManager.getPlayerPlayerEnchantments().containsKey(player.getUniqueId())) {
            playerEnchantments = new PlayerEnchantments(player);
            userManager.setPlayerPlayerEnchantments(player, playerEnchantments);
            userManager.setPlayerMastery(player, 0);
            userManager.setPickaxeXP(player, 0);
            userManager.setPickaxeLevel(player, 1);
        } else {
            System.out.println("k");
        }
        startTokensGainPerMinute(player);

        AccessoryBox accessoryBox = new AccessoryBox();
        accessoryBox.giveAccessoryBox(player, 1);
        accessoryBox.giveAccessoryBox(player, 2);
        accessoryBox.giveAccessoryBox(player, 3);
        accessoryBox.giveAccessoryBox(player, 4);


    }

    public void startTokensGainPerMinute(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                UserManager userManager = Main.userManager;
                String tokensPerMinute = String.valueOf(userManager.getTokensGainPerMinute(player));
                String moneyPerMinute = String.valueOf(userManager.getMoneyGainPerMinute(player));
                UnlimitedNumber tokensPerMinuteUN = new UnlimitedNumber(tokensPerMinute);
                UnlimitedNumber moneyPerMinuteUN = new UnlimitedNumber(moneyPerMinute);
                player.sendMessage("§8§m-------------------------------");
                player.sendMessage("    §6§lMINE SUMMARY");
                player.sendMessage("    §fTokens: " + tokensPerMinuteUN.format());
                player.sendMessage("    §fMoney: " + moneyPerMinuteUN.format());
                player.sendMessage("§8§m-------------------------------");
                userManager.resetTokensGainPerMinute(player);
                userManager.resetMoneyGainPerMinute(player);
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1200, 1200); // 1200 ticks = 60 seconds (1 minute)
    }

    public PlayerEnchantments getPlayerEnchantments() {
        return playerEnchantments;
    }

    public void setPlayerEnchantments(PlayerEnchantments playerEnchantments) {
        this.playerEnchantments = playerEnchantments;
    }

    public void createEnchantments(){

        enchants.add(new Enchant("Fortune", Material.DIAMOND, 10000, "This enchantment gives a multiplier for blocks", 1, 0, 5,10, 0, new ArrayList<>(Arrays.asList(EnchantTypes.Blocks))));
        enchants.add(new Enchant("Token_Finder", Material.MAGMA_CREAM, 5000, "This enchantment gives you Tokens while mining.", 1, 0, 10, 1000000, 0, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens))));
        enchants.add(new Enchant("Layer", Material.HOPPER, 10000, "This enchantment destroys a layer of the mine.", 0.0025, 0.0000012, 5000000, 10000000, 0, new ArrayList<>(Arrays.asList(EnchantTypes.Mine_XP, EnchantTypes.Blocks))));
        enchants.add(new Enchant("XP_Finder", Material.EXPERIENCE_BOTTLE, 1000, "This enchantment gives you Pickaxe XP while mining.", 0.005, 0, 100000000, 100000000, 0, new ArrayList<>(Arrays.asList(EnchantTypes.XP))));
        enchants.add(new Enchant("Key_Finder", Material.TRIPWIRE_HOOK, 5000, "This enchantment grants you the chance to obtain additional key", 0.0005, 0.000004, 10000000000L, 5000000000L, 0, new ArrayList<>(Arrays.asList(EnchantTypes.Misc))));
        enchants.add(new Enchant("Beacon_Finder", Material.BEACON, 5000, "This enchantment grants you the chance to obtain beacons", 0.05, 0, 10000000000L, 5000000000L, 0, new ArrayList<>(Arrays.asList(EnchantTypes.Beacons))));
        enchants.add(new Enchant("Explosive", Material.TNT, 10000, "This enchantment triggers an explosion which blows a crater in the mine. The size is increased by leveling", 0.0005, 0.00000015, 4000000, 5000000, 0, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens, EnchantTypes.Blocks))));
        enchants.add(new Enchant("Token_Merchant", Material.RAW_GOLD, 5000, "This enchantment gives you a multiplier on TokenFinder", 0.005, 0.00000024, 4000000000L, 4000000000L, 30, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens))));
        enchants.add(new Enchant("Laser", Material.REDSTONE, 200, "This enchantment turns your pickaxe into a laser for a short time that destroys massive amounts of blocks", 0.00005, 0.000005, 10000000000L, 10000000000L, 55, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens, EnchantTypes.Blocks))));
        enchants.add(new Enchant("Strike", Material.QUARTZ, 10000, "This enchantment summons a lightning bolt that triggers a massive explosion", 0.00025, 0.00000010, 10000000, 15000000, 70, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens, EnchantTypes.Blocks))));
        enchants.add(new Enchant("Zeus_Wrath", Material.TRIDENT, 10000, "This enchantment summons a lightning bolts that triggers a explosion", 0.25, 0.00000010, 10000000, 15000000, 70, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens, EnchantTypes.Blocks))));
        enchants.add(new Enchant("Earthquake", Material.RAW_COPPER, 10000, "This enchantment splits your mine", 0.00025, 0.0000002, 500000000, 50000000, 90, new ArrayList<>(Arrays.asList(EnchantTypes.Beacons, EnchantTypes.Mine_XP))));
        enchants.add(new Enchant("Meteor", Material.FIRE_CHARGE, 150, "This enchantment summons a meteor shower which hits the mine.", 0.0005, 0.00000007, 30000000000L, 30000000000L, 120, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens, EnchantTypes.Blocks))));
        enchants.add(new Enchant("Boomerang", Material.BONE, 10000, "", 0.10005, 0.00004, 10, 1, 170, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens))));
        enchants.add(new Enchant("Deep", Material.WITHER_SKELETON_SKULL, 100, "", 0.10005, 0.0000002,1000000000000L,500000000000L, 190, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens, EnchantTypes.Blocks))));
        enchants.add(new Enchant("Multiply", Material.GOLD_INGOT, 5000, "This enchantment increases the power of the next enchantment by 1.0x to 1.5x", 0.0005, 0.0000005, 1000000000, 500000000, 245, new ArrayList<>(Arrays.asList(EnchantTypes.Misc))));
        enchants.add(new Enchant("Emerald_Rush", Material.EMERALD_BLOCK, 100, "", 0.00004, 0.000001, 5000000000000L, 5000000000000L, 300, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens))));
        enchants.add(new Enchant("Ice_Breaker", Material.PACKED_ICE, 100, "", 0.00004, 0.000001, 5000000000000L, 5000000000000L, 300, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens))));
        enchants.add(new Enchant("Fireball", Material.BLAZE_POWDER, 200, "", 0.0004, 0.00001, 5000000000000L, 5000000000000L, 300, new ArrayList<>(Arrays.asList(EnchantTypes.Tokens))));

        beaconEnchants.add(new BeaconEnchant("Nuke", Material.TNT_MINECART, 100, "This enchant explodes your whole mine and gives you 10% of the blocks", 0.000001, 0.000001, 1000, 1.05));
        beaconEnchants.add(new BeaconEnchant("Super Explosive", Material.TNT, 100, "This enchantment triggers an huge explosion which blows a crater in the mine.", 0.000001, 0.000001, 1000, 1.05));
        beaconEnchants.add(new BeaconEnchant("Zeus", Material.IRON_AXE, 100, "This enchant explodes your whole mine and gives you 20% of the blocks", 0.1000001, 0.000001, 1000, 1.05));
    }

    public ArrayList<Enchant> getEnchants() {
        return enchants;
    }

    public ArrayList<BeaconEnchant> getBeaconEnchants() {
        return beaconEnchants;
    }
}

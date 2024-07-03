package de.pickaxeenchants.main;

import com.google.common.reflect.ClassPath;
import de.pickaxeenchants.api.EnchantInitiazer;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.block.BlockChangeCommand;
import de.pickaxeenchants.commands.AutoMinerCommand;
import de.pickaxeenchants.commands.MenuCommand;
import de.pickaxeenchants.commands.PickaxeLevelCommand;
import de.pickaxeenchants.commands.SetEnchantLevelCommand;
import de.pickaxeenchants.enchants.*;
import de.pickaxeenchants.events.EnchantMenuEvents;
import de.pickaxeenchants.granates.Granate;
import de.pickaxeenchants.items.AccessoryBox;
import de.pickaxeenchants.items.EnchantPickaxe;
import de.pickaxeenchants.listeners.BeaconFinder;
import de.pickaxeenchants.listeners.InteractPickaxe;
import de.pickaxeenchants.listeners.Pouch;
import de.pickaxeenchants.listeners.UpdatePickaxeMeta;
import de.pickaxeenchants.menus.AccessoryMenu;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Main extends JavaPlugin {


    private de.backpack.main.Main backpackmain = (de.backpack.main.Main) Bukkit.getServer().getPluginManager().getPlugin("BackPack");

    public static EnchantInitiazer enchantInitiazer;

    public static String prefix = "§b§lEnchants §f| ";
    public static UserManager userManager;

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        userManager = new UserManager();
        enchantInitiazer = new EnchantInitiazer();
        enchantInitiazer.createEnchantments();

        Bukkit.getPluginManager().registerEvents(new EnchantInitiazer(), this);
        Objects.requireNonNull(getCommand("pickaxeenchants")).setExecutor(new MenuCommand());
        Objects.requireNonNull(getCommand("blocks")).setExecutor(new BlockChangeCommand());
        Objects.requireNonNull(getCommand("setenchantlevel")).setExecutor(new SetEnchantLevelCommand());
        Objects.requireNonNull(getCommand("pickaxelevel")).setExecutor(new PickaxeLevelCommand());
        Objects.requireNonNull(getCommand("autominer")).setExecutor(new AutoMinerCommand(this));

        Bukkit.getPluginManager().registerEvents(new JackHammer(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ExplosiveEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SuperExplosiveEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LayerEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ZeusEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EarthquakeEnchant(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Pouch(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DeepEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BeaconFinder(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LaserEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Earthquake(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new StrikeEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new NukeEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MeteorEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EmeraldRushEnchantEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EnchantMenuEvents(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Granate(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Fortune(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new TokenFinder(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new TokenMerchant(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new XPFinder(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ZeusWrathEnchant(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FireballEnchantEvent(this), this);

        Bukkit.getServer().getPluginManager().registerEvents(new EnchantPickaxe(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new UpdatePickaxeMeta(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InteractPickaxe(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Boomerang(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new IceBreakerEnchant(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Fortune(), this);

        Bukkit.getServer().getPluginManager().registerEvents(new AccessoryBox(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new AccessoryMenu(), this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

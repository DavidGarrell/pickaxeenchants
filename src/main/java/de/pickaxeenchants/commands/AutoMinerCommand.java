package de.pickaxeenchants.commands;

import de.pickaxeenchants.autominer.AutoMiner;
import de.pickaxeenchants.menus.EnchantMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class AutoMinerCommand implements CommandExecutor {

    public static Plugin plugin;

    public AutoMinerCommand(Plugin plugin){
        this.plugin=plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {

            Player player = (Player) sender;

            AutoMiner autoMiner = new AutoMiner(plugin);
            autoMiner.spawnAutoMiner(player);
            return true;
        }
        return false;
    }
}

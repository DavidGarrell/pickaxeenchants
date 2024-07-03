package de.pickaxeenchants.commands;

import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PickaxeLevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Parse arguments
        if (args.length < 2) {
            sender.sendMessage("Usage: /pickelevel <player> <level>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid level.");
            return true;
        }

        // Find the Enchant object with the given name


        // Set the enchantment level for the player
        UserManager userManager =  Main.userManager;
        userManager.setPickaxeLevel(targetPlayer, level);
        sender.sendMessage("Pickaxe level for " + targetPlayer.getName() + " set to " + level + ".");

        return true;
    }
}

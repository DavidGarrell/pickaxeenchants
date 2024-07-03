package de.pickaxeenchants.commands;

import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.api.PlayerEnchantments;
import de.pickaxeenchants.api.UserManager;
import de.pickaxeenchants.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetEnchantLevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Parse arguments
        if (args.length < 3) {
            sender.sendMessage("Usage: /setenchantlevel <player> <enchant> <level>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        String enchantName = args[1];
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid level.");
            return true;
        }

        // Find the Enchant object with the given name
        Enchant enchant = null;
        for (Enchant e : Main.enchantInitiazer.getEnchants()) {
            if (e.getName().equalsIgnoreCase(enchantName)) {
                enchant = e;
                break;
            }
        }
        if (enchant == null) {
            sender.sendMessage("Invalid enchantment name.");
            return true;
        }

        // Set the enchantment level for the player
        UserManager userManager =  Main.userManager;
        PlayerEnchantments playerEnchantments = userManager.getPlayerPlayerEnchantments().get(targetPlayer.getUniqueId());
        playerEnchantments.setEnchantLevel(enchant, level);
        sender.sendMessage("Enchantment level for " + targetPlayer.getName() + " set to " + level + ".");

        return true;
    }
}

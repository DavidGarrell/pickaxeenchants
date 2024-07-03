package de.pickaxeenchants.commands;

import de.pickaxeenchants.menus.EnchantMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {

            Player player = (Player) sender;

            EnchantMenu enchantMenu = new EnchantMenu();

            enchantMenu.enchantInventory(player);

            Inventory inventory = enchantMenu.getInventory();

            player.openInventory(inventory);
            return true;
        }
        return false;
    }
}

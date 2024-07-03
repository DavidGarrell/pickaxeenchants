package de.pickaxeenchants.items;

import de.pickaxeenchants.api.Accessory;
import de.pickaxeenchants.api.Enchant;
import de.pickaxeenchants.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccessoryBox implements Listener {

    private List<Enchant> enchants = Main.enchantInitiazer.getEnchants();

    public void giveAccessoryBox(Player player, int tier){
        player.getInventory().addItem(accessoryBoxItem(tier));
    }

    public ItemStack accessoryBoxItem(int tier){
        ItemStack itemStack = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§e§lAccessory Box Tier " + tier);
        List<String> lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @EventHandler
    public void onOpenAccessoryBox(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand != null && itemInHand.getType() != Material.AIR) {
            ItemMeta itemMeta = itemInHand.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                String displayName = itemMeta.getDisplayName();
                if (displayName != null && displayName.startsWith("§e§lAccessory Box Tier ")) {

                    int tier = Integer.parseInt(displayName.substring(23));
                    openAccessoryBox(player, tier);

                    e.setCancelled(true);
                }
            }
        }
    }
    private void openAccessoryBox(Player player, int tier) {

        Random random = new Random();
        Enchant randomEnchant = enchants.get(random.nextInt(enchants.size()));
        Accessory accessory = new Accessory(randomEnchant, tier);
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);
        player.getInventory().addItem(accessory.createCustomItem());

    }
}

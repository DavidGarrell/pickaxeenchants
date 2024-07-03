package de.pickaxeenchants.enchants;


import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class FireballEnchant extends BlockBreakEvent {
    public FireballEnchant(Block theBlock, Player player) {
        super(theBlock, player);

    }
}

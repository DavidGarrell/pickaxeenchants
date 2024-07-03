package de.pickaxeenchants.enchants;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ExplosiveEnchant extends BlockBreakEvent {

    public ExplosiveEnchant(Block theBlock, Player player) {
        super(theBlock, player);

    }
}

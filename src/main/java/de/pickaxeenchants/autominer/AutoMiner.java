package de.pickaxeenchants.autominer;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class AutoMiner {


    public static Plugin plugin;
    AutoMinerState state;
    NPC npc;

    String npc_id;

    Player player;

    public AutoMiner(Plugin plugin) {
        this.plugin = plugin;
    }
    public void spawnAutoMiner(Player player){
        state = AutoMinerState.MINING;
        this.player = player;
        this.npc_id = UUID.randomUUID().toString();
        summonNPC((Location) player.getLocation());

    }

    public void summonNPC(Location location){
        Player player = getPlayer();
        NPCRegistry registery = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
        npc = registery.createNPC(EntityType.PLAYER, "1");
        npc.spawn(location);

    }

    public void despawnNPC(){
        npc = null;
        state = AutoMinerState.DESPAWN;
    }

    public Location getPossibleAutoMinerLocation(){
        return null;
    }

    public AutoMinerState getState() {
        return state;
    }

    public NPC getNpc() {
        return npc;
    }

    public Player getPlayer() {
        return player;
    }

    public String getNpc_id() {
        return npc_id;
    }
}

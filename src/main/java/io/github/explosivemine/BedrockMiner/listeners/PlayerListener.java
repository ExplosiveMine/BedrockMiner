package io.github.explosivemine.BedrockMiner.listeners;

import io.github.explosivemine.BedrockMiner.BPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PlayerListener extends EventListener {
    public PlayerListener(BPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock().getType().equals(Material.DRAGON_EGG))
            event.setCancelled(plugin.getConfigSettings().defaultParser.isCancelDragonEggTeleport());
    }

}
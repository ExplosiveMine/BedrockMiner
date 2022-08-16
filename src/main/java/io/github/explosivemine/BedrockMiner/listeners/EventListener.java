package io.github.explosivemine.BedrockMiner.listeners;

import io.github.explosivemine.BedrockMiner.BPlugin;
import org.bukkit.event.Listener;

public abstract class EventListener implements Listener {
    protected final BPlugin plugin;

    public EventListener(BPlugin plugin) {
        this.plugin = plugin;
    }

}
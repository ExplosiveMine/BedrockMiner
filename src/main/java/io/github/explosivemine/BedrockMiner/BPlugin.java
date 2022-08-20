package io.github.explosivemine.BedrockMiner;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.util.concurrent.Callables;
import io.github.explosivemine.BedrockMiner.config.ConfigSettings;
import io.github.explosivemine.BedrockMiner.listeners.PacketListener;
import io.github.explosivemine.BedrockMiner.listeners.PlayerListener;
import lombok.Getter;
import io.github.explosivemine.BedrockMiner.util.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class BPlugin extends JavaPlugin {
    @Getter private final ConfigSettings configSettings = new ConfigSettings(this);

    private final Listener[]  listeners = new Listener[] {
            new PlayerListener(this)
    };

    @Override
    public void onEnable() {
        configSettings.init();

        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(this));

        Metrics metrics = new Metrics(this, 16204);
        metrics.addCustomChart(new Metrics.SimplePie("Stop Dragon Egg", Callables.returning(String.valueOf(configSettings.defaultParser.isCancelDragonEggTeleport()))));

        Map<String, Integer> materials = new HashMap<>();
        configSettings.defaultParser.getBlockSettings().keySet().forEach(material-> materials.put(material.toString(), 1));
        metrics.addCustomChart(new Metrics.SimpleBarChart("Blocks", Callables.returning(materials)));
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }

}
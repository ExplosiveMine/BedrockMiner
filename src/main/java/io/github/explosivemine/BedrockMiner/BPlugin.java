package io.github.explosivemine.BedrockMiner;

import com.comphenix.protocol.ProtocolLibrary;
import io.github.explosivemine.BedrockMiner.config.ConfigSettings;
import io.github.explosivemine.BedrockMiner.listeners.PacketListener;
import io.github.explosivemine.BedrockMiner.listeners.PlayerListener;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

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
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }

}
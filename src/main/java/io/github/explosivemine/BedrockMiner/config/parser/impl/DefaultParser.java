package io.github.explosivemine.BedrockMiner.config.parser.impl;

import io.github.explosivemine.BedrockMiner.config.parser.SectionParser;
import lombok.Getter;
import io.github.explosivemine.BedrockMiner.BPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public final class DefaultParser extends SectionParser {
    @Getter private final Map<Material, BlockSettingsParser> blockSettings = new HashMap<>();

    @Getter private boolean cancelDragonEggTeleport;
    @Getter private boolean debug;

    public DefaultParser(BPlugin plugin) {
        super(plugin, "");
    }

    @Override
    public void parse() {
        ConfigurationSection config = getConfig();

        for (String key : config.getConfigurationSection("Block Settings").getKeys(false)) {
            BlockSettingsParser parser = new BlockSettingsParser(plugin, "Block Settings." + key);
            parser.parse();
            blockSettings.put(parser.getMaterial(), parser);
        }

        cancelDragonEggTeleport = config.getBoolean("disable dragon egg teleport");
        debug = config.getBoolean("debug");
    }

}
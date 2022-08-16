package io.github.explosivemine.BedrockMiner.config;

import io.github.explosivemine.BedrockMiner.config.parser.SectionParser;
import io.github.explosivemine.BedrockMiner.config.parser.impl.DefaultParser;
import io.github.explosivemine.BedrockMiner.BPlugin;
import io.github.explosivemine.BedrockMiner.config.parser.Lang;

public final class ConfigSettings {
    private final BPlugin plugin;

    public DefaultParser defaultParser;

    public ConfigSettings(BPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        plugin.saveDefaultConfig();
        Lang.reload(plugin);

        defaultParser = new DefaultParser(plugin);

        SectionParser[] parsers = new SectionParser[] {
                defaultParser
        };

        for (SectionParser parser : parsers)
            parser.parse();
    }

}
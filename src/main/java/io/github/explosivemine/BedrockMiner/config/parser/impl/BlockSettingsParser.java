package io.github.explosivemine.BedrockMiner.config.parser.impl;

import lombok.Getter;
import io.github.explosivemine.BedrockMiner.BPlugin;
import io.github.explosivemine.BedrockMiner.config.parser.SectionParser;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class BlockSettingsParser extends SectionParser {
    @Getter private Material material;

    private final Map<Material, ToolParser> tools = new HashMap<>();

    public BlockSettingsParser(BPlugin plugin, @NotNull String sectionPath) {
        super(plugin, sectionPath);
    }

    @Override
    public void parse() {
        ConfigurationSection section = getSection();

        material = parseMaterial("material");

        for (String key : section.getConfigurationSection("tools").getKeys(false)) {
            ToolParser toolParser = new ToolParser(plugin, sectionPath + ".tools." + key);
            toolParser.parse();
            tools.put(toolParser.getMaterial(), toolParser);
        }
    }

    public @Nullable ToolParser getTool(Material material) {
        return tools.get(material);
    }

}
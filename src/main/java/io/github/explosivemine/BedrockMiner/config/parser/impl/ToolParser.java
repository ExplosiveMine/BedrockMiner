package io.github.explosivemine.BedrockMiner.config.parser.impl;

import lombok.Getter;
import io.github.explosivemine.BedrockMiner.BPlugin;
import io.github.explosivemine.BedrockMiner.config.parser.SectionParser;
import io.github.explosivemine.BedrockMiner.util.Logging;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ToolParser extends SectionParser {
    @Getter private Material material;

    @Getter private long ticks;

    @Getter private int damage;

    @Getter private boolean drop;
    @Getter private boolean addToInventory;

    String permission;

    public ToolParser(BPlugin plugin, @NotNull String sectionPath) {
        super(plugin, sectionPath);
    }

    @Override
    public void parse() {
        ConfigurationSection section = getSection();

        material = parseMaterial("type");

        ticks = section.getLong("duration", 0L);

        damage = section.getInt("durability", 1);

        drop = section.getBoolean("drop", true);
        addToInventory = section.getBoolean("add to inventory", false);

        if (drop && addToInventory)
            Logging.warning("Incompatible settings 'drop: true' and 'add to inventory: true'. (" + filePath + ":" + sectionPath + ")");

        permission = section.getString("permission", "");
    }

    public Optional<String> getPermission() {
        return permission.isEmpty() ? Optional.empty() : Optional.of(permission);
    }

}
package io.github.explosivemine.BedrockMiner.config.parser;

import io.github.explosivemine.BedrockMiner.BPlugin;
import io.github.explosivemine.BedrockMiner.util.FileUtils;
import io.github.explosivemine.BedrockMiner.util.Logging;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class SectionParser {
    protected final BPlugin plugin;

    protected final String filePath;
    protected final String sectionPath;

    private ConfigurationSection section;

    public SectionParser(BPlugin plugin, @NotNull String filePath, @NotNull String sectionPath) {
        this.plugin = plugin;
        this.filePath = filePath;
        this.sectionPath = sectionPath;
    }

    public SectionParser(BPlugin plugin, @NotNull String sectionPath) {
        this(plugin, "config.yml", sectionPath);
    }

    public abstract void parse();

    public ConfigurationSection getSection() {
        if (section == null) {
            this.section = getConfig();

            if (!sectionPath.isEmpty())
                this.section = section.getConfigurationSection(sectionPath);
        }

        return section;
    }

    public ConfigurationSection getConfig() {
        return filePath.equals("config.yml") ? plugin.getConfig() : YamlConfiguration.loadConfiguration(FileUtils.loadFile(plugin, filePath));
    }

    public Material parseMaterial(String path) {
        String materialName = getSection().getString(path, "AIR");
        Material material;
        try {
            material = Material.matchMaterial(materialName);
        } catch(IllegalArgumentException e) {
            material = Material.AIR;
            Logging.warning(materialName + " is not a valid material type. (" + filePath + ":" + sectionPath + "." + path + ")");
        }
        return material;
    }

}
package io.github.explosivemine.BedrockMiner.util;

import io.github.explosivemine.BedrockMiner.BPlugin;
import org.bukkit.Bukkit;

public final class Logging {

    public static void info(String s) {
        Bukkit.getLogger().info(s);
    }

    public static void warning(String s) {
        Bukkit.getLogger().warning(s);
    }

    public static void severe(String s) {
        Bukkit.getLogger().severe(s);
    }

    public static void debug(BPlugin plugin, String... args) {
        if (!plugin.getConfigSettings().defaultParser.isDebug())
            return;

        for (String s : args) {
            info("[" + plugin.getName() + "] " + s);
        }
    }
}

package io.github.explosivemine.BedrockMiner.util;

import io.github.explosivemine.BedrockMiner.BPlugin;

import java.io.File;

public final class FileUtils {
    public static File loadFile(BPlugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            if (plugin.getResource(path) == null)
                Logging.severe("Could not resolve path:" + path);
            else
                plugin.saveResource(path, false);
        }

        return file;
    }
}

package io.github.explosivemine.BedrockMiner.config.parser;

import io.github.explosivemine.BedrockMiner.BPlugin;
import io.github.explosivemine.BedrockMiner.util.StringUtils;
import io.github.explosivemine.BedrockMiner.util.Logging;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Lang {
    NO_PERMISSION,
    CUSTOM {
        @Override
        public void send(CommandSender sender, Object... objects) {
            if (objects.length > 0)
                sender.sendMessage(StringUtils.replaceArgs("{0}", objects));
        }
    };

    private static YamlConfiguration langCfg;

    private static final Map<Lang, Message> messages = new HashMap<>();

    public static void reload(BPlugin plugin) {
        Logging.info("Reloading messages...");
        long startTime = System.currentTimeMillis();

        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists())
            plugin.saveResource("lang.yml", false);

        langCfg = YamlConfiguration.loadConfiguration(langFile);

        Arrays.stream(values()).forEach(lang -> messages.put(lang, new Message(langCfg.getString(lang.name(), ""))));

        Logging.info(StringUtils.replaceArgs("Messages have been reloaded. This took {0} ms.", (System.currentTimeMillis()-startTime)));
    }

    public void send(CommandSender sender, Object... objects) {
        messages.get(this).send(sender, objects);
    }

    public String get(Object... objects) {
        return StringUtils.colour(StringUtils.replaceArgs(messages.get(this).getMessage(), objects));
    }

    private static final class Message {
        private final String message;

        Message(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }

        void send(CommandSender sender, Object... objects) {
            if (message != null && !message.isEmpty())
                sender.sendMessage(StringUtils.colour(StringUtils.replaceArgs(message, objects)));
        }
    }

}
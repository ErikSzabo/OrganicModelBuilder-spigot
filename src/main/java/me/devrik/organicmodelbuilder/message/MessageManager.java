package me.devrik.organicmodelbuilder.message;

import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.ChatColor;

import java.util.Objects;

public class MessageManager {
    private static ModelsPlugin mp;

    public static void init(ModelsPlugin plugin) {
        mp = plugin;
    }

    public static String m(Message selector) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(mp.getConfig().getString(selector.getValue())));
    }
}

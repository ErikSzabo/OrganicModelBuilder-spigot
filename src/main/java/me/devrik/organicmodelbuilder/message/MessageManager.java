package me.devrik.organicmodelbuilder.message;

import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Map;

public class MessageManager {
    private static ModelsPlugin mp;

    public static void init(ModelsPlugin plugin) {
        mp = plugin;
    }

    public static String g(Message selector, Map<String, String> placeholders) {
        return createMessage(selector, placeholders);
    }

    public static String g(Message selector) {
        return createMessage(selector, null);
    }

    public static void m(Player player, Message selector, Map<String, String> placeholders) {
        Bukkit.getPlayer(player.getUniqueId()).sendMessage(createMessage(selector, placeholders));
    }

    public static void m(org.bukkit.entity.Player player, Message selector, Map<String, String> placeholders) {
        player.sendMessage(createMessage(selector, placeholders));
    }

    public static void m(Player player, Message selector) {
        Bukkit.getPlayer(player.getUniqueId()).sendMessage(createMessage(selector, null));
    }

    public static void m(org.bukkit.entity.Player player, Message selector) {
        player.sendMessage(createMessage(selector, null));
    }

    private static String createMessage(Message selector, Map<String, String> placeholders) {
        String message = mp.getConfig().getString(selector.getValue());
        if(placeholders != null) message = replacePlaceholders(message, placeholders);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String replacePlaceholders(String message, Map<String, String> placeholders) {
        for(String placeholder : placeholders.keySet()) {
            message = message.replaceAll("%" + placeholder +"%", placeholders.get(placeholder));
        }
        return message;
    }
}

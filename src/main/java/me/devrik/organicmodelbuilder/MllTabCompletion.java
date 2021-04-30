package me.devrik.organicmodelbuilder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MllTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("models.use")) return null;
        List<String> suggestions = new ArrayList<>();

        if(sender instanceof ConsoleCommandSender) {
            return suggestions;
        }

        if(args.length == 1) {
            suggestions.addAll(ModelsPlugin.getStateManager().getModelList());
            return suggestions;
        }

        if(args.length == 2) {
            for(int i = 3; i < 10; i++) suggestions.add(String.format("0.%d", i));
            suggestions.add("1");
            for(int i = 1; i < 10; i++) suggestions.add(String.format("1.%d", i));
            suggestions.add("2");
            return suggestions;
        }

        return null;
    }
}

package me.devrik.organicmodelbuilder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission("models.use")) return null;
        List<String> suggestions = new ArrayList<>();

        if(args.length == 1) {
            for(me.devrik.organicmodelbuilder.command.Command cmd : ModelsPlugin.getCommandManager().getCommands()) {
                suggestions.add(cmd.getName());
            }
            return suggestions;
        }

        if(args.length >= 1 && args[0].equals("load")) {
            suggestions.addAll(ModelsPlugin.getStateManager().getModelList());
            return suggestions;
        }

        return null;
    }
}

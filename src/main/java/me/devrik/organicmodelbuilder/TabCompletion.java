package me.devrik.organicmodelbuilder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission("models.use")) return null;
        List<String> suggestions = new ArrayList<>();
        if(sender instanceof ConsoleCommandSender) {
            if(ModelsPlugin.getStateManager().isInit()) return null;
            suggestions.add("init");
            return suggestions;
        }

        Player player = (Player) sender;

        if(args.length == 1) {
            for(me.devrik.organicmodelbuilder.command.Command cmd : ModelsPlugin.getCommandManager().getCommands()) {
                if(cmd.canRunByPlayerRightNow(player)) suggestions.add(cmd.getName());
            }
            return suggestions;
        }

        if(args.length == 2 && args[0].equals("load")) {
            suggestions.addAll(ModelsPlugin.getStateManager().getModelList());
            return suggestions;
        }

        if(args.length == 3 && args[0].equals("load")) {
            for(int i = 3; i < 10; i++) suggestions.add(String.format("0.%d", i));
            suggestions.add("1");
            for(int i = 1; i < 10; i++) suggestions.add(String.format("1.%d", i));
            suggestions.add("2");
            return suggestions;
        }

        return null;
    }
}

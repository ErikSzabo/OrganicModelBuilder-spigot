package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Collection;
import java.util.HashMap;

public class CommandManager {
    private final HashMap<String, Command> commands = new HashMap<>();

    public void addCommand(Command command) {
            commands.put(command.getName(), command);
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public void executeCommand(String name, CommandSender sender, String args[]) {
        if(!commands.containsKey(name)) {
            sender.sendMessage(MessageManager.m(Message.CMD_INVALID));
            return;
        }

        Command command = commands.get(name);

        if((sender instanceof ConsoleCommandSender) && !command.canRunByConsole()) {
            sender.sendMessage(MessageManager.m(Message.PLAYER_ONLY));
            return;
        }

        if(!sender.hasPermission("models.use")) {
            sender.sendMessage(MessageManager.m(Message.NO_PERMISSION));
            return;
        }

        try {
            this.commands.get(name).execute(sender, args);
        } catch (CommandException e) {
            sender.sendMessage(e.getMessage());
        }
    }

}

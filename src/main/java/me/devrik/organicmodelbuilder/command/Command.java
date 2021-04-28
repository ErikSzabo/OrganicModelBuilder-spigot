package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

public abstract class Command {
    private final String name;
    private final String usage;
    private boolean console = false;

    public Command(String name, String usage) {
        this.name = name;
        this.usage = usage;
    }
    public Command(String name, String usage, boolean console) {
        this(name, usage);
        this.console = console;
    }

    public abstract void execute(CommandSender sender, String[] args) throws CommandException;

    public abstract String getDescription();

    public String getName() {
        return name;
    }

    public String getUsage() {
        return "/model " + name + " " + usage;
    }

    public boolean canRunByConsole() {
        return console;
    }
}

package me.devrik.organicmodelbuilder;

import java.io.File;
import java.util.logging.Logger;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.devrik.organicmodelbuilder.command.*;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.model.factory.FAWEModelFactory;
import me.devrik.organicmodelbuilder.model.factory.ModelFactory;
import me.devrik.organicmodelbuilder.model.factory.WEModelFactory;
import me.devrik.organicmodelbuilder.util.ModelInitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ModelsPlugin extends JavaPlugin {
    private static final CommandManager commandManager = new CommandManager();
    private static final StateManager stateManager = new StateManager();
    private static ModelFactory modelFactory;
    private static Logger logger;
    private static WorldEditPlugin we;
    private static ModelsPlugin plugin;

    public void onLoad() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        saveDefaultConfig();
        logger = getLogger();
        plugin = this;

        commandManager.addCommand(new AdjustCommand());
        commandManager.addCommand(new UndoCommand());
        commandManager.addCommand(new EndCommand());
        commandManager.addCommand(new HelpCommand());
        commandManager.addCommand(new InitCommand());
        commandManager.addCommand(new ListCommand());
        commandManager.addCommand(new LoadCommand());
        commandManager.addCommand(new RollCommand());
        commandManager.addCommand(new CancelCommand());

        File f = new File(this.getDataFolder(), "models");
        if (!f.exists()) f.mkdir();

        we = WorldEditPlugin.getPlugin(WorldEditPlugin.class);

        if (Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null) {
            this.getLogger().info("Found FAWE");
            modelFactory = new FAWEModelFactory();
        } else if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            this.getLogger().info("Found WorldEdit");
            modelFactory = new WEModelFactory();
        } else {
            this.getLogger().warning("Couldn't find WorldEdit or FAWE!");
        }

        MessageManager.init(this);
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerInteractEventHandler(), this);
        this.getCommand("model").setTabCompleter(new TabCompletion());
        this.getLogger().warning("Model initialization will start after 30 seconds automatically!");
        new ModelInitRunnable().runTaskLaterAsynchronously(this, 600);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(MessageManager.m(Message.CMD_INVALID));
            return true;
        }
        commandManager.executeCommand(args[0], sender, args);
        return true;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static StateManager getStateManager() {
        return stateManager;
    }

    public static ModelFactory getModelFactory() {
        return modelFactory;
    }

    public static WorldEditPlugin getWE() {
        return we;
    }

    public static Logger logger() {
        return logger;
    }

    public static ModelsPlugin getInstance() {
        return plugin;
    }
}

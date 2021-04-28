package me.devrik.organicmodelbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import java.util.*;

import me.devrik.organicmodelbuilder.command.*;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.util.ModelInitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ModelsPlugin extends JavaPlugin {
    private final CommandManager commandManager = new CommandManager();
    private final StateManager stateManager = new StateManager();
    public WorldEditPlugin worldedit;
    public static boolean FAWE = false;

    public ModelsPlugin() {
    }

    public void onLoad() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        saveDefaultConfig();
        commandManager.addCommand(new AdjustCommand());
        commandManager.addCommand(new UndoCommand());
        commandManager.addCommand(new EndCommand());
        commandManager.addCommand(new HelpCommand(commandManager));
        commandManager.addCommand(new InitCommand());
        commandManager.addCommand(new ListCommand());
        commandManager.addCommand(new LoadCommand());
        commandManager.addCommand(new RollCommand());
        commandManager.addCommand(new CancelCommand());

        File f = new File(this.getDataFolder(), "models");
        if (!f.exists()) {
            f.mkdir();
        }

        this.worldedit = WorldEditPlugin.getPlugin(WorldEditPlugin.class);
        if (Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null) {
            FAWE = true;
        }

        if (FAWE) {
            this.getLogger().info("Found FAWE");
        } else if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            this.getLogger().info("Found WorldEdit");
        } else {
            this.getLogger().warning("Couldn't find WorldEdit or FAWE!");
        }

        MessageManager.init(this);
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerInteractEventHandler(this), this);
        this.getCommand("model").setTabCompleter(new TabCompletion(this));
        this.getLogger().warning("Model initialization will start after 30 seconds automatically!");
        new ModelInitRunnable(this).runTaskLaterAsynchronously(this, 600);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(MessageManager.m(Message.CMD_INVALID));
            return true;
        }
        commandManager.executeCommand(args[0], this, sender, args);
        return true;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public StateManager getStateManager() {
        return stateManager;
    }
}

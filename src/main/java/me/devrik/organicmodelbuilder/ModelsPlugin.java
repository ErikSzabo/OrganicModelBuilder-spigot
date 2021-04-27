package me.devrik.organicmodelbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import me.devrik.organicmodelbuilder.command.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ModelsPlugin extends JavaPlugin implements Listener {
    private final CommandManager commandManager = new CommandManager();
    public HashMap<String, ModelPart> registry = new HashMap<>();
    public HashMap<UUID, Model> currents = new HashMap<>();
    public WorldEditPlugin worldedit;
    public static boolean FAWE = false;


    private boolean isInit = false;

    public ModelsPlugin() {
    }

    public void onLoad() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        saveDefaultConfig();
        commandManager.addCommand(new AdjustCommand());
        commandManager.addCommand(new CancelCommand());
        commandManager.addCommand(new EndCommand());
        commandManager.addCommand(new HelpCommand(commandManager));
        commandManager.addCommand(new InitCommand());
        commandManager.addCommand(new ListCommand());
        commandManager.addCommand(new LoadCommand());
        commandManager.addCommand(new RollCommand());

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

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(MessageManager.m(Message.CMD_INVALID));
            return true;
        }
        commandManager.executeCommand(args[0], this, sender, args);
        return true;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        getCommand("model").setTabCompleter(new TabCompletion(this));
        this.getLogger().warning("Model initialization will start after 30 seconds automatically!");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isInit) {
                    isInit = true;
                    File f = new File(getDataFolder(), "models");
                    try {
                        File[] files = f.listFiles();

                        for (File sub : files) {
                            if (sub.isDirectory()) {
                                File json = new File(sub, sub.getName() + ".json");

                                try {
                                    JsonObject obj = (new JsonParser()).parse(new FileReader(json)).getAsJsonObject();
                                    String modelName = sub.getName();
                                    ModelPart part = loadModel(modelName, obj);
                                    registry.put(modelName, part);
                                } catch (IOException | IllegalAccessException | InstantiationException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        getLogger().info(ChatColor.GREEN + "Model initialization was successful!");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                        getLogger().warning(ChatColor.RED + "Model initialization failed! Check console for errors.");
                    }
                } else {
                    getLogger().info(ChatColor.GREEN + "Already initialized!");
                }
            }
        }.runTaskLaterAsynchronously(this, 600);
    }

    public ModelPart loadModel(String modelName, JsonObject obj) throws IOException, IllegalAccessException, InstantiationException {
        String name = obj.get("Name").getAsString();
        boolean flip = obj.get("flip").getAsBoolean();
        ModelPart[] children = this.loadChildren(modelName, obj.get("Childs").getAsJsonArray());
        return new ModelPart(this, modelName, name, children, flip);
    }


    private ModelPart[] loadChildren(String modelName, JsonArray children) throws IOException, IllegalAccessException, InstantiationException {
        ModelPart[] parts = new ModelPart[children.size()];

        for(int i = 0; i < children.size(); ++i) {
            parts[i] = this.loadModel(modelName, children.get(i).getAsJsonObject());
        }

        return parts;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (this.currents.containsKey(e.getPlayer().getUniqueId())) {
            Model model = this.currents.get(e.getPlayer().getUniqueId());
            if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
                if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && model.cancel(this.worldedit.wrapPlayer(e.getPlayer()))) {
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Aborted model creation");
                    this.currents.remove(e.getPlayer().getUniqueId());
                }
            } else {
                model.next(this.worldedit.wrapPlayer(e.getPlayer()));
            }
        }
    }
}

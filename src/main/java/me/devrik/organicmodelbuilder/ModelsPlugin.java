package me.devrik.organicmodelbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.FileUtil;

public class ModelsPlugin extends JavaPlugin implements Listener {
    public HashMap<String, ModelPart> registry = new HashMap<>();
    public HashMap<UUID, Model> currents = new HashMap<>();
    public ModelsCommands commands;
    public WorldEditPlugin worldedit;
    public static boolean FAWE = false;
    private boolean isInit = false;

    public ModelsPlugin() {
    }

    public void onLoad() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();

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
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0 && args[0].equals("init")) {
            if (!isInit) {
                isInit = true;
                File f = new File(this.getDataFolder(), "models");
                try {
                    File[] files = f.listFiles();

                    for (File sub : files) {
                        if (sub.isDirectory()) {
                            File json = new File(sub, sub.getName() + ".json");

                            try {
                                JsonObject obj = (new JsonParser()).parse(new FileReader(json)).getAsJsonObject();
                                String modelName = sub.getName();
                                ModelPart part = this.loadModel(modelName, obj);
                                this.registry.put(modelName, part);
                            } catch (IOException | IllegalAccessException | InstantiationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    sender.sendMessage(ChatColor.GREEN + "Model initialization was successful!");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Model initialization failed! Check console for errors.");
                }
            } else {
                sender.sendMessage(ChatColor.GREEN + "Already initialized!");
            }
            return true;
        }


        if (sender instanceof Player) {
            com.sk89q.worldedit.entity.Player player = this.worldedit.wrapPlayer((Player)sender);
            if (args.length >= 1) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                return this.commands.onCommand(args[0], player, newArgs);
            } else {
                sender.sendMessage("These are the possible commands : ");
                return this.commands.onCommand("help", player, new String[0]);
            }
        } else {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.commands = new ModelsCommands(this);
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

    private ModelPart loadModel(String modelName, JsonObject obj) throws IOException, IllegalAccessException, InstantiationException {
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

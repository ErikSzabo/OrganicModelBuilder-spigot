package me.devrik.organicmodelbuilder.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.devrik.organicmodelbuilder.model.ModelPart;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ModelInitRunnable extends BukkitRunnable {
    @Override
    public void run() {
        if(ModelsPlugin.getStateManager().isInit()) {
            ModelsPlugin.logger().info(ChatColor.GREEN + "Already initialized!");
            return;
        }
        File f = new File(ModelsPlugin.getInstance().getDataFolder(), "models");
        try {
            File[] files = f.listFiles();

            for (File sub : files) {
                if (sub.isDirectory()) {
                    File json = new File(sub, sub.getName() + ".json");

                    try {
                        JsonObject obj = (new JsonParser()).parse(new FileReader(json)).getAsJsonObject();
                        String modelName = sub.getName();
                        ModelPart part = ModelLoader.loadModel(modelName, obj);
                        ModelsPlugin.getStateManager().registerModelPart(part);
                    } catch (IOException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            ModelsPlugin.logger().info(ChatColor.GREEN + "Model initialization was successful!");
            ModelsPlugin.getStateManager().setInit(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            ModelsPlugin.logger().warning(ChatColor.RED + "Model initialization failed! Check console for errors.");
        }
    }
}

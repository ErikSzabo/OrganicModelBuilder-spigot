package me.devrik.organicmodelbuilder.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.devrik.organicmodelbuilder.ModelPart;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ModelInitRunnable extends BukkitRunnable {
    private final ModelsPlugin pl;

    public ModelInitRunnable(ModelsPlugin pl) {
        this.pl = pl;
    }

    @Override
    public void run() {
        if(pl.getStateManager().isInit()) {
            pl.getLogger().info(ChatColor.GREEN + "Already initialized!");
            return;
        }
        File f = new File(pl.getDataFolder(), "models");
        try {
            File[] files = f.listFiles();

            for (File sub : files) {
                if (sub.isDirectory()) {
                    File json = new File(sub, sub.getName() + ".json");

                    try {
                        JsonObject obj = (new JsonParser()).parse(new FileReader(json)).getAsJsonObject();
                        String modelName = sub.getName();
                        ModelPart part = ModelLoader.loadModel(pl, modelName, obj);
                        pl.getStateManager().registerModelPart(part);
                    } catch (IOException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            pl.getLogger().info(ChatColor.GREEN + "Model initialization was successful!");
            pl.getStateManager().setInit(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            pl.getLogger().warning(ChatColor.RED + "Model initialization failed! Check console for errors.");
        }
    }
}

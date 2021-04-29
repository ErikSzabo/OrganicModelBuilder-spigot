package me.devrik.organicmodelbuilder.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.model.ModelPart;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import me.devrik.organicmodelbuilder.util.ModelLoader;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileReader;

public class InitCommand extends Command {
    public InitCommand() {
        super("init", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(ModelsPlugin.getStateManager().isInit()) {
            sender.sendMessage(MessageManager.g(Message.INIT_ALREADY));
            return;
        }

        File f = new File(ModelsPlugin.getInstance().getDataFolder(), "models");
        try {
            File[] files = f.listFiles();

            for (File sub : files) {
                if (sub.isDirectory()) {
                    File json = new File(sub, sub.getName() + ".json");
                    JsonObject obj = (new JsonParser()).parse(new FileReader(json)).getAsJsonObject();
                    String modelName = sub.getName();
                    ModelPart part = ModelLoader.loadModel(modelName, obj);
                    ModelsPlugin.getStateManager().registerModelPart(part);
                }
            }

            ModelsPlugin.getStateManager().setInit(true);
            sender.sendMessage(MessageManager.g(Message.INIT_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(MessageManager.g(Message.INIT_FAILED));
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.g(Message.CMD_INIT);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        return !ModelsPlugin.getStateManager().isInit();
    }
}

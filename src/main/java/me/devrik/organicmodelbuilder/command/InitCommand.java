package me.devrik.organicmodelbuilder.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.minecraft.util.commands.CommandException;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelPart;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileReader;

public class InitCommand extends Command {
    public InitCommand() {
        super("init", "", true);
    }

    @Override
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        if(pl.isInit()) {
            sender.sendMessage(MessageManager.m(Message.INIT_ALREADY));
            return;
        }

        File f = new File(pl.getDataFolder(), "models");
        try {
            File[] files = f.listFiles();

            for (File sub : files) {
                if (sub.isDirectory()) {
                    File json = new File(sub, sub.getName() + ".json");
                    JsonObject obj = (new JsonParser()).parse(new FileReader(json)).getAsJsonObject();
                    String modelName = sub.getName();
                    ModelPart part = pl.loadModel(modelName, obj);
                    pl.registry.put(modelName, part);
                }
            }

            pl.setInit(true);
            sender.sendMessage(MessageManager.m(Message.INIT_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(MessageManager.m(Message.INIT_FAILED));
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_INIT);
    }
}

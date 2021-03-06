package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import me.devrik.organicmodelbuilder.*;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.message.Placeholder;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.factory.ModelFactory;
import org.bukkit.command.CommandSender;

public class LoadCommand extends Command {
    public LoadCommand() {
        super("load",  "<model> [scale] [pattern]");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = ModelsPlugin.getWE().wrapPlayer(player);
        ModelFactory factory = ModelsPlugin.getModelFactory();
        StateManager stateManager = ModelsPlugin.getStateManager();

        if (args.length < 2) {
            throw new CommandException(MessageManager.g(Message.NOT_ENOUGH_ARGS));
        }

        String modelName = args[1];
        double scale = 1.0D;
        if (args.length > 2) {
            try {
                scale = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                throw new CommandException(MessageManager.g(Message.SCALE_NOT_NUMBER));
            }
        }

        if (scale > 2.0D) {
            throw new CommandException(MessageManager.g(Message.SCALE_TOO_BIG));
        }

        Pattern pattern = null;
        if (args.length > 3) {
            ParserContext parserContext = new ParserContext();
            parserContext.setActor(p);
            parserContext.setExtent(p.getWorld());
            parserContext.setWorld(p.getWorld());
            parserContext.setSession(WorldEdit.getInstance().getSessionManager().get(p));

            try {
                pattern = WorldEdit.getInstance().getPatternFactory().parseFromInput(args[3], parserContext);
            } catch (InputParseException e) {
                throw new CommandException(MessageManager.g(Message.PATTERN_ERROR) + " " + e.getMessage());
            }
        }

        if (stateManager.hasPlayerSession(p)) {
            throw new CommandException(MessageManager.g(Message.ALREADY_CREATING));
        }

        if (!stateManager.getModelList().contains(modelName.toLowerCase())) {
            throw new CommandException(MessageManager.g(Message.MODEL_NOT_FOUND));
        }

        Model model = factory.getModel(stateManager.getModelPart(modelName.toLowerCase()), scale, pattern, p.getWorld());
        stateManager.registerPlayerSession(p, model);
        MessageManager.m(player, Message.MODEL_LOADED1);
        MessageManager.m(player, Message.MODEL_LOADED2);
        MessageManager.m(player, Message.MODEL_LOADED3, Placeholder.of("cmd_usage", "/model roll <value>"));
    }

    @Override
    public String getDescription() {
        return MessageManager.g(Message.CMD_LOAD);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        Player p = ModelsPlugin.getWE().wrapPlayer(player);
        return !ModelsPlugin.getStateManager().hasPlayerSession(p);
    }
}

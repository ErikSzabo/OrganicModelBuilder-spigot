package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import me.devrik.organicmodelbuilder.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LoadCommand extends Command {
    public LoadCommand() {
        super("load",  "<model> [scale] [pattern]");
    }

    @Override
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = pl.worldedit.wrapPlayer(player);

        if (args.length < 2) {
            throw new CommandException(MessageManager.m(Message.NOT_ENOUGH_ARGS));
        }

        String model = args[1];
        double scale = 1.0D;
        if (args.length > 2) {
            try {
                scale = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                throw new CommandException(MessageManager.m(Message.SCALE_NOT_NUMBER));
            }
        }

        if (scale > 2.0D) {
            throw new CommandException(MessageManager.m(Message.SCALE_TOO_BIG));
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
                throw new CommandException(MessageManager.m(Message.PATTERN_ERROR) + " " + e.getMessage());
            }
        }

        if (pl.currents.containsKey(p.getUniqueId())) {
            throw new CommandException(MessageManager.m(Message.ALREADY_CREATING));
        }

        if (!pl.registry.containsKey(model.toLowerCase())) {
            throw new CommandException(MessageManager.m(Message.MODEL_NOT_FOUND));
        }

        Model m = new Model(pl.registry.get(model.toLowerCase()), scale, pattern, p.getWorld());
        pl.currents.put(p.getUniqueId(), m);
        player.sendMessage(MessageManager.m(Message.MODEL_LOADED1));
        player.sendMessage(MessageManager.m(Message.MODEL_LOADED2));
        player.sendMessage(MessageManager.m(Message.MODEL_LOADED3));
        player.sendMessage("" + ChatColor.WHITE + ChatColor.ITALIC + "/model roll <value>");

    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_LOAD);
    }
}

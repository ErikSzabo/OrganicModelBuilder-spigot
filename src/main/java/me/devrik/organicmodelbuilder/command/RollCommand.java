package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.Message;
import me.devrik.organicmodelbuilder.MessageManager;
import me.devrik.organicmodelbuilder.Model;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

public class RollCommand extends Command {
    public RollCommand() {
        super("roll", "<roll>");
    }

    @Override
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = pl.worldedit.wrapPlayer(player);

        if (args.length < 2) {
            throw new CommandException(MessageManager.m(Message.NOT_ENOUGH_ARGS));
        }

        if (!pl.currents.containsKey(p.getUniqueId())) {
            throw new CommandException(MessageManager.m(Message.NOT_CREATING));
        }

        double rot;

        try {
            rot = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandException(MessageManager.m(Message.ROTATION_NOT_NUMBER));
        }

        Model current = pl.currents.get(p.getUniqueId());
        current.currentRoll = rot % 360.0D;
        player.sendMessage(MessageManager.m(Message.ROLL_SUCCESS) + current.currentRoll + "°");

    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_ROLL);
    }
}

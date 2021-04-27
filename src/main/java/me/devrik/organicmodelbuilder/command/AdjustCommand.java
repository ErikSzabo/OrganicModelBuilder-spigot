package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.Message;
import me.devrik.organicmodelbuilder.MessageManager;
import me.devrik.organicmodelbuilder.Model;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

public class AdjustCommand extends Command {
    public AdjustCommand() {
        super("adjust", "<partName> <yaw> <pitch> <roll>");
    }

    @Override
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = pl.worldedit.wrapPlayer(player);

        if (args.length < 5) {
            throw new CommandException(MessageManager.m(Message.NOT_ENOUGH_ARGS));
        }

        String partName = args[1];

        double yaw, pitch, roll;
        try {
            yaw = Double.parseDouble(args[2]);
            pitch = Double.parseDouble(args[3]);
            roll = Double.parseDouble(args[4]);
        } catch (NumberFormatException var13) {
            throw new CommandException(MessageManager.m(Message.YRP_NUMBER));
        }

        if (!pl.currents.containsKey(p.getUniqueId())) {
            throw new CommandException(MessageManager.m(Message.NOT_CREATING));
        }

        Model current = pl.currents.get(p.getUniqueId());

        if (current.modify(p, partName, yaw, pitch, roll)) {
            player.sendMessage(MessageManager.m(Message.PART_MODIFIED));
            player.sendMessage(String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", yaw, pitch, roll));
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_ADJUST);
    }
}

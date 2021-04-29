package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

public class AdjustCommand extends Command {
    public AdjustCommand() {
        super("adjust", "<partName> <yaw> <pitch> <roll>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = ModelsPlugin.getWE().wrapPlayer(player);

        if (args.length < 5) {
            throw new CommandException(MessageManager.g(Message.NOT_ENOUGH_ARGS));
        }

        String partName = args[1];

        double yaw, pitch, roll;
        try {
            yaw = Double.parseDouble(args[2]);
            pitch = Double.parseDouble(args[3]);
            roll = Double.parseDouble(args[4]);
        } catch (NumberFormatException var13) {
            throw new CommandException(MessageManager.g(Message.YRP_NUMBER));
        }

        if (!ModelsPlugin.getStateManager().hasPlayerSession(p)) {
            throw new CommandException(MessageManager.g(Message.NOT_CREATING));
        }

        Model model = ModelsPlugin.getStateManager().getSession(p);
        boolean success = model.modify(p, partName, yaw, pitch, roll);

        if (success) {
            MessageManager.m(player, Message.PART_MODIFIED);
            player.sendMessage(String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", yaw, pitch, roll));
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.g(Message.CMD_ADJUST);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        Player p = ModelsPlugin.getWE().wrapPlayer(player);
        return ModelsPlugin.getStateManager().hasPlayerSession(p) && ModelsPlugin.getStateManager().getSession(p).getCurrentIndex() != 0;
    }
}

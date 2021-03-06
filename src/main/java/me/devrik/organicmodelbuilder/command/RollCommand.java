package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.message.Placeholder;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

public class RollCommand extends Command {
    public RollCommand() {
        super("roll", "<roll>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = ModelsPlugin.getWE().wrapPlayer(player);

        if (args.length < 2) {
            throw new CommandException(MessageManager.g(Message.NOT_ENOUGH_ARGS));
        }

        if (!ModelsPlugin.getStateManager().hasPlayerSession(p)) {
            throw new CommandException(MessageManager.g(Message.NOT_CREATING));
        }

        double rot;

        try {
            rot = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandException(MessageManager.g(Message.ROTATION_NOT_NUMBER));
        }

        Model model = ModelsPlugin.getStateManager().getSession(p);
        model.setCurrentRoll(rot % 360.0D);
        MessageManager.m(player, Message.ROLL_SUCCESS, Placeholder.of("roll", model.getCurrentRoll() + "°"));
    }

    @Override
    public String getDescription() {
        return MessageManager.g(Message.CMD_ROLL);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        Player p = ModelsPlugin.getWE().wrapPlayer(player);
        return ModelsPlugin.getStateManager().hasPlayerSession(p);
    }
}

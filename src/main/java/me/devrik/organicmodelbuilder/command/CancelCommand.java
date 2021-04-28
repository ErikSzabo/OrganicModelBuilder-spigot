package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import org.bukkit.command.CommandSender;

public class CancelCommand extends Command{
    public CancelCommand() {
        super("cancel", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = ModelsPlugin.getWE().wrapPlayer(player);

        if (!ModelsPlugin.getStateManager().hasPlayerSession(p)) {
            throw new CommandException(MessageManager.m(Message.NOT_CREATING));
        }

        Model model = ModelsPlugin.getStateManager().getSession(p);

        player.sendMessage(MessageManager.m(Message.MERGING_PARTS));
        model.cancel(p);
        ModelsPlugin.getStateManager().unRegisterPlayerSession(p);
        player.sendMessage(MessageManager.m(Message.CANCEL_SUCCESS));
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_CANCEL);
    }
}

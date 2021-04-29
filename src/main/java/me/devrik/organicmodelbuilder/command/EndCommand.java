package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

public class EndCommand extends Command{
    public EndCommand() {
        super("end", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = ModelsPlugin.getWE().wrapPlayer(player);

        if (!ModelsPlugin.getStateManager().hasPlayerSession(p)) {
            throw new CommandException(MessageManager.m(Message.NOT_CREATING));
        }

        Model model = ModelsPlugin.getStateManager().getSession(p);
        if (!model.isAtTheEnd()) {
            throw new CommandException(MessageManager.m(Message.NOT_COMPLETED));
        }

        player.sendMessage(MessageManager.m(Message.ADD_TO_HISTORY));
        model.finalise(ModelsPlugin.getInstance(), p);
        ModelsPlugin.getStateManager().unRegisterPlayerSession(p);
        player.sendMessage(MessageManager.m(Message.VALIDATION_SUCCESS));
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_END);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        Player p = ModelsPlugin.getWE().wrapPlayer(player);
        return ModelsPlugin.getStateManager().hasPlayerSession(p);
    }
}

package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.message.Placeholder;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class UndoCommand extends Command {
    public UndoCommand() {
        super("undo", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = ModelsPlugin.getWE().wrapPlayer(player);

        if (!ModelsPlugin.getStateManager().hasPlayerSession(p)) {
            throw new CommandException(MessageManager.g(Message.NOT_CREATING));
        }

        Model model = ModelsPlugin.getStateManager().getSession(p);

        boolean success = model.undo(p);

        if(success) {
            MessageManager.m(player, Message.UNDID, Placeholder.of("part", model.getBeforePart().getName()));
        } else {
            MessageManager.m(player, Message.NOTHING_TO_UNDO);
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.g(Message.CMD_UNDO);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        Player p = ModelsPlugin.getWE().wrapPlayer(player);
        return ModelsPlugin.getStateManager().hasPlayerSession(p);
    }
}

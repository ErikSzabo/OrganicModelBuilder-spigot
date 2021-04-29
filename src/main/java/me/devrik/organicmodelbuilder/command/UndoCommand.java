package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class UndoCommand extends Command {
    public UndoCommand() {
        super("undo", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = ModelsPlugin.getWE().wrapPlayer(player);

        if (!ModelsPlugin.getStateManager().hasPlayerSession(p)) {
            throw new CommandException(MessageManager.m(Message.NOT_CREATING));
        }

        Model model = ModelsPlugin.getStateManager().getSession(p);

        boolean success = model.undo(p);

        if(success) {
            player.sendMessage(MessageManager.m(Message.UNDOED) + ChatColor.BOLD + ChatColor.WHITE + model.getCurrentPart().getName());
        } else {
            player.sendMessage(MessageManager.m(Message.NOTHING_TO_UNDO));
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_UNDO);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        Player p = ModelsPlugin.getWE().wrapPlayer(player);
        return ModelsPlugin.getStateManager().hasPlayerSession(p);
    }
}

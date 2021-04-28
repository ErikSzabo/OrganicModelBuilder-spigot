package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.Model;
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
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = pl.worldedit.wrapPlayer(player);

        if (!pl.getStateManager().hasPlayerSession(p)) {
            throw new CommandException(MessageManager.m(Message.NOT_CREATING));
        }

        Model model = pl.getStateManager().getSession(p);

        boolean success = model.undo(p);

        if(success) {
            player.sendMessage(MessageManager.m(Message.UNDOED) + ChatColor.BOLD + ChatColor.WHITE + model.getOrder().get(model.getCurrentIndex()));
        } else {
            player.sendMessage(MessageManager.m(Message.NOTHING_TO_UNDO));
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_UNDO);
    }
}

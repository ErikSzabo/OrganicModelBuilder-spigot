package me.devrik.organicmodelbuilder.commands;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.Message;
import me.devrik.organicmodelbuilder.MessageManager;
import me.devrik.organicmodelbuilder.Model;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CancelCommand extends Command {
    public CancelCommand() {
        super("cancel", "");
    }

    @Override
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = pl.worldedit.wrapPlayer(player);

        if (!pl.currents.containsKey(p.getUniqueId())) {
            throw new CommandException(MessageManager.m(Message.NOT_CREATING));
        }

        Model current = pl.currents.get(p.getUniqueId());

        current.cancel(p);
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_CANCEL);
    }
}

package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        ModelsPlugin.getInstance().reloadConfig();
        sender.sendMessage(MessageManager.g(Message.RELOADED));
    }

    @Override
    public String getDescription() {
        return MessageManager.g(Message.CMD_RELOAD);
    }

    @Override
    public boolean canRunByPlayerRightNow(Player player) {
        return true;
    }
}

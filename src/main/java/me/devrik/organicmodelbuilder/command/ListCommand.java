package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand extends Command {
    public ListCommand() {
        super("list", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player)sender;

        StringBuilder builder = new StringBuilder();
        for(String key : ModelsPlugin.getStateManager().getModelList()) {
            builder.append("   " + ChatColor.LIGHT_PURPLE + "- " + ChatColor.YELLOW).append(key).append("\n");
        }
        player.sendMessage(MessageManager.m(Message.LOADED_MODELS));
        player.sendMessage(builder.toString());
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_LIST);
    }

    @Override
    public boolean canRunByPlayerRightNow(org.bukkit.entity.Player player) {
        return true;
    }
}

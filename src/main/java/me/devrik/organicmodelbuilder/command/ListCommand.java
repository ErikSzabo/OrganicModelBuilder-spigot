package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.Message;
import me.devrik.organicmodelbuilder.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends Command {
    public ListCommand() {
        super("list", "");
    }

    @Override
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = pl.worldedit.wrapPlayer(player);

        StringBuilder builder = new StringBuilder();
        for(String key : pl.registry.keySet()) {
            builder.append("   " + ChatColor.RESET + "- " + ChatColor.YELLOW).append(key).append("\n");
        }
        player.sendMessage(MessageManager.m(Message.LOADED_MODELS));
        player.sendMessage(builder.toString());
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_LIST);
    }
}

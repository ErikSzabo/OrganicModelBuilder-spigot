package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;


public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;

        player.sendMessage(MessageManager.m(Message.HELP));
        for(Command c : ModelsPlugin.getCommandManager().getCommands()) {
            HoverEvent e = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new Text(net.md_5.bungee.api.ChatColor.GREEN + c.getDescription())));
            player.spigot().sendMessage(new ComponentBuilder("  -  /model ").append(c.getName()).color(net.md_5.bungee.api.ChatColor.GOLD).event(e).append(" : ").append(c.getUsage()).italic(true).color(net.md_5.bungee.api.ChatColor.YELLOW).create());
        }
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

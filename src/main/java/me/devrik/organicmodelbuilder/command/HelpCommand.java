package me.devrik.organicmodelbuilder.command;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.Message;
import me.devrik.organicmodelbuilder.MessageManager;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;


public class HelpCommand extends Command {
    private CommandManager manager;

    public HelpCommand(CommandManager manager) {
        super("help", "");
        this.manager = manager;
    }

    @Override
    public void execute(ModelsPlugin pl, CommandSender sender, String[] args) throws CommandException {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player)sender;
        Player p = pl.worldedit.wrapPlayer(player);

        player.sendMessage(MessageManager.m(Message.HELP));
        for(Command c : manager.getCommands()) {
            HoverEvent e = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new Text(net.md_5.bungee.api.ChatColor.GREEN + c.getDescription())));
            player.spigot().sendMessage(new ComponentBuilder("  -  /model ").append(c.getName()).color(net.md_5.bungee.api.ChatColor.GOLD).append(" : ").append(c.getUsage()).italic(true).color(net.md_5.bungee.api.ChatColor.YELLOW).event(e).create());
        }
    }

    @Override
    public String getDescription() {
        return MessageManager.m(Message.CMD_LIST);
    }
}

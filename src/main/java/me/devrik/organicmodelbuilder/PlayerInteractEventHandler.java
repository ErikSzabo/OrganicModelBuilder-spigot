package me.devrik.organicmodelbuilder;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.model.Model;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractEventHandler implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        WorldEditPlugin we = ModelsPlugin.getWE();
        StateManager stateManager = ModelsPlugin.getStateManager();
        Player player = we.wrapPlayer(e.getPlayer());
        if (stateManager.hasPlayerSession(player)) {
            Model model = stateManager.getSession(player);
            if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(model.undo(we.wrapPlayer(e.getPlayer()))) {
                        e.getPlayer().sendMessage(MessageManager.m(Message.UNDOED) + ChatColor.BOLD + ChatColor.WHITE + model.getOrder().get(model.getCurrentIndex()));
                    } else {
                        e.getPlayer().sendMessage(MessageManager.m(Message.CANCEL_SUCCESS));
                        stateManager.unRegisterPlayerSession(player);
                    }

                }
            } else {
                try {
                    model.next(we.wrapPlayer(e.getPlayer()));
                } catch (CommandException err) {
                    e.getPlayer().sendMessage(err.getMessage());
                }
            }
        }
    }
}

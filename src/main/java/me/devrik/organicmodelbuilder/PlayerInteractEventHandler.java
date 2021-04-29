package me.devrik.organicmodelbuilder;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import me.devrik.organicmodelbuilder.model.ActivePart;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.NextResult;
import org.bukkit.ChatColor;
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
        if(!stateManager.hasPlayerSession(player)) return;

        Model model = stateManager.getSession(player);
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(model.undo(we.wrapPlayer(e.getPlayer()))) {
                    e.getPlayer().sendMessage(MessageManager.m(Message.UNDOED) + ChatColor.BOLD + ChatColor.WHITE + model.getCurrentPart().getName());
                    return;
                }
                e.getPlayer().sendMessage(MessageManager.m(Message.CANCEL_SUCCESS));
                stateManager.unRegisterPlayerSession(player);
            }
            return;
        }

        NextResult result = model.next(we.wrapPlayer(e.getPlayer()));
        switch (result) {
            case ALL_PLACED:
                e.getPlayer().sendMessage(MessageManager.m(Message.NO_MORE_TO_PASTE1));
                e.getPlayer().sendMessage(MessageManager.m(Message.NO_MORE_TO_PASTE2));
                break;
            case PART_PLACED:
                ActivePart beforePart = model.getBeforePart();
                e.getPlayer().sendMessage(MessageManager.m(Message.PART_PLACED));
                e.getPlayer().sendMessage(String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", Math.toDegrees(beforePart.getYaw()), Math.toDegrees(beforePart.getPitch()), Math.toDegrees(beforePart.getRoll())));
                e.getPlayer().sendMessage(MessageManager.m(Message.PLACE_NEXT_PART) + ChatColor.BOLD + ChatColor.WHITE + "(" + model.getCurrentPart().getName() + ")" + ChatColor.RESET + ChatColor.YELLOW + " " + MessageManager.m(Message.WITH_LEFT_CLICK));
                e.getPlayer().sendMessage(MessageManager.m(Message.OR_CANCEL));
                e.getPlayer().sendMessage(MessageManager.m(Message.OR_MODIFY));
                e.getPlayer().sendMessage(ChatColor.WHITE + "" + ChatColor.ITALIC + " /model adjust " + beforePart.getName() + " <yaw> <pitch> <roll>." + ChatColor.RESET + ChatColor.YELLOW + " " + MessageManager.m(Message.AND_LATER));
                break;
            case FINISHED:
                e.getPlayer().sendMessage(MessageManager.m(Message.PASTE_ALL1));
                e.getPlayer().sendMessage(MessageManager.m(Message.PASTE_ALL2));
                e.getPlayer().sendMessage(MessageManager.m(Message.PASTE_ALL3));
                e.getPlayer().sendMessage(MessageManager.m(Message.PASTE_ALL4));
                for (ActivePart part : model.getPartsInOrder()) {
                    e.getPlayer().sendMessage(ChatColor.BLUE + part.getName() + ChatColor.RESET + " : " + String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", Math.toDegrees(part.getYaw()), Math.toDegrees(part.getPitch()), Math.toDegrees(part.getRoll())));
                }
                break;
            default:
                e.getPlayer().sendMessage(ChatColor.RED + "Operation failed, please try again, or see console for errors.");
                break;
        }
    }
}

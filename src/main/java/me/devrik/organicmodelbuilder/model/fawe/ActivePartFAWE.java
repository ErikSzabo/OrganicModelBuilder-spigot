package me.devrik.organicmodelbuilder.model.fawe;

import com.boydti.fawe.object.exception.FaweException;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.model.ActivePart;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.ModelPart;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;


public class ActivePartFAWE extends ActivePart {
    public ActivePartFAWE(Model model, ModelPart modelPart) {
        super(model, modelPart);
    }

    public boolean paste(Player p, boolean modify) {
        try(EditSession session =  new EditSessionBuilder(model.getWorld()).player(p).build()) {
            return pasteHelper(session, p, modify);
        }
    }

    @Override
    public boolean undo(Player p, EditSession e) {
        try {
            return super.undo(p, e);
        } catch (FaweException exception) {
            throw new CommandException(ChatColor.RED + exception.getLocalizedMessage());
        }
    }
}

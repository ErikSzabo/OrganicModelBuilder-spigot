package me.devrik.organicmodelbuilder.model.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import me.devrik.organicmodelbuilder.model.ActivePart;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.ModelPart;


public class ActivePartWE extends ActivePart {

    public ActivePartWE(Model model, ModelPart modelPart) {
        super(model, modelPart);
    }

    public boolean paste(Player p, boolean modify) {
        try(EditSession session =  WorldEdit.getInstance().newEditSessionBuilder().world(model.getWorld()).actor(p).build()) {
            return pasteHelper(session, p, modify);
        }
    }
}

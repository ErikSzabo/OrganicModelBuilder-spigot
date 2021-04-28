package me.devrik.organicmodelbuilder.model.worldedit;

import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.World;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.ModelPart;

public class ModelWE extends Model {
    public ModelWE(ModelPart mainPart, double scale, Pattern p, World world) {
        super(mainPart, scale, p, world);
    }
}

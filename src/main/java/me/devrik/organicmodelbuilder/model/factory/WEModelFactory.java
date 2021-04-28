package me.devrik.organicmodelbuilder.model.factory;

import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.World;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import me.devrik.organicmodelbuilder.model.ActivePart;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.ModelPart;
import me.devrik.organicmodelbuilder.model.worldedit.ActivePartWE;
import me.devrik.organicmodelbuilder.model.worldedit.ModelPartWE;
import me.devrik.organicmodelbuilder.model.worldedit.ModelWE;

import java.io.File;

public class WEModelFactory implements ModelFactory {

    @Override
    public Model getModel(ModelPart mainPart, double scale, Pattern p, World world) {
        return new ModelWE(mainPart, scale, p, world);
    }

    @Override
    public ModelPart getModelPart(String modelName, String name, ModelPart[] children, boolean flip) {
        return new ModelPartWE(modelName, name, children, flip);
    }

    @Override
    public ModelPart getModelPart(String modelName, String name, ModelPart[] children, boolean flip, File file) {
        return new ModelPartWE(modelName, name, children, flip, file);
    }

    @Override
    public ActivePart getActivePart(Model model, ModelPart modelPart) {
        return new ActivePartWE(model, modelPart);
    }
}

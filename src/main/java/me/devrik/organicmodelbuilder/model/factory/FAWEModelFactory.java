package me.devrik.organicmodelbuilder.model.factory;

import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.World;
import me.devrik.organicmodelbuilder.model.ActivePart;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.ModelPart;
import me.devrik.organicmodelbuilder.model.fawe.ActivePartFAWE;
import me.devrik.organicmodelbuilder.model.fawe.ModelFAWE;
import me.devrik.organicmodelbuilder.model.fawe.ModelPartFAWE;

import java.io.File;

public class FAWEModelFactory implements ModelFactory {
    @Override
    public Model getModel(ModelPart mainPart, double scale, Pattern p, World world) {
        return new ModelFAWE(mainPart, scale, p, world);
    }

    @Override
    public ModelPart getModelPart(String modelName, String name, ModelPart[] children, boolean flip) {
        return new ModelPartFAWE(modelName, name, children, flip);
    }

    @Override
    public ModelPart getModelPart(String modelName, String name, ModelPart[] children, boolean flip, File file) {
        return new ModelPartFAWE(modelName, name, children, flip, file);
    }

    @Override
    public ActivePart getActivePart(Model model, ModelPart modelPart) {
        return new ActivePartFAWE(model, modelPart);
    }
}

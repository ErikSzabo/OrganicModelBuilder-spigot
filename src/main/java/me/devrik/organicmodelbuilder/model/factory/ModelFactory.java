package me.devrik.organicmodelbuilder.model.factory;

import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.World;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import me.devrik.organicmodelbuilder.model.ActivePart;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.ModelPart;

import java.io.File;

public interface ModelFactory {
    Model getModel(ModelPart mainPart, double scale, Pattern p, World world);
    ModelPart getModelPart(String modelName, String name, ModelPart[] children, boolean flip);
    ModelPart getModelPart(String modelName, String name, ModelPart[] children, boolean flip, File file);
    ActivePart getActivePart(Model model, ModelPart modelPart);
}

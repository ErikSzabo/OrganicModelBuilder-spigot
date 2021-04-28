package me.devrik.organicmodelbuilder.model.fawe;

import me.devrik.organicmodelbuilder.ModelsPlugin;
import me.devrik.organicmodelbuilder.model.ModelPart;

import java.io.File;

public class ModelPartFAWE extends ModelPart {
    public ModelPartFAWE(String modelName, String name, ModelPart[] children, boolean flip) {
        this(modelName, name, children, flip, new File(ModelsPlugin.getInstance().getDataFolder(), "models/" + modelName + "/" + name + ".schematic"));
    }

    public ModelPartFAWE(String modelName, String name, ModelPart[] children, boolean flip, File file) {
        super(modelName, name, children, flip, file);
    }
}

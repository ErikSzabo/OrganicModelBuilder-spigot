package me.devrik.organicmodelbuilder.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.devrik.organicmodelbuilder.model.ModelPart;
import me.devrik.organicmodelbuilder.ModelsPlugin;

import java.io.IOException;

public class ModelLoader {

    public static ModelPart loadModel(String modelName, JsonObject obj) throws IOException, IllegalAccessException, InstantiationException {
        String name = obj.get("name").getAsString();
        boolean flip = obj.get("flip").getAsBoolean();
        ModelPart[] children = loadChildren(modelName, obj.get("children").getAsJsonArray());
        return ModelsPlugin.getModelFactory().getModelPart(modelName, name, children, flip);
    }


    private static ModelPart[] loadChildren(String modelName, JsonArray children) throws IOException, IllegalAccessException, InstantiationException {
        ModelPart[] parts = new ModelPart[children.size()];

        for(int i = 0; i < children.size(); ++i) {
            parts[i] = loadModel(modelName, children.get(i).getAsJsonObject());
        }

        return parts;
    }
}

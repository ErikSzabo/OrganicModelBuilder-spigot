package me.devrik.organicmodelbuilder.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.devrik.organicmodelbuilder.ModelPart;
import me.devrik.organicmodelbuilder.ModelsPlugin;

import java.io.IOException;

public class ModelLoader {

    public static ModelPart loadModel(ModelsPlugin pl, String modelName, JsonObject obj) throws IOException, IllegalAccessException, InstantiationException {
        String name = obj.get("Name").getAsString();
        boolean flip = obj.get("flip").getAsBoolean();
        ModelPart[] children = loadChildren(pl, modelName, obj.get("Childs").getAsJsonArray());
        return new ModelPart(pl, modelName, name, children, flip);
    }


    private static ModelPart[] loadChildren(ModelsPlugin pl, String modelName, JsonArray children) throws IOException, IllegalAccessException, InstantiationException {
        ModelPart[] parts = new ModelPart[children.size()];

        for(int i = 0; i < children.size(); ++i) {
            parts[i] = loadModel(pl, modelName, children.get(i).getAsJsonObject());
        }

        return parts;
    }
}

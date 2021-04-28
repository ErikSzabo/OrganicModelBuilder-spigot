package me.devrik.organicmodelbuilder;

import com.sk89q.worldedit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class StateManager {
    private final HashMap<String, ModelPart> partRegistry = new HashMap<>();
    private final HashMap<UUID, Model> currentSessions = new HashMap<>();
    private boolean init = false;

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isPartExist(String name) {
        return partRegistry.containsKey(name);
    }

    public ModelPart getModelPart(String name) {
        return partRegistry.get(name);
    }

    public void registerModelPart(ModelPart part) {
        partRegistry.put(part.getModelName(), part);
    }

    public boolean hasPlayerSession(Player player) {
        return currentSessions.containsKey(player.getUniqueId());
    }

    public Model getSession(Player player) {
        return currentSessions.get(player.getUniqueId());
    }

    public void registerPlayerSession(Player player, Model model) {
        currentSessions.put(player.getUniqueId(), model);
    }

    public void unRegisterPlayerSession(Player player) {
        currentSessions.remove(player.getUniqueId());
    }

    public Collection<String> getModelList() {
        return partRegistry.keySet();
    }
}

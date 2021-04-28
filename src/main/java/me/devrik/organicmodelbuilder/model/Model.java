package me.devrik.organicmodelbuilder.model;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import me.devrik.organicmodelbuilder.ModelsPlugin;
import me.devrik.organicmodelbuilder.message.Message;
import me.devrik.organicmodelbuilder.message.MessageManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a model that is currently being created by a player.
 */
public abstract class Model {
    /**
     * Name of the model
     */
    protected final String name;
    /**
     * Active model parts in the model
     */
    protected final HashMap<String, ActivePart> parts = new HashMap<>();
    /**
     * Active model parts names in order
     */
    protected final List<String> order = new ArrayList<>();
    /**
     * Scale of the model (max 2)
     */
    protected final double scale;
    /**
     * WorldEdit pattern
     */
    protected final Pattern pattern;
    /**
     * World where the model is being created
     */
    protected final World world;
    /**
     * Current rotation value
     */
    protected double currentRoll = 0.0D;
    /**
     * Index of the current part that will be placed.
     * Can be used to index the order list.
     */
    protected int currentIndex;

    public Model(ModelPart mainPart, double scale, Pattern p, World world) {
        this.name = mainPart.getModelName();
        this.scale = scale;
        this.pattern = p;
        this.world = world;
        this.addPart(mainPart);
    }

    private void addPart(ModelPart part) {
        parts.put(part.getName(), ModelsPlugin.getModelFactory().getActivePart(this, part));
        order.add(part.getName());

        for (ModelPart child : part.getChildren()) {
            addPart(child);
        }
    }

    protected float moduloDegree(float degree) {
        degree %= 360.0F;
        if (degree < 0.0F) {
            degree += 360.0F;
        }

        return degree;
    }

    // TODO: What the fuck is this
    public void next(Player p) {
        if (currentIndex >= order.size()) {
            p.print(MessageManager.m(Message.NO_MORE_TO_PASTE1));
            p.print(MessageManager.m(Message.NO_MORE_TO_PASTE2));
        } else {
            double yaw = Math.toRadians(moduloDegree(p.getLocation().getYaw()));
            double pitch = Math.toRadians(moduloDegree(p.getLocation().getPitch()));
            double roll = Math.toRadians(moduloDegree((float)currentRoll));
            currentRoll = 0.0D;
            ActivePart part = parts.get(order.get(currentIndex));
            if (currentIndex == 0) {
                part.setPosition(BlockVector3.at(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
                p.setPosition(p.getBlockOn().toVector().add(0.0D, part.getMaxRadius(scale), 0.0D), p.getLocation().getPitch(), p.getLocation().getYaw());
            }

            part.setYaw(yaw);
            part.setPitch(pitch);
            part.setRoll(roll);
            if (part.paste(p, false)) {
                part.updatePos(part.getPosition());
                ++currentIndex;
                if (currentIndex < order.size()) {
                    p.print(MessageManager.m(Message.PART_PLACED));
                    p.print(String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", Math.toDegrees(part.getYaw()), Math.toDegrees(part.getPitch()), Math.toDegrees(part.getRoll())));
                    p.print(MessageManager.m(Message.PLACE_NEXT_PART) + ChatColor.BOLD + ChatColor.WHITE + "(" + order.get(currentIndex) + ")" + ChatColor.RESET + ChatColor.YELLOW + " " + MessageManager.m(Message.WITH_LEFT_CLICK));
                    p.print(MessageManager.m(Message.OR_CANCEL));
                    p.print(MessageManager.m(Message.OR_MODIFY));
                    p.print("" + ChatColor.WHITE + ChatColor.ITALIC + " /model adjust " + order.get(currentIndex - 1) + " <yaw> <pitch> <roll>." + ChatColor.RESET + ChatColor.YELLOW + " " + MessageManager.m(Message.AND_LATER));
                }

                if (currentIndex >= order.size()) {
                    p.print(MessageManager.m(Message.PASTE_ALL1));
                    p.print(MessageManager.m(Message.PASTE_ALL2));
                    p.print(MessageManager.m(Message.PASTE_ALL3));
                    p.print(MessageManager.m(Message.PASTE_ALL4));
                    p.print("");

                    for (String name : order) {
                        ActivePart part2 = parts.get(name);
                        p.print(ChatColor.BLUE + name + ChatColor.RESET + " : " + String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", Math.toDegrees(part2.getYaw()), Math.toDegrees(part2.getPitch()), Math.toDegrees(part2.getRoll())));
                    }
                }

            }
        }
    }

    /**
     * Returns true if undo was successful.
     * If nothing to undo, returns false.
     *
     * @param player Player who is performing the undo operation on the model
     * @return true if successful, false if nothing to undo
     */
    public boolean undo(Player player) {
        if (currentIndex == 0) return false;
        boolean success = parts.get(order.get(currentIndex - 1)).undo(player, null);
        if(!success) return false;
        currentIndex--;
        return true;
    }

    /**
     * Modifies a part based on the new values. Returns false if the operation fails.
     *
     * @param p     player who executes the modify
     * @param name  name of the part
     * @param yaw   new yaw of the part
     * @param pitch new pitch of the part
     * @param roll  new rotation of the part
     * @return      true if update was successful, false if it wasn't
     * @throws CommandException When not all parts are placed, or when the selected part not found
     */
    public boolean modify(Player p, String name, double yaw, double pitch, double roll) throws CommandException {
        if (currentIndex < order.size()) {
            throw new CommandException(MessageManager.m(Message.NOT_ALL_PLACED));
        }

        ActivePart part = parts.get(name);
        if (part == null) {
            throw new CommandException(MessageManager.m(Message.PART_NOT_FOUND));
        }

        return part.update(p, Math.toRadians(yaw), Math.toRadians(pitch), Math.toRadians(roll));
    }

    /**
     * Saves the model creation process to WorldEdit history.
     *
     * @param plugin plugin instance
     * @param player player who created the model
     */
    public void finalise(ModelsPlugin plugin, Player player) {
        for (String s : order) {
            WorldEdit.getInstance().getSessionManager().get(player).remember(parts.get(s).getChanges());
        }
    }

    /**
     * Determines if the model is completed or not.
     *
     * @return true if the model is completed
     */
    public boolean isAtTheEnd() {
        return currentIndex >= order.size();
    }

    /**
     * Fully stops the model creation process.
     *
     * @param player player who is editing the model
     */
    public void cancel(Player player) {
        for(int i = 0; i < currentIndex; ++i) {
            WorldEdit.getInstance().getSessionManager().get(player).remember((parts.get(order.get(i))).getChanges());
        }
    }

    public String getName() {
        return name;
    }

    public HashMap<String, ActivePart> getParts() {
        return parts;
    }

    public List<String> getOrder() {
        return order;
    }

    public double getScale() {
        return scale;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public World getWorld() {
        return world;
    }

    public double getCurrentRoll() {
        return currentRoll;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentRoll(double currentRoll) {
        this.currentRoll = currentRoll;
    }
}

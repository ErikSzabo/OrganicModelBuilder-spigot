package me.devrik.organicmodelbuilder;

import com.boydti.fawe.object.exception.FaweException;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;

public class Model {
    public static HashMap<Integer, Integer> vectors = new HashMap<>();
    private String name;
    private final HashMap<String, Model.Part> parts = new HashMap<>();
    private final List<String> order = new ArrayList<>();
    public double scale;
    private final Pattern pattern;
    private final World world;
    public double currentRoll = 0.0D;
    private int currentIndex;

    public Model(ModelPart mainPart, double scale, Pattern p, World world) {
        this.name = mainPart.modelName;
        this.scale = scale;
        this.pattern = p;
        this.world = world;
        this.addPart(mainPart);
        vectors.clear();
    }

    private void addPart(ModelPart part) {
        this.parts.put(part.partName, new Model.Part(part));
        this.order.add(part.partName);

        for (ModelPart child : part.getChildren()) {
            this.addPart(child);
        }
    }

    public void next(Player p) {
        if (this.currentIndex >= this.order.size()) {
            p.print(ChatColor.RED + "There isn't any part left to paste.");
            p.print(ChatColor.YELLOW + "You can either cancel your last part, adjust any of them with" + ChatColor.WHITE + ChatColor.ITALIC + " /model adjust" + ChatColor.RESET + ChatColor.YELLOW + " or validate the model as it is with " + ChatColor.WHITE + ChatColor.ITALIC + " /model end.");
        } else {
            double yaw = Math.toRadians(this.modulosDegree(p.getLocation().getYaw()));
            double pitch = Math.toRadians(this.modulosDegree(p.getLocation().getPitch()));
            double roll = Math.toRadians(this.modulosDegree((float)this.currentRoll));
            this.currentRoll = 0.0D;
            Model.Part part = this.parts.get(this.order.get(this.currentIndex));
            if (this.currentIndex == 0) {
                part.pos = BlockVector3.at(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
                p.setPosition(p.getBlockOn().toVector().add(0.0D, part.getMaxRadius(this.scale), 0.0D), p.getLocation().getPitch(), p.getLocation().getYaw());
            }

            part.yaw = yaw;
            part.pitch = pitch;
            part.roll = roll;
            if (part.paste(p, false)) {
                part.updatePos(part.pos);
                ++this.currentIndex;
                if (this.currentIndex < this.order.size()) {
                    ModelsCommands.sendMessages(p, new String[]{ChatColor.GREEN + "The part was successfully placed, the values are :", String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", Math.toDegrees(part.yaw), Math.toDegrees(part.pitch), Math.toDegrees(part.roll)), "", ChatColor.YELLOW + "You can now place the next part " + ChatColor.BOLD + ChatColor.WHITE + "(" + (String)this.order.get(this.currentIndex) + ")" + ChatColor.RESET + ChatColor.YELLOW + " with left click", ChatColor.YELLOW + "or you can cancel it with right click. (You need to have an item in your hand)", ChatColor.YELLOW + "You can also modify the values of the current part with", "" + ChatColor.WHITE + ChatColor.ITALIC + " /model adjust " + (String)this.order.get(this.currentIndex - 1) + " <yaw> <pitch> <roll>." + ChatColor.RESET + ChatColor.YELLOW + "You will be able to do it later too."});
                }

                if (this.currentIndex >= this.order.size()) {
                    p.print(ChatColor.GREEN + "You have successfully placed all the part.");
                    p.print(ChatColor.YELLOW + "You can now validate the model as it is with " + ChatColor.WHITE + ChatColor.ITALIC + " /model end");
                    p.print(ChatColor.YELLOW + "Or you can adjust the parts values with " + ChatColor.WHITE + ChatColor.ITALIC + " /model adjust <part> <yaw> <pitch> <roll>");
                    p.print(ChatColor.YELLOW + "These are the current values :");
                    p.print("");

                    for (String name : this.order) {
                        Part part2 = this.parts.get(name);
                        p.print(ChatColor.BLUE + name + ChatColor.RESET + " : " + String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", Math.toDegrees(part2.yaw), Math.toDegrees(part2.pitch), Math.toDegrees(part2.roll)));
                    }
                }

            }
        }
    }

    private float modulosDegree(float degree) {
        degree %= 360.0F;
        if (degree < 0.0F) {
            degree += 360.0F;
        }

        return degree;
    }

    public boolean cancel(Player player) {
        if (this.currentIndex == 0) {
            return true;
        } else {
            --this.currentIndex;
            if (!(this.parts.get(this.order.get(this.currentIndex))).cancel(player, null)) {
                ++this.currentIndex;
            } else {
                player.print(ChatColor.GREEN + "Last part cancelled, you will now place : " + ChatColor.BOLD + ChatColor.WHITE + this.order.get(this.currentIndex));
            }
            return false;
        }
    }

    public boolean modify(Player p, String name, double yaw, double pitch, double roll) throws CommandException {
        if (this.currentIndex < this.order.size()) {
            throw new CommandException(ChatColor.RED + "You can't use that command now, you haven't placed all the parts.");
        } else {
            Model.Part part = this.parts.get(name);
            if (part == null) {
                throw new CommandException(ChatColor.RED + "The part " + name + " cannot be found.");
            } else {
                double lastYaw = part.yaw;
                double lastPitch = part.pitch;
                double lastRoll = part.roll;
                part.yaw = Math.toRadians(yaw);
                part.pitch = Math.toRadians(pitch);
                part.roll = Math.toRadians(roll);
                if (!part.update(p)) {
                    part.yaw = lastYaw;
                    part.pitch = lastPitch;
                    part.roll = lastRoll;
                    part.updatePos(part.pos);
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public void finalise(ModelsPlugin plugin, Player player) {
        player.print(ChatColor.AQUA + "Adding to history...");

        for (String s : this.order) {
            WorldEdit.getInstance().getSessionManager().get(player).remember(this.parts.get(s).changes);
        }

        plugin.currents.remove(player.getUniqueId());
        player.print(ChatColor.GREEN + "The validation process is a success.");
    }

    public boolean isAtTheEnd() {
        return this.currentIndex >= this.order.size();
    }

    public void stop(ModelsPlugin plugin, Player player) {
        player.print(ChatColor.AQUA + "Merging parts...");

        for(int i = 0; i < this.currentIndex; ++i) {
            WorldEdit.getInstance().getSessionManager().get(player).remember(((Model.Part)this.parts.get(this.order.get(i))).changes);
        }

        plugin.currents.remove(player.getUniqueId());
        player.print(ChatColor.GREEN + "The abort process is a  success");
    }

    public class Part {
        private final ModelPart modelPart;
        private boolean paste = false;
        double yaw;
        double pitch;
        double roll;
        EditSession changes;
        BlockVector3 pos;

        public Part(ModelPart from) {
            this.modelPart = from;
        }

        public boolean paste(Player p, boolean modify) {
            LocalSession session = WorldEdit.getInstance().getSessionManager().get(p);
            this.changes = WorldEdit.getInstance().getEditSessionFactory().getEditSession(Model.this.world, session.getBlockChangeLimit(), session.getBlockBag(p), p);

            try {
                this.modelPart.paste(p, this.pos, this.yaw, this.pitch, this.roll, Model.this.scale, Model.this.pattern, this.changes);
                if (modify) {
                    BlockVector3[] poses = this.modelPart.getOffsets();
                    ModelPart[] childs = this.modelPart.getChildren();

                    for(int i = 0; i < poses.length; ++i) {
                        String childName = childs[i].partName;
                        Model.Part child = Model.this.parts.get(childName);
                        child.paste(p, true);
                    }
                }

                this.paste = true;
                return true;
            } catch (FaweException e) {
                p.print(ChatColor.RED + e.getLocalizedMessage());
                return false;
            }
        }

        public boolean cancel(Player p, EditSession e) {
            try {
                boolean justCancel = e == null;
                BlockVector3[] poses = this.modelPart.getOffsets();
                ModelPart[] children = this.modelPart.getChildren();

                for(int i = 0; i < poses.length; ++i) {
                    String childName = children[poses.length - i - 1].partName;
                    Model.Part child = Model.this.parts.get(childName);
                    child.cancel(p, e);
                }

                if (!this.paste) {
                    return true;
                } else {
                    if (justCancel) {
                        e = WorldEdit.getInstance().getEditSessionFactory().getEditSession(Model.this.world, -1);
                    }

                    this.changes.undo(e);
                    if (justCancel) {
                        WorldEdit.getInstance().flushBlockBag(p, e);
                        e.close();
                    }

                    this.changes = null;
                    this.paste = false;
                    return true;
                }
            } catch (FaweException exception) {
                p.print(ChatColor.RED + exception.getLocalizedMessage());
                return false;
            }
        }

        public void updatePos(BlockVector3 pos) {
            this.pos = pos;
            BlockVector3[] poses = this.modelPart.getOffsets();
            ModelPart[] children = this.modelPart.getChildren();

            for(int i = 0; i < poses.length; ++i) {
                String childName = children[i].partName;
                Model.Part child = Model.this.parts.get(childName);
                child.pos = this.pos.add(Transformer.apply(poses[i], this.yaw, this.pitch, this.roll, this.modelPart.flip, Model.this.scale));
                child.updatePos(this.pos.add(Transformer.apply(poses[i], this.yaw, this.pitch, this.roll, this.modelPart.flip, Model.this.scale)));
            }

        }

        public boolean update(Player p) {
            this.updatePos(this.pos);
            return this.cancel(p, null) && this.paste(p, true);
        }

        public double getMaxRadius(double scale) {
            return this.modelPart.getMaxRadius(scale);
        }
    }
}

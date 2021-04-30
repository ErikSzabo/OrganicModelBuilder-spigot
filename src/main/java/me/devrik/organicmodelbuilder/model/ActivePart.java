package me.devrik.organicmodelbuilder.model;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import me.devrik.organicmodelbuilder.Transformer;


public abstract class ActivePart {
    protected final Model model;
    protected final ModelPart modelPart;
    protected boolean paste = false;
    protected double yaw;
    protected double pitch;
    protected double roll;
    protected EditSession changes;
    protected BlockVector3 position;

    public ActivePart(Model model, ModelPart modelPart) {
        this.model = model;
        this.modelPart = modelPart;
    }

    public abstract boolean paste(Player p, boolean modify);

    public boolean undo(Player p, EditSession e) {
        boolean justCancel = e == null;
        BlockVector3[] poses = this.modelPart.getOffsets();
        ModelPart[] children = this.modelPart.getChildren();

        for(int i = 0; i < poses.length; ++i) {
            String childName = children[poses.length - i - 1].getName();
            ActivePart child = model.getParts().get(childName);
            child.undo(p, e);
        }

        if (this.paste) {
            if (justCancel) {
                e = WorldEdit.getInstance().getEditSessionFactory().getEditSession(model.getWorld(), -1);
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
        return false;
    }

    public void updatePos(BlockVector3 pos) {
        this.position = pos;
        BlockVector3[] poses = this.modelPart.getOffsets();
        ModelPart[] children = this.modelPart.getChildren();

        for(int i = 0; i < poses.length; ++i) {
            String childName = children[i].getName();
            ActivePart child = model.getParts().get(childName);
            child.position = this.position.add(Transformer.apply(poses[i], this.yaw, this.pitch, this.roll, modelPart.isFlip(), model.getScale()));
            child.updatePos(this.position.add(Transformer.apply(poses[i], this.yaw, this.pitch, this.roll, modelPart.isFlip(), model.getScale())));
        }

    }

    public boolean update(Player p, double yaw, double pitch, double roll) {
        double lastYaw = this.yaw;
        double lastPitch = this.pitch;
        double lastRoll = this.roll;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        updatePos(position);
        boolean success = this.undo(p, null) && this.paste(p, true);
        if(!success) {
            this.yaw = lastYaw;
            this.pitch = lastPitch;
            this.roll = lastRoll;
            updatePos(position);
        }
        return success;
    }

    public void updateYawPitchRoll(double yaw, double pitch, double roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public String getName() {
        return modelPart.getName();
    }

    public int getMaxRadius(double scale) {
        return this.modelPart.getMaxRadius(scale);
    }

    public EditSession getChanges() {
        return changes;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public BlockVector3 getPosition() {
        return position;
    }

    public void setPosition(BlockVector3 position) {
        this.position = position;
    }
}

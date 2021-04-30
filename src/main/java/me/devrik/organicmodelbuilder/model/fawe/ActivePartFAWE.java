package me.devrik.organicmodelbuilder.model.fawe;

import com.boydti.fawe.object.exception.FaweException;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.devrik.organicmodelbuilder.Transformer;
import me.devrik.organicmodelbuilder.model.ActivePart;
import me.devrik.organicmodelbuilder.model.Model;
import me.devrik.organicmodelbuilder.model.ModelPart;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;


public class ActivePartFAWE extends ActivePart {
    public ActivePartFAWE(Model model, ModelPart modelPart) {
        super(model, modelPart);
    }

    public boolean paste(Player p, boolean modify) {
        try(EditSession session =  new EditSessionBuilder(model.getWorld()).player(p).build()) {
            return miniPaste(session, p, modify);
        }
    }

    private boolean miniPaste(EditSession e, Player p, boolean modify) {
        this.changes = e;
        int maxR = getMaxRadius(model.getScale());
        BlockVector3 zero = position;
        Region region = new CuboidRegion(p.getWorld(), zero.add(-maxR, -maxR, -maxR), zero.add(maxR, maxR, maxR));
        Clipboard schematic = modelPart.getSchematic();
        for(BlockVector3 position : region) {
            double sx = position.getX() - zero.getX();
            double sy = position.getY() - zero.getY();
            double sz = position.getZ() - zero.getZ();
            BlockVector3 input = BlockVector3.at(sx, sy, sz);
            BlockVector3 out = Transformer.inverse(input, yaw, pitch, roll, modelPart.isFlip(), model.getScale());
            BaseBlock material = schematic.getFullBlock(out.add(schematic.getOrigin()).round());
            if (!material.getBlockType().equals(BlockTypes.AIR) && !material.getBlockType().equals(BlockTypes.OAK_SIGN) && !material.getBlockType().equals(BlockTypes.OAK_WALL_SIGN)) {
                try {
                    if (model.getPattern() == null) {
                        e.setBlock(position, material, EditSession.Stage.BEFORE_HISTORY);
                    } else {
                        e.setBlock(position, model.getPattern().apply(position), EditSession.Stage.BEFORE_HISTORY);
                    }
                } catch (WorldEditException err) {
                    throw new CommandException(ChatColor.RED + err.getLocalizedMessage());
                }
            }
        }

        if (modify) {
            BlockVector3[] poses = this.modelPart.getOffsets();
            ModelPart[] children = this.modelPart.getChildren();

            for(int i = 0; i < poses.length; ++i) {
                String childName = children[i].getName();
                ActivePart child = model.getParts().get(childName);
                child.paste(p, true);
            }
        }

        this.paste = true;
        return true;
    }

    @Override
    public boolean undo(Player p, EditSession e) {
        try {
            return super.undo(p, e);
        } catch (FaweException exception) {
            throw new CommandException(ChatColor.RED + exception.getLocalizedMessage());
        }
    }
}

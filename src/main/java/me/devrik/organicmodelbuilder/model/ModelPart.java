package me.devrik.organicmodelbuilder.model;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.devrik.organicmodelbuilder.ModelsPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ModelPart {
    protected final String name;
    protected final String modelName;
    protected final Clipboard schematic;
    protected final ModelPart[] children;
    protected final BlockVector3[] offsets;
    protected final boolean flip;
    protected int maxRadius;

    public ModelPart(String modelName, String name, ModelPart[] children, boolean flip) {
        this(modelName, name, children, flip, new File(ModelsPlugin.getInstance().getDataFolder(), "models/" + modelName + "/" + name + ".schematic"));
    }

    public ModelPart(String modelName, String name, ModelPart[] children, boolean flip, File file) {
        this.maxRadius = 0;
        this.name = name;
        this.modelName = modelName;
        this.children = children;
        this.flip = flip;
        this.schematic = this.loadSchematic(file);
        this.offsets = this.getChildPoints();
        if (this.offsets.length != this.children.length) {
            ModelsPlugin.logger().warning("The model " + modelName + " cannot be loaded properly because the part " + name + " does not have the same amount of child and location set to them.");
        }

        this.maxRadius = this.genMaxRadius();
    }

    protected Clipboard loadSchematic(File file) {
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new BufferedInputStream(new FileInputStream(file)))) {
                    return reader.read();
            }
        } catch (IOException e) {
            ModelsPlugin.logger().warning("Schematic load failed " + e.getMessage());
            return null;
        }
    }

    protected BlockVector3[] getChildPoints() {
        HashMap<Integer, BlockVector3> poses = new HashMap<>();
        BlockVector3 origin = this.schematic.getOrigin();

        for(BlockVector3 blockPos : schematic.getRegion()) {
            BaseBlock block = this.schematic.getFullBlock(blockPos);
            if (block.getBlockType().equals(BlockTypes.OAK_WALL_SIGN) || block.getBlockType().equals(BlockTypes.OAK_SIGN)) {
                for(int i = 1; i <= 4; ++i) {
                    String text = block.getNbtData().getString("Text" + i);

                    String type = null;
                    if (text.contains("Orga:")) {
                        type = "Orga:";
                    } else if (text.contains("Model:")) {
                        type = "Model:";
                    }

                    if (type != null) {
                        text = text.substring(text.indexOf(type) + type.length(), text.length() - 1);
                        int id = 0;

                        try {
                            id = Integer.parseInt(text);
                        } catch (NumberFormatException ne) {
                            text = text.substring(0, text.length() - ("\"}],\"text\":\"\"}".length() - 1));

                            try {
                                id = Integer.parseInt(text);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                        poses.put(id, BlockVector3.at(blockPos.getBlockX() - origin.getBlockX(), blockPos.getBlockY() - origin.getBlockY(), blockPos.getBlockZ() - origin.getBlockZ()));
                    }
                }
            }
        }

        BlockVector3[] ret = new BlockVector3[poses.size()];
        List<Integer> keys = new ArrayList<>(poses.keySet());
        keys.sort(Integer::compareTo);

        for(int z = 0; z < keys.size(); ++z) {
            ret[z] = poses.get(keys.get(z));
        }

        return ret;
    }

    protected int genMaxRadius() {
        BlockVector3 o = this.schematic.getOrigin();
        BlockVector3 min = this.schematic.getRegion().getMinimumPoint();
        BlockVector3 max = this.schematic.getRegion().getMaximumPoint();
        int minX = min.getBlockX() - o.getBlockX();
        int minY = min.getBlockY() - o.getBlockY();
        int minZ = min.getBlockZ() - o.getBlockZ();
        int maxX = max.getBlockX() - o.getBlockX();
        int maxY = max.getBlockY() - o.getBlockY();
        int maxZ = max.getBlockZ() - o.getBlockZ();
        int r = 0;
        r = getR(minX, minY, minZ, maxY, maxZ, r);
        r = getR(maxX, minY, minZ, maxY, maxZ, r);
        return (int)Math.sqrt(r) + 1;
    }

    private int getR(int minX, int minY, int minZ, int maxY, int maxZ, int r) {
        r = Math.max(r, minX * minX + minY * minY + minZ * minZ);
        r = Math.max(r, minX * minX + minY * minY + maxZ * maxZ);
        r = Math.max(r, minX * minX + maxY * maxY + minZ * minZ);
        r = Math.max(r, minX * minX + maxY * maxY + maxZ * maxZ);
        return r;
    }

    public int getMaxRadius(double scale) {
        return (int)((double)this.maxRadius * scale);
    }


    public String getName() {
        return name;
    }

    public String getModelName() {
        return modelName;
    }

    public ModelPart[] getChildren() {
        return children;
    }

    public BlockVector3[] getOffsets() {
        return offsets;
    }

    public boolean isFlip() {
        return flip;
    }

    public int getMaxRadius() {
        return maxRadius;
    }

    public Clipboard getSchematic() {
        return schematic;
    }
}

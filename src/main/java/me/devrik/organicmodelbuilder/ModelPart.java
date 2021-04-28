package me.devrik.organicmodelbuilder;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.ChatColor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelPart {
    private ModelsPlugin plugin;
    private String name;
    private String modelName;
    private Clipboard schematic;
    private ModelPart[] children;
    private BlockVector3[] offsets;
    private boolean flip;
    private int maxRadius;

    public ModelPart(ModelsPlugin plugin, String modelName, String name, ModelPart[] children, boolean flip) {
        this(plugin, modelName, name, children, flip, new File(plugin.getDataFolder(), "models/" + modelName + "/" + name + ".schematic"));
    }

    public ModelPart(ModelsPlugin plugin, String modelName, String name, ModelPart[] children, boolean flip, File file) {
        this.plugin = plugin;
        this.maxRadius = 0;
        this.name = name;
        this.modelName = modelName;
        this.children = children;
        this.flip = flip;
        this.schematic = this.loadSchematic(file);
        this.offsets = this.getChildPoints();
        if (this.offsets.length != this.children.length) {
            plugin.getLogger().warning("The model " + modelName + " cannot be loaded properly because the part " + name + " does not have the same amount of child and location set to them.");
        }

        this.maxRadius = this.genMaxRadius();
    }

    protected Clipboard loadSchematic(File file) {
        Clipboard clipboard;
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new BufferedInputStream(new FileInputStream(file)))) {
                try {
                    clipboard = reader.read();
                    return clipboard;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    this.plugin.getLogger().warning(".read() not working" + " " + file.getName() + " " + file.getAbsolutePath());
                }
            }
            return null;
        } catch (IOException e) {
            this.plugin.getLogger().warning("Schematic load failed");
            System.out.println(e.getMessage());
            return null;
        } catch (NullPointerException e) {
            this.plugin.getLogger().warning("WorldEdit is a piece of shit");
            this.plugin.getLogger().warning(e.getMessage());
            return null;
        }
    }

    protected BlockVector3[] getChildPoints() {
        HashMap<Integer, BlockVector3> poses = new HashMap<>();
        BlockVector3 o = this.schematic.getOrigin();

        int z;
        for(int x = this.schematic.getRegion().getMinimumPoint().getBlockX(); x <= this.schematic.getRegion().getMaximumPoint().getBlockX(); ++x) {
            for(int y = this.schematic.getRegion().getMinimumPoint().getBlockY(); y <= this.schematic.getRegion().getMaximumPoint().getBlockY(); ++y) {
                for(z = this.schematic.getRegion().getMinimumPoint().getBlockZ(); z <= this.schematic.getRegion().getMaximumPoint().getBlockZ(); ++z) {
                    try {
                        BaseBlock block = this.schematic.getFullBlock(BlockVector3.at(x, y, z));
                        if (block.getBlockType().equals(BlockTypes.OAK_WALL_SIGN) || block.getBlockType().equals(BlockTypes.OAK_SIGN)) {
                            for(int i = 1; i <= 4; ++i) {
                                String text = "";
                                try {
                                    text = block.getNbtData().getString("Text" + i);
                                } catch (NullPointerException e) {
                                    this.plugin.getLogger().warning("RIP");
                                }
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
                                    } catch (NumberFormatException var14) {
                                        text = text.substring(0, text.length() - ("\"}],\"text\":\"\"}".length() - 1));

                                        try {
                                            id = Integer.parseInt(text);
                                        } catch (NumberFormatException var13) {
                                            var13.printStackTrace();
                                        }
                                    }

                                    poses.put(id, BlockVector3.at(x - o.getBlockX(), y - o.getBlockY(), z - o.getBlockZ()));
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        BlockVector3[] ret = new BlockVector3[poses.size()];
        List<Integer> keys = new ArrayList<>(poses.keySet());
        keys.sort(Integer::compareTo);

        for(z = 0; z < keys.size(); ++z) {
            ret[z] = poses.get(keys.get(z));
        }

        return ret;
    }

    private int genMaxRadius() {
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
        r = Math.max(r, minX * minX + minY * minY + minZ * minZ);
        r = Math.max(r, minX * minX + minY * minY + maxZ * maxZ);
        r = Math.max(r, minX * minX + maxY * maxY + minZ * minZ);
        r = Math.max(r, minX * minX + maxY * maxY + maxZ * maxZ);
        r = Math.max(r, maxX * maxX + minY * minY + minZ * minZ);
        r = Math.max(r, maxX * maxX + minY * minY + maxZ * maxZ);
        r = Math.max(r, maxX * maxX + maxY * maxY + minZ * minZ);
        r = Math.max(r, maxX * maxX + maxY * maxY + maxZ * maxZ);
        return (int)Math.sqrt(r) + 1;
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

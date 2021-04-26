package me.devrik.organicmodelbuilder;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.EditSession.Stage;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.ChatColor;

public class ModelPart {
    public ModelsPlugin plugin;
    public String partName;
    public String modelName;
    public Clipboard schem;
    protected ModelPart[] children;
    protected BlockVector3[] offsets;
    public boolean flip;
    private int maxRadius;

    public ModelPart(ModelsPlugin plugin, String modelName, String partName, ModelPart[] children, boolean flip) {
        this(plugin, partName, modelName, children, flip, new File(plugin.getDataFolder(), "models/" + modelName + "/" + partName + ".schematic"));
    }

    public ModelPart(ModelsPlugin plugin, String partName, String modelName, ModelPart[] children, boolean flip, File file) {
        this.plugin = plugin;
        this.maxRadius = 0;
        this.partName = partName;
        this.modelName = modelName;
        this.children = children;
        this.flip = flip;
        this.schem = this.loadSchematic(file);
        this.offsets = this.getChildPoints();
        if (this.offsets.length != this.children.length) {
            plugin.getLogger().warning("The model " + modelName + " cannot be loaded properly because the part " + partName + " does not have the same amount of child and location set to them.");
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

    public ModelPart[] getChildren() {
        return this.children;
    }

    protected BlockVector3[] getChildPoints() {
        HashMap<Integer, BlockVector3> poses = new HashMap<>();
        BlockVector3 o = this.schem.getOrigin();

        int z;
        for(int x = this.schem.getRegion().getMinimumPoint().getBlockX(); x <= this.schem.getRegion().getMaximumPoint().getBlockX(); ++x) {
            for(int y = this.schem.getRegion().getMinimumPoint().getBlockY(); y <= this.schem.getRegion().getMaximumPoint().getBlockY(); ++y) {
                for(z = this.schem.getRegion().getMinimumPoint().getBlockZ(); z <= this.schem.getRegion().getMaximumPoint().getBlockZ(); ++z) {
                    try {
                        BaseBlock block = this.schem.getFullBlock(BlockVector3.at(x, y, z));
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
                    } catch (Exception var15) {
                        var15.printStackTrace();
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

    public BlockVector3[] getOffsets() {
        return this.offsets;
    }

    public EditSession paste(final Player p, final BlockVector3 zero, final double yaw, final double pitch, final double roll, final double scale, final Pattern pattern, final EditSession e) {
        int maxR = this.getMaxRadius(scale);
        Region region = new CuboidRegion(p.getWorld(), zero.add(-maxR, -maxR, -maxR), zero.add(maxR, maxR, maxR));
        RegionVisitor visitor = new RegionVisitor(region, position -> {
            double sx = position.getX() - zero.getX();
            double sy = position.getY() - zero.getY();
            double sz = position.getZ() - zero.getZ();
            BlockVector3 input = BlockVector3.at(sx, sy, sz);
            BlockVector3 out = Transformer.inverse(input, yaw, pitch, roll, flip, scale);
            BaseBlock material = schem.getFullBlock(out.add(schem.getOrigin()).round());
            if (!material.getBlockType().equals(BlockTypes.AIR) && !material.getBlockType().equals(BlockTypes.OAK_SIGN) && !material.getBlockType().equals(BlockTypes.OAK_WALL_SIGN)) {
                try {
                    return pattern == null ? e.setBlock(position, material, Stage.BEFORE_HISTORY) : e.setBlock(position, pattern.apply(position), Stage.BEFORE_HISTORY);
                } catch (WorldEditException var12) {
                    p.print(ChatColor.RED + var12.getLocalizedMessage());
                    throw var12;
                }
            } else {
                return false;
            }
        });
        Operations.completeBlindly(visitor);
        e.close();
        return e;
    }

    private int genMaxRadius() {
        BlockVector3 o = this.schem.getOrigin();
        BlockVector3 min = this.schem.getRegion().getMinimumPoint();
        BlockVector3 max = this.schem.getRegion().getMaximumPoint();
        int minX = min.getBlockX() - o.getBlockX();
        int minY = min.getBlockY() - o.getBlockY();
        int minZ = min.getBlockZ() - o.getBlockZ();
        int maxX = max.getBlockX() - o.getBlockX();
        int maxY = max.getBlockY() - o.getBlockY();
        int maxZ = max.getBlockZ() - o.getBlockZ();
        int r = 0;
        int r1 = minX * minX + minY * minY + minZ * minZ;
        r = Math.max(r, minX * minX + minY * minY + minZ * minZ);
        r = Math.max(r, minX * minX + minY * minY + maxZ * maxZ);
        r = Math.max(r, minX * minX + maxY * maxY + minZ * minZ);
        r = Math.max(r, minX * minX + maxY * maxY + maxZ * maxZ);
        r = Math.max(r, maxX * maxX + minY * minY + minZ * minZ);
        r = Math.max(r, maxX * maxX + minY * minY + maxZ * maxZ);
        r = Math.max(r, maxX * maxX + maxY * maxY + minZ * minZ);
        r = Math.max(r, maxX * maxX + maxY * maxY + maxZ * maxZ);
        return (int)Math.sqrt((double)r) + 1;
    }

    public int getMaxRadius(double scale) {
        return (int)((double)this.maxRadius * scale);
    }
}

package me.devrik.organicmodelbuilder;

import com.sk89q.worldedit.math.BlockVector3;

public class Transformer {
    public static BlockVector3 apply(BlockVector3 in, double yaw, double pitch, double roll, boolean flip, double scale) {
        double x = in.getX();
        double y = in.getY();
        double z = in.getZ();
        if (flip) {
            x = -x;
        }

        double sinR = Math.sin(roll);
        double cosR = Math.cos(roll);
        double t = y * cosR - x * sinR;
        x = y * sinR + x * cosR;
        y = t;
        double sinP = Math.sin(pitch);
        double cosP = Math.cos(pitch);
        t = z * cosP + t * sinP;
        y = y * cosP - z * sinP;
        z = t;
        double sinY = Math.sin(yaw);
        double cosY = Math.cos(yaw);
        t = t * cosY + x * sinY;
        x = x * cosY - z * sinY;
        x *= scale;
        y *= scale;
        z = t * scale;
        BlockVector3.at(x, y, z);
        return BlockVector3.at(x, y, z);
    }

    public static BlockVector3 inverse(BlockVector3 in, double yaw, double pitch, double roll, boolean flip, double scale) {
        double x = in.getX();
        double y = in.getY();
        double z = in.getZ();
        x /= scale;
        y /= scale;
        z /= scale;
        double sinY = Math.sin(yaw);
        double cosY = Math.cos(yaw);
        double t = z * cosY - x * sinY;
        x = x * cosY + z * sinY;
        z = t;
        double sinP = Math.sin(pitch);
        double cosP = Math.cos(pitch);
        t = t * cosP - y * sinP;
        y = y * cosP + z * sinP;
        z = t;
        double sinR = Math.sin(roll);
        double cosR = Math.cos(roll);
        t = y * cosR + x * sinR;
        x = x * cosR - y * sinR;
        if (flip) {
            x = -x;
        }

        return BlockVector3.at(x, t, z);
    }
}

package io.github.winx64.sse.data;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public final class SignData {

    private final String[] lines;
    private final Location location;
    private final BlockFace facing;
    private final boolean wallSign;

    public SignData(String[] lines, Location location, BlockFace facing, boolean wallSign) {
        this.lines = lines;
        this.location = location;
        this.facing = facing;
        this.wallSign = wallSign;
    }

    public Location getLocation() {
        return location;
    }

    public BlockFace getFacing() {
        return facing;
    }

    public boolean isWallSign() {
        return wallSign;
    }
}

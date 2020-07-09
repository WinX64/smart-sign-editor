package io.github.winx64.sse.data;

import org.bukkit.block.BlockFace;

public final class SignData {

    private final BlockFace facing;
    private final boolean wallSign;

    public SignData(BlockFace facing, boolean wallSign) {
        this.facing = facing;
        this.wallSign = wallSign;
    }

    public BlockFace getFacing() {
        return facing;
    }

    public boolean isWallSign() {
        return wallSign;
    }
}

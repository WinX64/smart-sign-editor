package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntitySign;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public final class VersionAdapter_1_19_R3 implements VersionAdapter {

    @Override
    public void openSignEditor(Player player, Sign sign) {
        player.openSign(sign);
    }

    @Override
    public boolean shouldProcessEvent(PlayerInteractEvent event) {
        return event.getHand() == EquipmentSlot.HAND;
    }

    @Override
    public boolean isSign(Block block) {
        BlockData data = block.getBlockData();
        return data instanceof org.bukkit.block.data.type.Sign || data instanceof WallSign;
    }

    @Override
    public SignData getSignData(Sign sign) {
        BlockData blockData = sign.getBlockData();
        if (blockData instanceof org.bukkit.block.data.type.Sign) {
            return new SignData(((org.bukkit.block.data.type.Sign) blockData).getRotation(), false);
        } else {
            return new SignData(((WallSign) blockData).getFacing(), true);
        }
    }

    @Override
    public boolean isSignBeingEdited(Sign sign) {
        Location loc = sign.getLocation();
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        World world = ((CraftWorld) sign.getWorld()).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) Objects.requireNonNull(world.c_(pos));

        return tileEntitySign.h;
    }
}

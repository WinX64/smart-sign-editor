package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntitySign;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public final class VersionAdapter_1_18_R1 implements VersionAdapter {

    @Override
    public void openSignEditor(Player player, Sign sign) {
        Location location = sign.getLocation();
        BlockPosition pos = new BlockPosition(location.getX(), location.getY(), location.getZ());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) Objects.requireNonNull(nmsPlayer.cA().c_(pos));
        PlayerConnection conn = nmsPlayer.b;

        tileEntitySign.f = true;
        tileEntitySign.a(nmsPlayer.cm());
        conn.a(new PacketPlayOutOpenSignEditor(pos));
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
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        World world = ((CraftWorld) sign.getWorld()).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) Objects.requireNonNull(world.c_(pos));

        return tileEntitySign.f;
    }
}

package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public final class VersionAdapter_1_9_R2 implements VersionAdapter {

    @Override
    public void openSignEditor(Player player, Location location) {
        BlockPosition pos = new BlockPosition(location.getX(), location.getY(), location.getZ());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) Objects.requireNonNull(nmsPlayer.world.getTileEntity(pos));
        PlayerConnection conn = nmsPlayer.playerConnection;

        tileEntitySign.isEditable = true;
        tileEntitySign.a(nmsPlayer);
        conn.sendPacket(new PacketPlayOutOpenSignEditor(pos));
    }

    @Override
    public boolean shouldProcessEvent(PlayerInteractEvent event) {
        return event.getHand() == EquipmentSlot.HAND;
    }

    @Override
    public boolean isSign(org.bukkit.block.Block block) {
        return block.getType() == org.bukkit.Material.SIGN_POST || block.getType() == org.bukkit.Material.WALL_SIGN;
    }

    @Override
    public SignData getSignData(Sign sign) {
        org.bukkit.material.Sign materialData = (org.bukkit.material.Sign) sign.getData();
        return new SignData(materialData.getFacing(), materialData.isWallSign());
    }

    @Override
    public boolean isSignBeingEdited(Sign sign) {
        Location loc = sign.getLocation();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        World world = ((CraftWorld) sign.getWorld()).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) Objects.requireNonNull(world.getTileEntity(pos));

        return tileEntitySign.isEditable;
    }
}

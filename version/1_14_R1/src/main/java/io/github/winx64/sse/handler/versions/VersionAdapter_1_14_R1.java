package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class VersionAdapter_1_14_R1 implements VersionAdapter {

    @Override
    public void updateSignText(Player player, Sign sign, String[] text) {
        Location loc = sign.getLocation();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) nmsPlayer.world.getTileEntity(pos);
        PlayerConnection conn = nmsPlayer.playerConnection;
        IChatBaseComponent[] oldSignText = new IChatBaseComponent[4];

        for (int i = 0; i < 4; i++) {
            oldSignText[i] = tileEntitySign.lines[i];
            tileEntitySign.lines[i] = new ChatComponentText(text[i]);
        }
        conn.sendPacket(tileEntitySign.getUpdatePacket());
        System.arraycopy(oldSignText, 0, tileEntitySign.lines, 0, 4);
    }

    @Override
    public void openSignEditor(Player player, Sign sign) {
        Location loc = sign.getLocation();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) nmsPlayer.world.getTileEntity(pos);
        PlayerConnection conn = nmsPlayer.playerConnection;

        tileEntitySign.isEditable = true;
        tileEntitySign.a((EntityHuman) nmsPlayer);
        conn.sendPacket(new PacketPlayOutOpenSignEditor(pos));
    }

    @Override
    public boolean isSignBeingEdited(Sign sign) {
        Location loc = sign.getLocation();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        World world = ((CraftWorld) sign.getWorld()).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) world.getTileEntity(pos);

        return tileEntitySign.isEditable;
    }

    @Override
    public boolean shouldProcessEvent(PlayerInteractEvent event) {
        return event.getHand() == EquipmentSlot.HAND;
    }

    @Override
    public org.bukkit.material.Sign buildSignMaterialData(Sign sign) {
        BlockData blockData = sign.getBlockData();
        if (blockData instanceof WallSign) {
            org.bukkit.material.Sign signData = new org.bukkit.material.Sign(org.bukkit.Material.LEGACY_WALL_SIGN);
            signData.setFacingDirection(((WallSign) blockData).getFacing());
            return signData;
        } else {
            org.bukkit.material.Sign signData = new org.bukkit.material.Sign(org.bukkit.Material.LEGACY_SIGN_POST);
            signData.setFacingDirection(((org.bukkit.block.data.type.Sign) blockData).getRotation());
            return signData;
        }
    }
}

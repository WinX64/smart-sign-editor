package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import net.minecraft.server.v1_13_R2.TileEntitySign;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class VersionAdapter_1_13_R2 implements VersionAdapter {

    @Override
    public void updateSignText(Player player, Location location, String[] text) {
        BlockPosition pos = new BlockPosition(location.getX(), location.getY(), location.getZ());
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
    public void openSignEditor(Player player, Location location) {
        BlockPosition pos = new BlockPosition(location.getX(), location.getY(), location.getZ());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) nmsPlayer.world.getTileEntity(pos);
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
    public boolean isSign(Block block) {
        return block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN;
    }

    @Override
    public SignData getSignData(Block block) {
        org.bukkit.block.Sign blockState = (org.bukkit.block.Sign) block.getState();
        BlockData blockData = block.getBlockData();
        if (blockData instanceof org.bukkit.block.data.type.Sign) {
            return new SignData(blockState.getLines(), block.getLocation(), ((Sign) blockData).getRotation(), false);
        } else {
            return new SignData(blockState.getLines(), block.getLocation(), ((WallSign) blockData).getFacing(), true);
        }
    }

    @Override
    public boolean isSignBeingEdited(Block block) {
        Location loc = block.getLocation();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        World world = ((CraftWorld) block.getWorld()).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) world.getTileEntity(pos);

        return tileEntitySign.isEditable;
    }
}

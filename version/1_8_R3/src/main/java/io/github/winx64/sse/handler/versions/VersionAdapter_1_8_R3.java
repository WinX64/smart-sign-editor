package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.TileEntitySign;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public final class VersionAdapter_1_8_R3 implements VersionAdapter {

    @Override
    public void updateSignText(Player player, Location location, String[] text) {
        ChatComponentText[] chatComponent = new ChatComponentText[4];
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;

        for (int i = 0; i < 4; i++) {
            chatComponent[i] = new ChatComponentText(text[i]);
        }
        conn.sendPacket(new PacketPlayOutUpdateSign(null, new BlockPosition(location.getX(), location.getY(),
                location.getZ()), chatComponent));
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
        return true;
    }

    @Override
    public boolean isSign(Block block) {
        return block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN;
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
        TileEntitySign tileEntitySign = (TileEntitySign) world.getTileEntity(pos);

        return tileEntitySign.isEditable;
    }
}

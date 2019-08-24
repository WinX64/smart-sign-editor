package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public final class VersionAdapter_1_8_R1 implements VersionAdapter {

    /**
     * This specific 1.8 build is very odd. It strangely adds color codes for
     * black in every line. Have to handle it differently here.
     * <p>
     * EDIT (2.1.0): Perhaps 1.8 is more broken than I initially though, sign
     * events pass the wrong texts as well, with extra colors and format codes
     */
    @Override
    public void updateSignText(Player player, Sign sign, String[] text) {
        Location loc = sign.getLocation();
        ChatComponentText[] chatComponent = new ChatComponentText[4];
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;

        for (int i = 0; i < 4; i++) {
            while (text[i].startsWith("&0")) {
                text[i] = text[i].substring(2);
            }
            chatComponent[i] = new ChatComponentText(text[i]);
        }
        conn.sendPacket(new PacketPlayOutUpdateSign(null, new BlockPosition(loc.getX(), loc.getY(), loc.getZ()),
                chatComponent));
    }

    @Override
    public void openSignEditor(Player player, Sign sign) {
        Location loc = sign.getLocation();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign tileEntitySign = (TileEntitySign) nmsPlayer.world.getTileEntity(pos);
        PlayerConnection conn = nmsPlayer.playerConnection;

        tileEntitySign.isEditable = true;
        tileEntitySign.a(nmsPlayer);
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
        return true;
    }

    @Override
    public org.bukkit.material.Sign buildSignMaterialData(Sign sign) {
        return (org.bukkit.material.Sign) sign.getData();
    }
}

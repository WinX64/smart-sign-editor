package io.github.winx64.sse.handler.versions;

import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class VersionAdapter_1_9_R1 implements VersionAdapter {

    @Override
    public void updateSignText(Player player, Sign sign, String[] text) {
        Location loc = sign.getLocation();
        ChatComponentText[] chatComponent = new ChatComponentText[4];
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;

        for (int i = 0; i < 4; i++) {
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
        return event.getHand() == EquipmentSlot.HAND;
    }
}

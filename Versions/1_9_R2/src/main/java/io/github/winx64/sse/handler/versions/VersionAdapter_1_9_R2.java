package io.github.winx64.sse.handler.versions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.base.Charsets;

import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.ChatComponentText;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_9_R2.PlayerConnection;
import net.minecraft.server.v1_9_R2.TileEntitySign;
import net.minecraft.server.v1_9_R2.World;

public final class VersionAdapter_1_9_R2 implements VersionAdapter {

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
		for (int i = 0; i < 4; i++) {
			tileEntitySign.lines[i] = oldSignText[i];
		}
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

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers();
	}

	@Override
	public YamlConfiguration loadFromResource(InputStream input) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(input, Charsets.UTF_8)) {
			return YamlConfiguration.loadConfiguration(reader);
		}
	}
}

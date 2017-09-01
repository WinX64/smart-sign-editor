package io.github.winx64.sse.handler.versions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.base.Charsets;

import io.github.winx64.sse.handler.VersionAdapter;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_7_R4.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.TileEntitySign;
import net.minecraft.server.v1_7_R4.World;

public final class VersionAdapter_1_7_R4 implements VersionAdapter {

	@Override
	public void updateSignText(Player player, Sign sign, String[] text) {
		Location loc = sign.getLocation();
		PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
		conn.sendPacket(new PacketPlayOutUpdateSign(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), text));
	}

	@Override
	public void openSignEditor(Player player, Sign sign) {
		Location loc = sign.getLocation();
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		TileEntitySign tileEntitySign = (TileEntitySign) nmsPlayer.world.getTileEntity(loc.getBlockX(), loc.getBlockY(),
				loc.getBlockZ());
		PlayerConnection conn = nmsPlayer.playerConnection;

		tileEntitySign.isEditable = true;
		tileEntitySign.a(nmsPlayer);
		conn.sendPacket(new PacketPlayOutOpenSignEditor(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@Override
	public boolean isSignBeingEdited(Sign sign) {
		Location loc = sign.getLocation();
		World world = ((CraftWorld) sign.getWorld()).getHandle();
		TileEntitySign tileEntitySign = (TileEntitySign) world.getTileEntity(loc.getBlockX(), loc.getBlockY(),
				loc.getBlockZ());

		return tileEntitySign.isEditable;
	}

	@Override
	public boolean shouldProcessEvent(PlayerInteractEvent event) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		return Arrays.asList(Bukkit.getOnlinePlayers());
	}

	@Override
	public YamlConfiguration loadFromResource(InputStream input) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(input, Charsets.UTF_8)) {
			return YamlConfiguration.loadConfiguration(reader);
		}
	}
}

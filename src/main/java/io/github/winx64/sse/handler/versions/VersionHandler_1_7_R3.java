/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2016
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.winx64.sse.handler.versions;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.github.winx64.sse.handler.VersionHandler;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_7_R3.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import net.minecraft.server.v1_7_R3.TileEntitySign;

public class VersionHandler_1_7_R3 extends VersionHandler {

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
    public Collection<? extends Player> getOnlinePlayers() {
	return Arrays.asList(((CraftServer) Bukkit.getServer()).getOnlinePlayers());
    }
}

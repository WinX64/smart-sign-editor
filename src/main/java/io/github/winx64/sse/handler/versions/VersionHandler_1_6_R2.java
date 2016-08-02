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
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.github.winx64.sse.handler.VersionHandler;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet130UpdateSign;
import net.minecraft.server.v1_6_R2.Packet133OpenTileEntity;
import net.minecraft.server.v1_6_R2.PlayerConnection;
import net.minecraft.server.v1_6_R2.TileEntitySign;

public final class VersionHandler_1_6_R2 extends VersionHandler {

    @Override
    public void updateSignText(Player player, Sign sign, String[] text) {
	Location loc = sign.getLocation();
	PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
	conn.sendPacket(new Packet130UpdateSign(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), text));
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
	conn.sendPacket(new Packet133OpenTileEntity(0, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    @Override
    public Collection<? extends Player> getOnlinePlayers() {
	return Arrays.asList(((CraftServer) Bukkit.getServer()).getOnlinePlayers());
    }
}

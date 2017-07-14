/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2017
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
package io.github.winx64.sse;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Class with some useful trigonometric methods to help calculate player's line
 * of sight and sign intersection
 * 
 * @author Lucas
 *
 */
public final class MathUtil {

	/**
	 * The 16 normal vectors for the planes formed by the 16 different rotated
	 * sign posts. From the rotation 0 to 15
	 */
	private static final Map<BlockFace, Vector> SIGN_POST_PLANE_NORMAL_VECTORS;

	/**
	 * The 4 normal vectors for the planes formed by the 4 different rotated
	 * wall signs. Rotations 0, 4, 8 and 12
	 */
	private static final Map<BlockFace, Vector> WALL_SIGN_PLANE_NORMAL_VECTORS;

	/**
	 * Distance between the block below and the base of the sign post
	 */
	private static final double SIGN_POST_POLE_HEIGHT_OFFSET = 0.58250;
	/**
	 * Distance between the block below and the base of the wall sign
	 */
	private static final double WALL_SIGN_WALL_HEIGHT_OFFSET = 0.27050;
	/**
	 * Distance between the block and the back of the wall sign
	 */
	private static final double WALL_SIGN_WALL_DISTANCE_OFFSET = 0.0625;

	/**
	 * Thickness of the sign plate
	 */
	private static final double SIGN_THICKNESS = 0.084;
	/**
	 * Height of the sign plate
	 */
	private static final double SIGN_HEIGHT = 0.5;
	/**
	 * Width of the sign plate
	 */
	private static final double SIGN_WIDTH = 1.0;

	/**
	 * Pairs of Y coordinates for the boundaries of each line in the sign plate
	 */
	private static final double SIGN_LINE_Y_OFFSET[] = new double[] { 5.0, 0.36650, 0.26250, 0.15850, 0.0 };

	static {
		Map<BlockFace, Vector> signPostVectors = new HashMap<BlockFace, Vector>();
		Map<BlockFace, Vector> wallSignVectors = new HashMap<BlockFace, Vector>();

		double p8 = Math.PI / 8;

		signPostVectors.put(BlockFace.SOUTH, new Vector(cos(4 * p8), 0, sin(4 * p8)));
		signPostVectors.put(BlockFace.SOUTH_SOUTH_WEST, new Vector(cos(5 * p8), 0, sin(5 * p8)));
		signPostVectors.put(BlockFace.SOUTH_WEST, new Vector(cos(6 * p8), 0, sin(6 * p8)));
		signPostVectors.put(BlockFace.WEST_SOUTH_WEST, new Vector(cos(7 * p8), 0, sin(7 * p8)));
		signPostVectors.put(BlockFace.WEST, new Vector(cos(8 * p8), 0, sin(8 * p8)));
		signPostVectors.put(BlockFace.WEST_NORTH_WEST, new Vector(cos(9 * p8), 0, sin(9 * p8)));
		signPostVectors.put(BlockFace.NORTH_WEST, new Vector(cos(10 * p8), 0, sin(10 * p8)));
		signPostVectors.put(BlockFace.NORTH_NORTH_WEST, new Vector(cos(11 * p8), 0, sin(11 * p8)));
		signPostVectors.put(BlockFace.NORTH, new Vector(cos(12 * p8), 0, sin(12 * p8)));
		signPostVectors.put(BlockFace.NORTH_NORTH_EAST, new Vector(cos(13 * p8), 0, sin(13 * p8)));
		signPostVectors.put(BlockFace.NORTH_EAST, new Vector(cos(14 * p8), 0, sin(14 * p8)));
		signPostVectors.put(BlockFace.EAST_NORTH_EAST, new Vector(cos(15 * p8), 0, sin(15 * p8)));
		signPostVectors.put(BlockFace.EAST, new Vector(cos(16 * p8), 0, sin(16 * p8)));
		signPostVectors.put(BlockFace.EAST_SOUTH_EAST, new Vector(cos(1 * p8), 0, sin(1 * p8)));
		signPostVectors.put(BlockFace.SOUTH_EAST, new Vector(cos(2 * p8), 0, sin(2 * p8)));
		signPostVectors.put(BlockFace.SOUTH_SOUTH_EAST, new Vector(cos(3 * p8), 0, sin(3 * p8)));

		wallSignVectors.put(BlockFace.SOUTH, signPostVectors.get(BlockFace.SOUTH));
		wallSignVectors.put(BlockFace.WEST, signPostVectors.get(BlockFace.WEST));
		wallSignVectors.put(BlockFace.NORTH, signPostVectors.get(BlockFace.NORTH));
		wallSignVectors.put(BlockFace.EAST, signPostVectors.get(BlockFace.EAST));

		SIGN_POST_PLANE_NORMAL_VECTORS = Collections.unmodifiableMap(signPostVectors);
		WALL_SIGN_PLANE_NORMAL_VECTORS = Collections.unmodifiableMap(wallSignVectors);
	}

	private MathUtil() {}

	/**
	 * Gets at which coordinates the player's line of sight intersected a sign
	 * 
	 * @param player
	 *            The player
	 * @param sign
	 *            The sign
	 * @return The point of intersection of the player's line of sight and the
	 *         sign, or null, if it happened out of bounds
	 */
	public static Vector getSightSignIntersection(Player player, Sign sign) {
		org.bukkit.material.Sign materialData = (org.bukkit.material.Sign) sign.getData();

		Location loc = player.getEyeLocation();
		BlockFace face = materialData.getFacing();

		Vector linePoint = loc.toVector();
		Vector lineDirection = loc.getDirection();
		Vector planeNormal = materialData.isWallSign() ? WALL_SIGN_PLANE_NORMAL_VECTORS.get(face)
				: SIGN_POST_PLANE_NORMAL_VECTORS.get(face);
		Vector planePoint = materialData.isWallSign()
				? sign.getLocation().add(0.5, (SIGN_HEIGHT / 2) + WALL_SIGN_WALL_HEIGHT_OFFSET, 0.5)
						.add(planeNormal.clone().multiply(-1)
								.multiply(0.5 - WALL_SIGN_WALL_DISTANCE_OFFSET - (SIGN_THICKNESS / 2)))
						.toVector()
				: sign.getLocation().add(0.5, SIGN_POST_POLE_HEIGHT_OFFSET + (SIGN_HEIGHT / 2), 0.5)
						.add(planeNormal.clone().multiply(SIGN_THICKNESS / 2)).toVector();

		Vector intersection = getLinePlaneIntersection(linePoint, lineDirection, planePoint, planeNormal);
		if (!isInsidePostSignBoundaries(intersection, planePoint)) {
			return null;
		}

		return intersection;
	}

	/**
	 * Gets which line the player is looking at
	 * 
	 * @param intersection
	 *            The point of intersection between the player's line of sight
	 *            and the sign
	 * @param sign
	 *            The sign
	 * @return The line of the sign that the intersection is on
	 */
	public static int getSignLine(Vector intersection, Sign sign) {
		org.bukkit.material.Sign materialData = (org.bukkit.material.Sign) sign.getData();
		double y = intersection.getY() - sign.getLocation().getY()
				- (materialData.isWallSign() ? WALL_SIGN_WALL_HEIGHT_OFFSET : SIGN_POST_POLE_HEIGHT_OFFSET);
		for (int i = 0; i < 4; i++) {
			if (SIGN_LINE_Y_OFFSET[i] >= y && SIGN_LINE_Y_OFFSET[i + 1] < y) {
				return i;
			}
		}
		return 3;
	}

	/**
	 * Gets the parametric value(t) of the intersection point between the
	 * specified line and plane
	 * 
	 * @param linePoint
	 *            A point of the line
	 * @param lineDirection
	 *            The direction of the line
	 * @param planePoint
	 *            A point of the plane
	 * @param planeNormal
	 *            A normal vector of the plane
	 * @return The t value
	 */
	public static double getParametricIntersectionValue(Vector linePoint, Vector lineDirection, Vector planePoint,
			Vector planeNormal) {
		double d = planePoint.dot(planeNormal);

		return (d - planeNormal.dot(linePoint)) / planeNormal.dot(lineDirection);
	}

	/**
	 * Checks if the point of intersection is inside the sign plate's boundaries
	 * 
	 * @param intersection
	 *            The point of intersection
	 * @param planePoint
	 *            The sign's plane central point
	 * @return Whether the intersection is valid or not
	 */
	private static boolean isInsidePostSignBoundaries(Vector intersection, Vector planePoint) {
		double distX = intersection.getX() - planePoint.getX();
		double distY = intersection.getY() - planePoint.getY();
		double distZ = intersection.getZ() - planePoint.getZ();

		return (distX * distX) + (distZ * distZ) <= (SIGN_WIDTH / 2) * (SIGN_WIDTH / 2)
				&& Math.abs(distY) <= (SIGN_HEIGHT / 2);
	}

	/**
	 * Gets at which point the specified line intersects the specified plane
	 * 
	 * @param linePoint
	 *            A point of the line
	 * @param lineDirection
	 *            The direction of the line
	 * @param planePoint
	 *            A point of the plane
	 * @param planeNormal
	 *            A normal vector of the plane
	 * @return The point at which the line intersects the plane
	 */
	private static Vector getLinePlaneIntersection(Vector linePoint, Vector lineDirection, Vector planePoint,
			Vector planeNormal) {

		double t = getParametricIntersectionValue(linePoint, lineDirection, planePoint, planeNormal);
		if (t < 0) {
			return null;
		}

		double x = linePoint.getX() + lineDirection.getX() * t;
		double y = linePoint.getY() + lineDirection.getY() * t;
		double z = linePoint.getZ() + lineDirection.getZ() * t;

		return new Vector(x, y, z);
	}
}

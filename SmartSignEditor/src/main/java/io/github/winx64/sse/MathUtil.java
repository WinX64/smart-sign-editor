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
package io.github.winx64.sse;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
	 * One eighth of PI
	 */
	private static final double P8 = Math.PI / 8;

	/**
	 * The 16 normal vectors for the planes formed by the 16 different rotated
	 * sign posts. From the rotation 0 to 15
	 */
	private static final Vector[] SIGN_POST_PLANE_NORMAL_VECTORS = new Vector[] {
			new Vector(cos(4 * P8), 0, sin(4 * P8)), new Vector(cos(5 * P8), 0, sin(5 * P8)),
			new Vector(cos(6 * P8), 0, sin(6 * P8)), new Vector(cos(7 * P8), 0, sin(7 * P8)),
			new Vector(cos(8 * P8), 0, sin(8 * P8)), new Vector(cos(9 * P8), 0, sin(9 * P8)),
			new Vector(cos(10 * P8), 0, sin(10 * P8)), new Vector(cos(11 * P8), 0, sin(11 * P8)),
			new Vector(cos(12 * P8), 0, sin(12 * P8)), new Vector(cos(13 * P8), 0, sin(13 * P8)),
			new Vector(cos(14 * P8), 0, sin(14 * P8)), new Vector(cos(15 * P8), 0, sin(15 * P8)),
			new Vector(cos(16 * P8), 0, sin(16 * P8)), new Vector(cos(1 * P8), 0, sin(1 * P8)),
			new Vector(cos(2 * P8), 0, sin(2 * P8)), new Vector(cos(3 * P8), 0, sin(3 * P8)) };

	/**
	 * The 4 normal vectors for the planes formed by the 4 different rotated
	 * wall signs. Rotations 0, 4, 8 and 12
	 */
	private static final Vector[] WALL_SIGN_PLANE_NORMAL_VECTORS = new Vector[] { new Vector(0, 0, 1),
			new Vector(-1, 0, 0), new Vector(0, 0, -1), new Vector(1, 0, 0) };

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
		int rotation = getRotationId(materialData.getFacing());

		Vector linePoint = loc.toVector();
		Vector lineDirection = loc.getDirection();
		Vector planeNormal = materialData.isWallSign() ? WALL_SIGN_PLANE_NORMAL_VECTORS[rotation / 4]
				: SIGN_POST_PLANE_NORMAL_VECTORS[rotation];
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

	/**
	 * Converts a specified BlockFace to an ID
	 * 
	 * @param blockFace
	 *            The block face
	 * @return The representing ID
	 */
	private static int getRotationId(BlockFace blockFace) {
		switch (blockFace) {
			case SOUTH:
				return 0;

			case SOUTH_SOUTH_WEST:
				return 1;

			case SOUTH_WEST:
				return 2;

			case WEST_SOUTH_WEST:
				return 3;

			case WEST:
				return 4;

			case WEST_NORTH_WEST:
				return 5;

			case NORTH_WEST:
				return 6;

			case NORTH_NORTH_WEST:
				return 7;

			case NORTH:
				return 8;

			case NORTH_NORTH_EAST:
				return 9;

			case NORTH_EAST:
				return 10;

			case EAST_NORTH_EAST:
				return 11;

			case EAST:
				return 12;

			case EAST_SOUTH_EAST:
				return 13;

			case SOUTH_EAST:
				return 14;

			case SOUTH_SOUTH_EAST:
				return 15;

			default:
				return -1;
		}
	}
}

package io.github.winx64.sse;

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
 * @author WinX64
 *
 */
public final class MathUtil {

	/**
	 * Normal vectors for each sign rotation
	 */
	private static final Map<BlockFace, Vector> SIGN_NORMAL_VECTORS;

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
	private static final double SIGN_LINE_Y_OFFSET[] = new double[] { 0.5, 0.36650, 0.26250, 0.15850, 0.0 };

	/**
	 * One eighth of PI
	 */
	private static final double EIGHTH_PI = Math.PI / 8;

	static {
		Map<BlockFace, Vector> normalVectors = new HashMap<BlockFace, Vector>();

		for (BlockFace face : BlockFace.values()) {
			if (face == BlockFace.UP || face == BlockFace.DOWN || face == BlockFace.SELF) {
				continue;
			}

			double angle = Math.atan2(face.getModZ(), face.getModX());
			angle = Math.round(angle / EIGHTH_PI) * EIGHTH_PI;

			normalVectors.put(face, new Vector(Math.cos(angle), 0, Math.sin(angle)));
		}

		SIGN_NORMAL_VECTORS = Collections.unmodifiableMap(normalVectors);
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
		Vector planeNormal = SIGN_NORMAL_VECTORS.get(face);
		Vector planePoint = materialData.isWallSign()
				? sign.getLocation().add(0.5, (SIGN_HEIGHT / 2) + WALL_SIGN_WALL_HEIGHT_OFFSET, 0.5)
						.add(planeNormal.clone().multiply(-1)
								.multiply(0.5 - WALL_SIGN_WALL_DISTANCE_OFFSET - (SIGN_THICKNESS / 2)))
						.toVector()
				: sign.getLocation().add(0.5, SIGN_POST_POLE_HEIGHT_OFFSET + (SIGN_HEIGHT / 2), 0.5)
						.add(planeNormal.clone().multiply(SIGN_THICKNESS / 2)).toVector();

		Vector intersection = getLinePlaneIntersection(linePoint, lineDirection, planePoint, planeNormal);
		if (intersection == null || !isInsidePostSignBoundaries(intersection, planePoint)
				|| !isPointInFrontOfPlane(linePoint, planePoint, planeNormal)) {
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
	private static double getParametricIntersectionValue(Vector linePoint, Vector lineDirection, Vector planePoint,
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

		return Math.pow(distX, 2) + Math.pow(distZ, 2) <= Math.pow(SIGN_WIDTH / 2, 2)
				&& Math.abs(distY) <= SIGN_HEIGHT / 2;
	}

	/**
	 * Checks if a given point is in front of a given plane
	 * 
	 * @param linePoint
	 *            The given point
	 * @param planePoint
	 *            A point of the plane
	 * @param planeNormal
	 *            The plane's normal vector
	 * @return
	 */
	private static boolean isPointInFrontOfPlane(Vector linePoint, Vector planePoint, Vector planeNormal) {
		Vector directionVector = linePoint.clone().subtract(planePoint);

		return directionVector.dot(planeNormal) > 0;
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

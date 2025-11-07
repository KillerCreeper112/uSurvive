package killercreepr.usurvive.core.guide.particle;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class PathSmoother {

    public static List<Location> catmullRomSpline(List<Location> points, double resolution) {
        if (points.size() < 4) return points; // need at least 4 control points

        List<Location> smooth = new ArrayList<>();
        for (int i = 0; i < points.size() - 3; i++) {
            Location p0 = points.get(i);
            Location p1 = points.get(i + 1);
            Location p2 = points.get(i + 2);
            Location p3 = points.get(i + 3);

            for (double t = 0; t < 1; t += resolution) {
                Vector v = interpolate(p0.toVector(), p1.toVector(), p2.toVector(), p3.toVector(), t);
                smooth.add(new Location(p0.getWorld(), v.getX(), v.getY(), v.getZ()));
            }
        }

        // always include the final point
        smooth.add(points.get(points.size() - 1));
        return smooth;
    }

    private static Vector interpolate(Vector p0, Vector p1, Vector p2, Vector p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double x = 0.5 * ((2 * p1.getX()) +
                (-p0.getX() + p2.getX()) * t +
                (2*p0.getX() - 5*p1.getX() + 4*p2.getX() - p3.getX()) * t2 +
                (-p0.getX() + 3*p1.getX() - 3*p2.getX() + p3.getX()) * t3);

        double y = 0.5 * ((2 * p1.getY()) +
                (-p0.getY() + p2.getY()) * t +
                (2*p0.getY() - 5*p1.getY() + 4*p2.getY() - p3.getY()) * t2 +
                (-p0.getY() + 3*p1.getY() - 3*p2.getY() + p3.getY()) * t3);

        double z = 0.5 * ((2 * p1.getZ()) +
                (-p0.getZ() + p2.getZ()) * t +
                (2*p0.getZ() - 5*p1.getZ() + 4*p2.getZ() - p3.getZ()) * t2 +
                (-p0.getZ() + 3*p1.getZ() - 3*p2.getZ() + p3.getZ()) * t3);

        return new Vector(x, y, z);
    }
}

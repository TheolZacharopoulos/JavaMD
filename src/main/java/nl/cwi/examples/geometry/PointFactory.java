package nl.cwi.examples.geometry;

import nl.cwi.examples.geometry.schemas.Line;
import nl.cwi.examples.geometry.schemas.Point2D;
import nl.cwi.examples.geometry.schemas.Point3D;
import nl.cwi.managed_data_4j.IFactory;

/**
 * The Point Schema Factory.
 *
 * This is used to create instances of Point, and Line objects.
 */
public interface PointFactory extends IFactory {
    Point2D Point2D();
    Point2D Point2D(Integer x, Integer y);

    Point3D Point3D();
    Point3D Point3D(Integer x, Integer y, Integer z);

    Line Line();
}

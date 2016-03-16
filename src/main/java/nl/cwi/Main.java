package nl.cwi;

import nl.cwi.examples.ccc.UpdateLogger;
import nl.cwi.examples.geometry.*;
import nl.cwi.examples.patterns.lockable.Lockable;
import nl.cwi.examples.patterns.lockable.LockableFactory;
import nl.cwi.examples.patterns.observer.Observable;
import nl.cwi.examples.patterns.observer.ObservableFactory;
import nl.cwi.managed_data_4j.data_manager.BasicFactory;
import nl.cwi.managed_data_4j.schema.boot.SchemaFactory;
import nl.cwi.managed_data_4j.schema.load.SchemaLoader;
import nl.cwi.managed_data_4j.schema.models.definition.*;
import nl.cwi.managed_data_4j.utils.DebugUtils;
import nl.cwi.managed_data_4j.utils.MObjectUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        // ================================ Schema Schema ========================================

        // We bootstrap the SchemaSchema from the minimal BootstrapSchema that has only classes and fields.
        // This minimal bootstrap schema is necessarily self-describing as it must change itself and
        // it processes simplistic data managers that only allow updating.
        // It is also hardcoded.
        final Schema bootstrapSchema = SchemaLoader.bootLoad();

        final BasicFactory basicFactory = new BasicFactory(SchemaFactory.class, bootstrapSchema);

        // Create a schema Factory which creates Schema instances.
        final SchemaFactory schemaFactory = basicFactory.make();

        // The schemas are described by the SchemaSchema.
        // This schemaSchema is also self-describing.
        final Schema realSchemaSchema =
                SchemaLoader.load(schemaFactory, Schema.class, Type.class, Primitive.class, Klass.class, Field.class);

//        DebugUtils.debugTypes(realSchemaSchema.types());

        // =======================
        // Test equality
        final BasicFactory basicFactory2 = new BasicFactory(SchemaFactory.class, realSchemaSchema);
        final SchemaFactory schemaFactory2 = basicFactory2.make();
        final Schema realSchemaSchema2 =
            SchemaLoader.load(schemaFactory2, Schema.class, Type.class, Primitive.class, Klass.class, Field.class);

        boolean equal = MObjectUtils.equals(realSchemaSchema, realSchemaSchema2);
        if (equal) {
            System.out.println("* EQUAL *");
        } else {
            System.out.println("Should be equal");
            System.exit(-1);
        }

        // ================================ Data objects ========================================
        System.out.println("=============");
        System.out.println("Data Objects: ");

        // Data objects (like Point) are described by schemas (like the Point interface)
        // This schema is managed by a data manager capable of initialization allowing the objects
        // (points) to be created with starting props.
        // use the schemaFactory2, the schema factory which has been made from the realSchemaSchema
        final Schema pointSchema = SchemaLoader.load(schemaFactory2, Point.class, Point2D.class, Point3D.class, Line.class);
        final BasicFactory basicFactoryForPoints = new BasicFactory(PointFactory.class, pointSchema);
        final PointFactory pointFactory = basicFactoryForPoints.make();

        final Point2D point = pointFactory.point2D(3, 2);

        final Line line = pointFactory.line();
        line.startPoint(point);
        line.endPoint(point);

        System.out.print(point.x() + " + " + point.y() + " = ");
        System.out.print(point.x() + point.y());
        System.out.print(" == ");
        System.out.println(line.startPoint().x() + line.startPoint().y());

        // Check multi points
        Set<Point2D> points = new LinkedHashSet<>(Arrays.asList(
           pointFactory.point2D(1, 2),
           pointFactory.point2D(2, 3),
           pointFactory.point2D(1, 4)
        ));

        System.out.println("============");
        System.out.println("Set of 2D points: ");
        for (Point2D point2D : points) {
            System.out.println("x = " + point2D.x() + ", y = " + point2D.y());
        }

        // ================================ Observer ========================================
        System.out.println("=============");
        System.out.println("Observable Objects: ");

        final ObservableFactory observableFactory = new ObservableFactory(PointFactory.class, pointSchema);
        final PointFactory observablePointFactory = observableFactory.make();

        // Create a new observer-record managed object.
        final Point3D observerPoint = observablePointFactory.point3D();

        // Add Logging concerns
        ((Observable) observerPoint).observe(UpdateLogger::log);

        observerPoint.x(1);
        observerPoint.y(6);
        observerPoint.z(9);
        observerPoint.x(observerPoint.x() + observerPoint.y());
        System.out.println(observerPoint.x() + observerPoint.y() + observerPoint.z());

        // ================================ Lockable ========================================
        System.out.println("=============");
        System.out.println("Lockable Objects: ");

        final LockableFactory lockableFactory = new LockableFactory(PointFactory.class, pointSchema);
        final PointFactory lockablePointFactory = lockableFactory.make();
        final Point2D lockablePoint = lockablePointFactory.point2D();
        lockablePoint.x(1);
        lockablePoint.y(1);

        // It was mutable until now, now it is locked (immutable).
        ((Lockable)lockablePoint).lock();

        try {
            lockablePoint.x(2); // Should throw here since its immutable.
        } catch (IllegalAccessError e) {
            System.out.println("IllegalAccessError: " + e.getMessage());
        }
        System.out.println(lockablePoint.x());

        System.exit(0);
    }
}

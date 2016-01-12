package nl.cwi.managed_data_4j.schema.models.definition;

import com.sun.istack.internal.Nullable;
import nl.cwi.managed_data_4j.schema.models.definition.annotations.Contain;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * While there are many kinds of schemas and data managers
 * there is one basic schema that is used in the implementation of itself.
 * This schema is called the Schema Schema.
 *
 * This is a self-described Schema,
 * since every concept is used is included in its definition.
 */

// TODO:
//interface M {
//    Klass schemaKlass();
//}
//
//public interface Schema extends M {

public interface Schema {

    /**
     * The Interface which describes this schema.
     */
    // TODO: Should leave
    Set<Class> klassInterfaces(Class... interfaces);

    /**
     * A set of all the types in the schema.
     */
    @Contain
    Set<Type> types(Type... type);

    /**
     * A set of klasses types.
     */
    default Set<Klass> klasses() {
        return types().stream()
            .filter(Klass.class::isInstance)
            .map(Klass.class::cast)
            .collect(Collectors.toSet());
    }

    /**
     * A set of primitive types.
     */
    default Set<Primitive> primitives() {
        return types().stream()
            .filter(Primitive.class::isInstance)
            .map(Primitive.class::cast)
            .collect(Collectors.toSet());
    }
}
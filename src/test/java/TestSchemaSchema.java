import nl.cwi.managed_data_4j.data_managers.BasicFactory;
import nl.cwi.managed_data_4j.data_managers.DataManagerFactory;
import nl.cwi.managed_data_4j.schema.boot.SchemaFactory;
import nl.cwi.managed_data_4j.schema.load.SchemaLoader;
import nl.cwi.managed_data_4j.schema.models.schema_schema.*;
import org.junit.Before;
import org.junit.Test;


import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

public class TestSchemaSchema {

    Schema bootstrapSchema;
    BasicFactory basicFactory;
    SchemaFactory schemaFactory;
    Schema realSchemaSchema;

    @Before
    public void setup() {
        bootstrapSchema = SchemaLoader.bootLoad();

        basicFactory = new BasicFactory();
        schemaFactory = DataManagerFactory.make(basicFactory, SchemaFactory.class, bootstrapSchema);
        realSchemaSchema = SchemaLoader.load(schemaFactory, Schema.class, Type.class, Primitive.class, Klass.class, Field.class);
    }

    @Test
    public void testBootStrap_schemaSchema() {
        final Set<Class> bootstrapSchemaKlassInterfaces = bootstrapSchema.klassInterfaces();
        final Set<Class> realSchemaSchemaKlassInterfaces = realSchemaSchema.klassInterfaces();

        assertThat(bootstrapSchemaKlassInterfaces, is(realSchemaSchemaKlassInterfaces));

        // TODO
        final Set<Type> bootstrapSchemaTypes = bootstrapSchema.types();
        final Set<Type> realSchemaSchemaTypes = realSchemaSchema.types();
    }
}
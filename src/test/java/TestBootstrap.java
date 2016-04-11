import nl.cwi.managed_data_4j.language.data_manager.BasicFactory;
import nl.cwi.managed_data_4j.language.schema.boot.SchemaFactory;
import nl.cwi.managed_data_4j.language.schema.load.SchemaLoader;
import nl.cwi.managed_data_4j.language.schema.models.definition.*;
import nl.cwi.managed_data_4j.language.utils.MObjectUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TestBootstrap {

    @Before
    public void setup() {
        PropertyConfigurator.configure("src/test/resources/logger.properties");
    }

    @Test
    public void equality_Test() {
        final Schema bootstrapSchema = SchemaLoader.bootLoad();

        final BasicFactory basicFactory = new BasicFactory(SchemaFactory.class, bootstrapSchema);

        // Create a schema Factory which creates Schema instances.
        final SchemaFactory schemaFactory = basicFactory.make();

        // The schemas are described by the SchemaSchema.
        // This schemaSchema is also self-describing.
        final Schema realSchemaSchema = SchemaLoader.load(
                schemaFactory, bootstrapSchema, Schema.class, Type.class, Primitive.class, Klass.class, Field.class);

        // =======================
        // Test equality
        final BasicFactory basicFactory2 = new BasicFactory(SchemaFactory.class, realSchemaSchema);
        final SchemaFactory schemaFactory2 = basicFactory2.make();
        final Schema realSchemaSchema2 = SchemaLoader.load(
                schemaFactory2, realSchemaSchema, Schema.class, Type.class, Primitive.class, Klass.class, Field.class);

        assertTrue(MObjectUtils.equals(bootstrapSchema, realSchemaSchema));
        assertTrue(MObjectUtils.equals(realSchemaSchema, realSchemaSchema2));
        assertTrue(MObjectUtils.equals(bootstrapSchema, realSchemaSchema2));
    }
}
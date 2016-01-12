package nl.cwi.managed_data_4j.schema.models.implementation;

import nl.cwi.managed_data_4j.schema.boot.boot_fields.NullField;
import nl.cwi.managed_data_4j.schema.models.definition.Field;
import nl.cwi.managed_data_4j.schema.models.definition.Klass;
import nl.cwi.managed_data_4j.schema.models.definition.Schema;
import nl.cwi.managed_data_4j.schema.models.definition.Type;

public abstract class AbstractField implements Field {

    protected Schema schema;
    protected Klass owner;

    public AbstractField(Schema schema, Klass owner) {
        this.schema = schema;
        this.owner = owner;
    }

    @Override
    public abstract String name(String... name);

    @Override
    public abstract Type type(Type... type);

    @Override
    public Boolean many(Boolean... many) {
        return false;
    }

    @Override
    public Boolean optional(Boolean... optional) {
        return false;
    }

    @Override
    public Field inverse(Field... field) {
        return new NullField();
    }

    @Override
    public Klass owner(Klass... owner) {
        return this.owner;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void setOwner(Klass owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        //TODO: Add schema, fix optional and many
        if (this == o) return true;

        AbstractField that = (AbstractField) o;

        if (!this.name().equals(that.name())) return false;
//        if (!this.owner().equals(that.owner())) return false;
//        if ((this.optional()) != (that.optional())) return false;
//        if ((this.many()) != (that.many())) return false;
//        if (!this.inverse().equals(that.inverse())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        //TODO: Add schema
        int result = this.name() != null ? this.name().hashCode() : 0;
        result = 31 * result + (this.owner != null ? this.owner.hashCode() : 0);
        return result;
    }
}

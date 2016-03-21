package nl.cwi.managed_data_4j.ccconcerns.patterns.lockable;

import nl.cwi.managed_data_4j.language.data_manager.IFactory;
import nl.cwi.managed_data_4j.language.managed_object.MObject;
import nl.cwi.managed_data_4j.language.managed_object.managed_object_field.errors.InvalidFieldValueException;
import nl.cwi.managed_data_4j.language.schema.models.definition.Klass;

public class LockableMObject extends MObject implements Lockable {

    private boolean isLocked = false;

    public LockableMObject(Klass schemaKlass, IFactory factory, Object... initializers) {
        super(schemaKlass, factory, initializers);
    }

    public void lock() {
        isLocked = true;
    }

    @Override
    protected void _set(String name, Object value) throws NoSuchFieldError, InvalidFieldValueException {
        if (isLocked) {
            throw new IllegalAccessError("Cannot change " + name + " of locked object " + schemaKlass.name() + ".");
        }
        super._set(name, value);
    }
}
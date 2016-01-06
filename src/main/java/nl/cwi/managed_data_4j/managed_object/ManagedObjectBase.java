package nl.cwi.managed_data_4j.managed_object;

import nl.cwi.managed_data_4j.data_managers.IFactory;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.errors.InvalidFieldValueException;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.errors.UnknownPrimitiveTypeException;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Field;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Klass;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Type;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The Basic Managed Object, all managed objects should inherit from this one.
 */
public class ManagedObjectBase implements InvocationHandler {

    // Store props for the object: <Name, Value>
    protected Map<String, Object> props = new HashMap<>();

    // Keeps the types (schemaKlass pointer)
    protected Klass schemaKlass;

    protected IFactory factory;

    /**
     * ManagedObject is the “backing object”. It stores the data and schemaKlass pointer.
     * @param _schemaKlass the schemaKlass pointer
     */
    public ManagedObjectBase(Klass _schemaKlass, IFactory _factory, Object... _initializers) {
        this.schemaKlass = _schemaKlass;
        this.factory = _factory;

        if (this.schemaKlass.fields() != null) {

            // setup fields and properties / set default values.
            this.schemaKlass.fields().stream()
                .forEach(this::safeSetupField);

            // initialize fields with actual values.
            if (_initializers != null) {
                this.safeInitializeProps(_initializers);
            }
        }
    }

    /**
     * Wrapper to handle exceptions.
     */
    private void safeSetupField(Field _field) {
        try {
            this.setupField(_field);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupField(Field field) throws UnknownPrimitiveTypeException, InvalidFieldValueException {
        this.props.put(field.name(), null);
    }

    /**
     * Wrapper to handle exceptions.
     */
    private void safeInitializeProps(Object... initializers) {
        try {
            this.initializeProps(initializers);
        } catch (InvalidFieldValueException e) {
            e.printStackTrace();
        }
    }

    protected void initializeProps(Object... initializers) throws InvalidFieldValueException {

        // FIXME: ugly
        int i = 0;
        for (String fieldKey : this.props.keySet()) {
            if (initializers[i] != null) {
                this.props.put(fieldKey, initializers[i]);
                i++;
            } else {
                break;
            }
        }
    }

    /**
     * Get a field from the schemaKlass by it's name.
     *
     * @param _name the name of the field.
     * @return the field object.
     */
    private Field getFieldByName(String _name) {
        return (schemaKlass.fields().stream()
            .filter(field -> field.name().equals(_name))
            .findFirst())
            .orElseThrow(() -> new NoSuchFieldError("No such field named " + _name + " in " + schemaKlass.name()));
    }

    // TODO: Should I check the types manually like this?
    private void checkType(Type _fieldType, Object _fieldValue) {
        final String fieldTypeName = _fieldType.name();
        final String valueClassName = _fieldValue.getClass().getSimpleName();

        if (!fieldTypeName.equals(valueClassName)) {
            throw new IllegalArgumentException("Illegal type " + fieldTypeName + " with " + valueClassName);
        }
    }

    private void checkFieldByName(String _name) {
        Field field = getFieldByName(_name);

        if (field == null) {
            throw new NoSuchFieldError("No such field named " + _name + " in " + schemaKlass.name());
        }
    }

    protected Object _get(String _name) throws NoSuchFieldError {
        checkFieldByName(_name);
        return props.get(_name);
    }

    protected void _set(String _name, Object _value) throws NoSuchFieldError {
        Field field = getFieldByName(_name);
        checkType(field.type(), _value);

        props.put(_name, _value);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String fieldName = method.getName();
        boolean isAssignment = false;

        // FIXME
        // This is a way to execute the "attached" methods of the derived Managed Objects,
        // from the proxied objects. (e.g. point.observe()).
        //
        // In case there is already the method declared
        // (in one of the sub-klasses/sub managedObjects),
        // then invoke it dynamically, and return.
        for (Method declaredMethod : this.getClass().getMethods()) {
            if (declaredMethod.getName().equals(fieldName)) {
                method.invoke(this, args);
                return null;
            }
        }

        Object [] fieldArgs = (Object []) args[0];

        // FIXME: Is this the right way to check assignment?
        // If there is an argument then is considered as assignment.
        if (fieldArgs.length > 0) {
            isAssignment = true;
        }

        checkFieldByName(fieldName);
        Field field = getFieldByName(fieldName);

        // If it is an assignment
        if (isAssignment) {
            checkType(field.type(), fieldArgs[0]);

            _set(fieldName, fieldArgs[0]);

        // If is not assignment, return the value.
        } else {
            return _get(fieldName);
        }

        return null;
    }
}

package schemas.static_diagram.values;

import nl.cwi.managed_data_4j.IFactory;

public interface ValuesFactory extends IFactory {
    IntegerValue IntegerValue();
    BooleanValue BooleanValue();
}

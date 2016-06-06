package test_definition.schemas;

import java.util.List;
import java.util.Set;

import nl.cwi.managed_data_4j.M;
import nl.cwi.managed_data_4j.language.schema.models.definition.annotations.Contain;
import nl.cwi.managed_data_4j.language.schema.models.definition.annotations.Optional;

public interface Person extends M {

    String name(String... name);

    @Optional
    Integer age(Integer... age);

    @Optional
    List<Integer> grades(Integer... grades);

    @Contain
    List<Person> friends(Person... friend);

    @Contain
    Address address(Address... address);

    @Contain
    Set<Car> cars(Car... cars);

    Object value(Object... val);

    default String getNameWithFormat() {
        return "__" + name() + "__";
    }

    default String justReturnWhatYouGet(String message) {
        return message;
    }
}

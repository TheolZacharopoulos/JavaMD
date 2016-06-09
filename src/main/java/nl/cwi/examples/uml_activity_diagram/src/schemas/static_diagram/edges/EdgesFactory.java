package schemas.static_diagram.edges;

import nl.cwi.managed_data_4j.IFactory;

public interface EdgesFactory extends IFactory {
    ActivityEdge ActivityEdge();
    ControlFlow ControlFlow();
}

package nl.cwi.examples.runtime_state_machine;

import nl.cwi.examples.runtime_state_machine.schemas.RMachine;
import nl.cwi.examples.runtime_state_machine.schemas.RState;
import nl.cwi.examples.state_machine.StateMachineFactory;
import nl.cwi.examples.state_machine.schemas.Transition;
import nl.cwi.managed_data_4j.IFactory;

public interface RuntimeStateMachineFactory extends StateMachineFactory, IFactory {
    RState State();
    RMachine Machine();
    Transition Transition();
}

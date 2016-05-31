package nl.cwi.examples.state_machine;

import nl.cwi.examples.ccconcerns.patterns.observer.Observable;
import nl.cwi.examples.ccconcerns.patterns.observer.ObservableDataManager;
import nl.cwi.examples.state_machine.ccc.StateMachineLogging;
import nl.cwi.examples.state_machine.schemas.Machine;
import nl.cwi.examples.state_machine.schemas.State;
import nl.cwi.examples.state_machine.schemas.Transition;
import nl.cwi.managed_data_4j.framework.SchemaFactoryProvider;
import nl.cwi.managed_data_4j.language.data_manager.IDataManager;
import nl.cwi.managed_data_4j.language.schema.boot.SchemaFactory;
import nl.cwi.managed_data_4j.language.schema.load.SchemaLoader;
import nl.cwi.managed_data_4j.language.schema.models.definition.Schema;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StateMachineExampleMonitoring {

    public final static String OPEN_STATE   = "Open";
    public final static String CLOSED_STATE = "Closed";
    public final static String LOCKED_STATE = "Locked";

    public final static String OPEN_EVENT   = "open_door";
    public final static String CLOSE_EVENT  = "close_door";
    public final static String LOCK_EVENT   = "lock_door";
    public final static String UNLOCK_EVENT = "unlock_door";

    public static void main(String[] args) {
        final SchemaFactory schemaFactory = SchemaFactoryProvider.getSchemaFactory();

        // ========================================================
        // State Machine monitoring
        final Schema stateMachineSchema =
                SchemaLoader.load(schemaFactory, Machine.class, State.class, Transition.class);

        final IDataManager dataManager = new ObservableDataManager();
        final StateMachineFactory stateMachineFactory =
                dataManager.factory(StateMachineFactory.class, stateMachineSchema);

        final Machine doorStateMachine = doors(stateMachineFactory);

        // Add logging concern
        ((Observable) doorStateMachine)
                .observe(StateMachineLogging::logCurrentStateChanges);

        interpretStateMachine(doorStateMachine, new LinkedList<>(Arrays.asList(
                LOCK_EVENT,
                UNLOCK_EVENT,
                OPEN_EVENT)));
    }

    private static Machine doors(StateMachineFactory stateMachineFactory) {
        // ========================================================
        // Door State Machine definition, with state changes data manager
        final Machine doorStateMachine = stateMachineFactory.Machine();

        final State openState = stateMachineFactory.State();
        openState.name(OPEN_STATE);
        openState.machine(doorStateMachine);

        // Closed State definition
        final State closedState = stateMachineFactory.State();
        closedState.name(CLOSED_STATE);
        closedState.machine(doorStateMachine);

        // Locked State definition
        final State lockedState = stateMachineFactory.State();
        lockedState.name(LOCKED_STATE);
        lockedState.machine(doorStateMachine);

        // Close Transition
        final Transition closeTransition = stateMachineFactory.Transition();
        closeTransition.event(CLOSE_EVENT);
        closeTransition.from(openState);
        closeTransition.to(closedState);

        // Open Transition
        final Transition openTransition = stateMachineFactory.Transition();
        openTransition.event(OPEN_EVENT);
        openTransition.from(closedState);
        openTransition.to(openState);

        // Lock Transition
        final Transition lockTransition = stateMachineFactory.Transition();
        lockTransition.event(LOCK_EVENT);
        lockTransition.from(closedState);
        lockTransition.to(lockedState);

        // Unlock Transition
        final Transition unlockTransition = stateMachineFactory.Transition();
        unlockTransition.event(UNLOCK_EVENT);
        unlockTransition.from(lockedState);
        unlockTransition.to(closedState);

        // State machine start
        doorStateMachine.start(closedState);

        return doorStateMachine;
    }

    private static void interpretStateMachine(Machine stateMachine, List<String> commands) {

        stateMachine.current(stateMachine.start());

        for (String event : commands) {
            for (Transition trans : stateMachine.current().out()) {
                if (trans.event().equals(event)) {
                    stateMachine.current(trans.to());
                    break;
                }
            }
        }
    }
}

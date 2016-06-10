package nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.runtime_activity;

import nl.cwi.examples.uml_activity_diagram.helpers.FactoriesProvider;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.input.InputValue;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.runtime_nodes.RuntimeActivityFinalNode;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.runtime_nodes.RuntimeActivityNode;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.trace.Trace;
import nl.cwi.examples.uml_activity_diagram.schemas.static_diagram.nodes.ActivityNode;
import nl.cwi.examples.uml_activity_diagram.schemas.static_diagram.variables.Variable;
import nl.cwi.managed_data_4j.language.utils.MObjectUtils;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.runtime_nodes.RuntimeInitialNode;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.tokens.Token;
import nl.cwi.examples.uml_activity_diagram.schemas.static_diagram.activity.Activity;
import nl.cwi.examples.uml_activity_diagram.schemas.static_diagram.nodes.InitialNode;

import java.util.ArrayList;
import java.util.List;

public interface RuntimeActivity extends Activity {

	default void initializeTrace() {
		Trace newTrace = FactoriesProvider.getRuntimeActivityDiagramFactory().Trace();
		trace(newTrace);
	}

	default void initialize(List<InputValue> inputValues) {
		for (Variable v : locals()) {
			v.currentValue(v.initialValue());
		}
		if (inputValues != null) {
			for (InputValue v : inputValues) {
				v.getVariable().currentValue(v.getValue());
			}
		}
	}

	default void runNodes() {
		for (ActivityNode node : nodes()) {
			((RuntimeActivityNode)node).run();
		}
	}

	default void fireInitialNode() {
		InitialNode initialNode = getInitialNode();
		fireNode(initialNode);
	}

	default void fireNode(ActivityNode node) {
		System.out.println("Fire node " + node.name());
		List<Token> tokens = ((RuntimeActivityNode)node).takeOfferdTokens();
		((RuntimeActivityNode)node).fire(tokens);

		trace().executedNodes().add(node);

		// TODO: Add this?
		if (MObjectUtils.instanceOf(node, RuntimeActivityFinalNode.class)) {
			terminateNodes();
		}
	}

	default InitialNode getInitialNode() {
		for (ActivityNode node : nodes()) {
			if (MObjectUtils.instanceOf(node, RuntimeInitialNode.class)) {
				return (InitialNode)node;
			}
		}
		return null;
	}

	default List<ActivityNode> getEnabledNodes() {
		List<ActivityNode> enabledNodes = new ArrayList<ActivityNode>();
		for (ActivityNode node : nodes()) {
			if (((RuntimeActivityNode)node).isReady()) {
				enabledNodes.add(node);
			}
		}
		return enabledNodes;
	}

	default void main(List<InputValue> inputValues) {
		initialize(inputValues);
		initializeTrace();
		runActivity();
	}

	default void runActivity() {
		runNodes();

		fireInitialNode();

		List<ActivityNode> enabledNodes = getEnabledNodes();
//		while(enabledNodes.size() > 0) {
//			ActivityNode nextNode = selectNextNode(enabledNodes);
//			fireNode(nextNode);
//			enabledNodes = getEnabledNodes();
//		}

		// TODO: Change to this?
		while (enabledNodes.size() > 0) {
			for (ActivityNode nextNode : enabledNodes) {
				fireNode(nextNode);
				enabledNodes = getEnabledNodes();
			}
		}
	}

//	default ActivityNode selectNextNode(List<ActivityNode> activityNodes) {
//		return activityNodes.get(0);
//	}

	default void terminateNodes() {
		for (ActivityNode node : nodes()) {
			((RuntimeActivityNode)node).terminate();
		}
	}
}

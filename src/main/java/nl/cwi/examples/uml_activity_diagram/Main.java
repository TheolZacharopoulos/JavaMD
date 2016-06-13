package nl.cwi.examples.uml_activity_diagram;

import nl.cwi.examples.uml_activity_diagram.examples.TestPerformanceVariant3_2;
import nl.cwi.examples.uml_activity_diagram.helpers.FactoriesProvider;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.runtime_activity.RuntimeActivity;
import nl.cwi.examples.uml_activity_diagram.schemas.runtime_diagram.runtime_activity.RuntimeActivityFactory;
import nl.cwi.examples.uml_activity_diagram.schemas.static_diagram.nodes.ActivityNode;

import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        System.out.println("> Building schemas and factories");
        final RuntimeActivityFactory factory = FactoriesProvider.getRuntimeActivityDiagramFactory();

        System.out.println("> Building uml activity diagram");
        final RuntimeActivity activity = (RuntimeActivity) TestPerformanceVariant3_2.testperformance_variant3_2(factory);

        System.out.println("> Executing uml activity diagram ...");
        double start = System.currentTimeMillis();

        activity.main(Collections.emptyList());

        double end = System.currentTimeMillis();

        printTrace(activity);

        System.out.println("Total seconds: " + ((end - start)/1000)%60);
    }

    static void printTrace(RuntimeActivity a) {
        System.out.println("TRACE for activity " + a.name() + ": ");

        for (ActivityNode activityNode : a.trace().executedNodes()) {
            System.out.println(" " + activityNode.name());
        }
    }
}

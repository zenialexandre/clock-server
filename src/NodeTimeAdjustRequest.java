package src;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class NodeTimeAdjustRequest implements Runnable {

    private final Utils utils = new Utils();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final Map<Integer, LocalTime> nodesActualTimeMap = new HashMap<>();
    private final List<Integer> nodesTimeDifferences = new ArrayList<>();

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 40, 40, TimeUnit.SECONDS);
    }

    public void run() {
        try {
            if (Objects.nonNull(Main.coordinatorNodeId)) {
                executeTimeAdjustRequest();
            }
        } catch (final Exception exception) {
            utils.defaultErrorCatch(exception, "NodeTimeAdjustRequest");
        }
    }

    private void executeTimeAdjustRequest() {
        System.out.println("#######################\n");
        System.out.println("Coordinator " + Main.coordinatorNodeId
                + " is sending a current time request for the nodes.");
        getNodesActualTime();
        getTimeDifferences();
        System.out.println("#######################\n");
    }

    private void getNodesActualTime() {
        Main.nodesList.forEach(node -> {
            nodesActualTimeMap.put(node.getId(), node.getActualTime());
            System.out.println("Node " + node.getId() + " actual time: " + node.getActualTime() + "\n");
            if (Objects.deepEquals(node.getId(), Main.coordinatorNodeId)) System.out.println("And it's the coordinator.\n");
        });
    }

    private void getTimeDifferences() {
        for (Map.Entry<Integer, LocalTime> element : nodesActualTimeMap.entrySet()) {
            if (!Objects.deepEquals(element.getKey(), Main.coordinatorNodeId)) {
                nodesTimeDifferences.add(0 - element.getValue().getMinute());
            }
        }
    }

}

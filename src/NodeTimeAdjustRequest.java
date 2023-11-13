package src;

import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class NodeTimeAdjustRequest implements Runnable {

    private final Utils utils = new Utils();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final Map<Integer, LocalTime> nodesActualTimeMap = new HashMap<>();
    private final Map<Integer, Integer> nodesTimeDifferences = new HashMap<>();

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 100, 100, TimeUnit.SECONDS);
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
        getNodesTimeDifferences();
        setNodesCorrectTime();
        System.out.println("#######################\n");
    }

    private void getNodesActualTime() {
        Main.nodesList.forEach(node -> {
            nodesActualTimeMap.put(node.getId(), node.getActualTime());
            System.out.println("Node " + node.getId() + " actual time: " + node.getActualTime() + "\n");
            if (Objects.deepEquals(node.getId(), Main.coordinatorNodeId)) System.out.println("And it's the coordinator.\n");
        });
    }

    private void getNodesTimeDifferences() {
        System.out.println("Collecting time differences from nodes...\n");
        for (final Map.Entry<Integer, LocalTime> element : nodesActualTimeMap.entrySet()) {
            if (!Objects.deepEquals(element.getKey(), Main.coordinatorNodeId)) {
                nodesTimeDifferences.put(element.getKey(),
                        Integer.parseInt(String.valueOf(Objects.requireNonNull(getCoordinatorNode()).getActualTime()
                                .until(element.getValue(), ChronoUnit.MINUTES))) * -1);
                System.out.println("Node " + element.getKey() + " has difference: "
                        + nodesTimeDifferences.get(element.getKey()) + "\n");
            }
        }
    }

    private Node getCoordinatorNode() {
        return Main.nodesList.stream().filter(node ->
                Objects.deepEquals(node.getId(), Main.coordinatorNodeId)).findAny().orElse(null);
    }

    private Integer getDifferencesAverage() {
        System.out.println("Collecting differences average...\n");
        final List<Integer> differencesToSum = new ArrayList<>();

        for (final Map.Entry<Integer, Integer> element : nodesTimeDifferences.entrySet()) {
            differencesToSum.add(element.getValue());
        }
        final int differencesSum = differencesToSum.stream().mapToInt(Integer::intValue).sum();
        final Integer differencesAverage = Math.abs(differencesSum / Main.nodesList.size());
        System.out.println("Differences average: " + differencesAverage + "\n");
        return differencesAverage;
    }

    private void setNodesCorrectTime() {
        System.out.println("Adjusting time between nodes...\n");
        final Integer differencesAverage = getDifferencesAverage();

        Main.nodesList.forEach(node -> {
            node.setActualTime(getActualCorrectTime(node, differencesAverage));
            System.out.println("Node " + node.getId() + " adjusted with time: " + node.getActualTime() + "\n");
        });
    }

    private LocalTime getActualCorrectTime(@NotNull final Node node, @NotNull final Integer differencesAverage) {
        if (Objects.deepEquals(node.getId(), Main.coordinatorNodeId)) {
            return node.getActualTime().plusMinutes(differencesAverage);
        } else {
            final Integer nodeTimeDifference = nodesTimeDifferences.get(node.getId());
            return node.getActualTime().plusMinutes(nodeTimeDifference + differencesAverage);
        }
    }

}

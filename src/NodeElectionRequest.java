package src;

import java.util.List;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NodeElectionRequest implements Runnable {

    private final Utils utils = new Utils();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private Boolean isElectionRunning = false;

    public NodeElectionRequest() {}

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 0, 25, TimeUnit.SECONDS);
    }

    public void run() {
        try {
            if (!Main.nodesList.isEmpty() && !getIsElectionRunning()) {
                executeElectionRequest();
            }
        } catch (final Exception exception) {
            utils.defaultErrorCatch(exception, "NodeElectionRequest");
        }
    }

    private void executeElectionRequest() {
        final Node requesterNode = utils.getNodeRandomly();

        if (Objects.isNull(Main.coordinatorNodeId)) {
            System.out.println("#######################\n");
            System.out.println("An election has started...");
            bullyElection(requesterNode);
            System.out.println("Node that initialized the election: "
                    + requesterNode.getId() + " - Node that won: " + Main.coordinatorNodeId + "\n");
            System.out.println("#######################\n");
        }
    }

    private void bullyElection(final Node requesterNode) {
        final List<Integer> nodesIdsThatResponded = new LinkedList<>();
        establishElection(requesterNode, nodesIdsThatResponded);
        selectCoordinator(nodesIdsThatResponded);
    }

    private void establishElection(final Node requesterNode, final List<Integer> nodesIdsThatResponded) {
        setIsElectionRunning(true);
        sendRequests(requesterNode, nodesIdsThatResponded);
    }

    private void sendRequests(final Node requesterNode, final List<Integer> nodesIdsThatResponded) {
        for (final Node node : Main.nodesList) {
            if (!Objects.deepEquals(node, requesterNode) && node.getId() > requesterNode.getId()) {
                System.out.println("Node " + requesterNode.getId() + " calls node "
                        + node.getId() + " for an election.");
                System.out.println("Node " + node.getId() + " responds with OK.");
                nodesIdsThatResponded.add(node.getId());
            } else if (!Objects.deepEquals(node, requesterNode) && node.getId() < requesterNode.getId()) {
                System.out.println("Node " + requesterNode.getId() + " called node " + node.getId()
                        + " for an election, but it has no responses.");
            } else if (Objects.deepEquals(node, requesterNode)) {
                System.out.println("Node " + requesterNode.getId() + " called himself for an election.");
                nodesIdsThatResponded.add(requesterNode.getId());
            }
        }
    }

    private void selectCoordinator(final List<Integer> nodesIdsThatResponded) {
        final Integer selectedNodeId = Collections.max(nodesIdsThatResponded);
        Main.coordinatorNodeId = selectedNodeId;
        System.out.println("Node " + selectedNodeId + " now is the coordinator.");
        setIsElectionRunning(false);
    }

    private void setIsElectionRunning(final Boolean isElectionRunning) {
        this.isElectionRunning = isElectionRunning;
    }

    private Boolean getIsElectionRunning() {
        return isElectionRunning;
    }

}
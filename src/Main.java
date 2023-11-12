package src;

import java.util.LinkedList;
import java.util.List;

public class Main {

    protected static Integer coordinatorNodeId;
    protected static List<Node> nodesList = new LinkedList<>();

    public static void main(String[] args) {
        startSystem();
    }

    private static void startSystem() {
        System.out.println("############# clock-server #############\n");
        final NodeFactory nodeFactory = new NodeFactory();
        final NodeElectionRequest nodeElectionRequest = new NodeElectionRequest();
        final NodeTimeAdjustRequest nodeTimeAdjustRequest = new NodeTimeAdjustRequest();
        nodeFactory.start();
        nodeElectionRequest.start();
        nodeTimeAdjustRequest.start();
    }

}

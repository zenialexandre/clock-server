package src;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NodeFactory implements Runnable {

    private final Utils utils = new Utils();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public NodeFactory() {}

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
    }

    public void run() {
        try {
            createNode();
        } catch (final Exception exception) {
            utils.defaultErrorCatch(exception, "NodeFactory");
        }
    }

    private void createNode() {
        System.out.println("#######################\n");
        System.out.println("New node being created now...");
        final Node node = new Node(getRandomizedId(), getRandomizedActualTime());
        Main.nodesList.add(node);
        System.out.println("New node created successfully: " + node.getId() + ", with time: " + node.getActualTime());
        displayNodes();
        System.out.println("#######################\n");
    }

    private Integer getRandomizedId() {
        return new SecureRandom().nextInt(1000);
    }

    private LocalTime getRandomizedActualTime() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(LocalTime.now().plusMinutes(new SecureRandom().nextInt(60)).format(dateTimeFormatter));
    }

    private void displayNodes() {
        final StringBuilder nodesDisplayed = new StringBuilder("System current nodes: ");

        for (final Node node : Main.nodesList) {
            nodesDisplayed.append(node.getId()).append(" / ");
        }
        System.out.println(nodesDisplayed + "\n");
    }

}

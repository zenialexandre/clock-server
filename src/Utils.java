package src;

import java.security.SecureRandom;

public class Utils {

    public Utils() {}

    protected void defaultErrorCatch(final Exception exception, final String currentProcess) {
        System.out.println("###############\n");
        System.out.println("ERROR: " + exception.getMessage() + ", on process: " + currentProcess);
        System.out.println("###############\n");
    }

    protected Node getNodeRandomly() {
        return Main.nodesList.get(new SecureRandom().nextInt(Main.nodesList.size()));
    }

}

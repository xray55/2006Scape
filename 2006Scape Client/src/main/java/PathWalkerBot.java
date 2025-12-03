public class PathWalkerBot extends Script {

    // Paste coordinates from Explv's Map here:
    private final int[][] PATH_TO_BANK = {
            {3233, 3230}, // Tile 1
            {3225, 3225}, // Tile 2
            {3218, 3218}  // Lumbridge Bank
    };

    private int currentNode = 0;

    @Override
    public void onStart() {
        log("Path Walker Started!");
    }

    @Override
    public int onLoop() {
        // If we reached the end, stop.
        if (currentNode >= PATH_TO_BANK.length) {
            log("Arrived at destination!");
            return -1;
        }

        int targetX = PATH_TO_BANK[currentNode][0];
        int targetY = PATH_TO_BANK[currentNode][1];

        // Check if we are close to the current target node
        if (ClientAPI.distanceTo(client, targetX, targetY) < 4) {
            log("Reached node " + currentNode + ", moving to next.");
            currentNode++;
        }
        // If we are not walking, click the next tile
        else {
            log("Walking to: " + targetX + "," + targetY);
            ClientAPI.walkTo(client, targetX, targetY);
        }

        return 1000; // Check again in 1 second
    }

    @Override
    public void onExit() {
        log("Path Walker Stopped.");
    }
}
public class Navigation {

    // Nodes: {X, Y}
    public static final int[][] TO_VARROCK = {
            {3233, 3229}, {3243, 3226}, {3253, 3226}, // Lumbridge -> Bridge
            {3260, 3236}, {3259, 3249}, {3252, 3261}, // Cows
            {3243, 3274}, {3241, 3287}, {3237, 3297}, // Road
            // ... Add more points here using PathRecorder ...
    };

    public static final int[][] TO_LUMBRIDGE_BANK = {
            {3222, 3218}, {3218, 3218} // Castle courtyard to Bank
    };

    /**
     * Smart path walker. Finds the furthest visible tile in the path and walks to it.
     * Returns TRUE if destination reached.
     */
    public static boolean walkPath(Game client, int[][] path) {
        // 1. Am I at the end?
        int lastNodeIndex = path.length - 1;
        if (ClientAPI.distanceTo(client, path[lastNodeIndex][0], path[lastNodeIndex][1]) < 4) {
            return true;
        }

        // 2. Find furthest walkable node
        for (int i = path.length - 1; i >= 0; i--) {
            int x = path[i][0];
            int y = path[i][1];

            // If this tile is on screen (roughly 14-16 tiles distance)
            if (ClientAPI.distanceTo(client, x, y) < 14) {
                ClientAPI.walkTo(client, x, y);
                return false; // We are walking, but not done yet
            }
        }

        // 3. If lost, walk to start
        ClientAPI.walkTo(client, path[0][0], path[0][1]);
        return false;
    }
}
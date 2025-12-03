public class PathRecorder extends Script {

    private int lastX = 0;
    private int lastY = 0;

    @Override
    public void onStart() {
        log("Recorder Started! Walk to your destination...");
        System.out.print("int[][] MY_PATH = { ");
    }

    @Override
    public int onLoop() {
        int currentX = ClientAPI.getX(client);
        int currentY = ClientAPI.getY(client);

        // Only log if we moved more than 5 tiles (to keep the path clean)
        if (ClientAPI.distanceTo(client, lastX, lastY) > 5) {
            System.out.print("{" + currentX + ", " + currentY + "}, ");
            lastX = currentX;
            lastY = currentY;
        }
        return 1000;
    }

    @Override
    public void onExit() {
        System.out.println(" };");
        log("Path recording finished. Check your console!");
    }
}
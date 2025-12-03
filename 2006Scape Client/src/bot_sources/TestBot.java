public class TestBot extends Script {

    // Fallback location (Lumbridge) if no NPCs are around
    private final int TARGET_X = 3222;
    private final int TARGET_Y = 3222;

    @Override
    public void onStart() {
        log("Fighter Bot Started!");
    }

    @Override
    public int onLoop() {
        // 1. Priority: Check if we are already fighting
        if (ClientAPI.inCombat(client)) {
            return 1000; // Just wait
        }

        // 2. Priority: Look for a target nearby (within 15 tiles)
        NPC target = ClientAPI.getClosestNPC(client, "Man");

        if (target != null) {
            // Calculate distance to the goblin/man
            // We need to convert the NPC's local coordinates to global to measure distance
            int npcGlobalX = (target.x >> 7) + client.baseX;
            int npcGlobalY = (target.y >> 7) + client.baseY;

            double dist = ClientAPI.distanceTo(client, npcGlobalX, npcGlobalY);

            // If he is close enough, ATTACK him instead of walking to the spot
            if (dist <= 6) {
                log("Spotting Man at distance " + (int)dist + ". Attacking!");
                ClientAPI.interactNPC(client, "Man", "Attack");
                return 3000; // Wait for run/attack
            }
        }

        // 3. Priority: If no targets are near, Walk to the "Fighting Spot"
        if (!ClientAPI.isAt(client, TARGET_X, TARGET_Y)) {
            log("No targets nearby. Walking to main spot...");
            ClientAPI.walkTo(client, TARGET_X, TARGET_Y);
            return 2500;
        }

        return 1000;
    }

    @Override
    public void onExit() {
        log("Bot Stopped.");
    }
}
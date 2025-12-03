import java.util.ArrayList;
import java.util.List;

public final class ClientAPI {

    // ==========================================
    //               POSITIONING
    // ==========================================

    public static int getX(Game client) {
        if (client.myPlayer == null) return -1;
        return (client.myPlayer.x >> 7) + client.baseX;
    }

    public static int getY(Game client) {
        if (client.myPlayer == null) return -1;
        return (client.myPlayer.y >> 7) + client.baseY;
    }

    public static boolean isAt(Game client, int x, int y) {
        return getX(client) == x && getY(client) == y;
    }

    public static double distanceTo(Game client, int targetX, int targetY) {
        int dx = getX(client) - targetX;
        int dy = getY(client) - targetY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // ==========================================
    //               NAVIGATION
    // ==========================================

    public static boolean walkTo(Game client, int targetX, int targetY) {
        int localX = targetX - client.baseX;
        int localY = targetY - client.baseY;

        if (localX < 0 || localX > 104 || localY < 0 || localY > 104) {
            System.out.println("[Bot] Destination too far.");
            return false;
        }

        client.doWalkTo(0, 0, 0, 0, client.myPlayer.smallY[0], 0, 0, localY, client.myPlayer.smallX[0], true, localX);
        client.crossX = client.saveClickX;
        client.crossY = client.saveClickY;
        client.crossType = 1;
        client.crossIndex = 0;
        return true;
    }

    public static boolean walkPath(Game client, int[][] path) {
        int lastIndex = path.length - 1;
        if (distanceTo(client, path[lastIndex][0], path[lastIndex][1]) < 4) return true;

        for (int i = lastIndex; i >= 0; i--) {
            int x = path[i][0];
            int y = path[i][1];
            if (distanceTo(client, x, y) < 14) {
                walkTo(client, x, y);
                return false;
            }
        }
        walkTo(client, path[0][0], path[0][1]);
        return false;
    }

    // ==========================================
    //               INTERACTION (NPCs)
    // ==========================================

    public static boolean interactNPC(Game client, String npcName, String action) {
        NPC target = getClosestNPC(client, npcName);
        if (target == null) return false;

        // 1. Find the action index
        if (target.desc == null || target.desc.actions == null) return false;

        int actionIndex = -1;
        for (int i = 0; i < target.desc.actions.length; i++) {
            if (target.desc.actions[i] != null && target.desc.actions[i].equalsIgnoreCase(action)) {
                actionIndex = i;
                break;
            }
        }

        if (actionIndex == -1) {
            System.out.println("[Bot] Action '" + action + "' not found on " + npcName);
            return false;
        }

        // 2. Walk to the NPC
        client.doWalkTo(2, 0, 1, 0, client.myPlayer.smallY[0], 1, 0, target.smallY[0], client.myPlayer.smallX[0], false, target.smallX[0]);

        // 3. Send the Packet (Corrected Opcodes & Methods)
        int npcIndex = -1;
        for(int i=0; i<client.npcCount; i++) {
            if(client.npcArray[client.npcIndices[i]] == target) {
                npcIndex = client.npcIndices[i];
                break;
            }
        }

        if (npcIndex != -1) {
            int opcode = 0;

            switch(actionIndex) {
                case 0: // First Option (Talk)
                    opcode = 155;
                    client.stream.createFrame(opcode);
                    client.stream.method431(npcIndex);
                    break;
                case 1: // Second Option (Attack)
                    opcode = 72;
                    client.stream.createFrame(opcode);
                    client.stream.method432(npcIndex); // <--- FIXED (Was 433)
                    break;
                case 2: // Third Option (Trade/Pickpocket)
                    opcode = 17;
                    client.stream.createFrame(opcode);
                    client.stream.method433(npcIndex); // <--- FIXED (Was 431)
                    break;
                case 3: // Fourth Option
                    opcode = 21;
                    client.stream.createFrame(opcode);
                    client.stream.writeWord(npcIndex); // <--- FIXED (Was createFrame)
                    break;
                case 4: // Fifth Option
                    opcode = 18;
                    client.stream.createFrame(opcode);
                    client.stream.method431(npcIndex);
                    break;
            }

            System.out.println("[Bot] Interaction sent: Opcode " + opcode + " Index " + npcIndex);
            return true;
        }
        return false;
    }

    public static NPC getClosestNPC(Game client, String name) {
        NPC closest = null;
        double closestDist = 9999;
        for (int i = 0; i < client.npcCount; i++) {
            NPC npc = client.npcArray[client.npcIndices[i]];
            if (npc != null && npc.desc != null && npc.desc.name.equalsIgnoreCase(name)) {
                int npcX = (npc.x >> 7) + client.baseX;
                int npcY = (npc.y >> 7) + client.baseY;
                double dist = distanceTo(client, npcX, npcY);
                if (dist < closestDist) {
                    closest = npc;
                    closestDist = dist;
                }
            }
        }
        return closest;
    }

    // ==========================================
    //          INTERACTION (Objects)
    // ==========================================

    public static int[] getClosestObject(Game client, int objectId) {
        int minDist = 9999;
        int[] result = null;
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                int id = client.worldController.method303(client.plane, x, y);
                if (id > 0) {
                    id = (id >> 14) & 0x7fff;
                    if (id == objectId) {
                        int globalX = x + client.baseX;
                        int globalY = y + client.baseY;
                        double dist = distanceTo(client, globalX, globalY);
                        if (dist < minDist) {
                            minDist = (int)dist;
                            result = new int[]{globalX, globalY};
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void interactObject(Game client, int objectId, int x, int y, int option) {
        int localX = x - client.baseX;
        int localY = y - client.baseY;

        client.doWalkTo(2, 0, 0, 0, client.myPlayer.smallY[0], 0, 0, localY, client.myPlayer.smallX[0], false, localX);

        // MATCHING SERVER PROTOCOL (ClickObject.java):
        // Option 1: Opcode 132 -> X(A), ID, Y(A)
        // Option 2: Opcode 252 -> ID(A), Y, X(A)

        if (option == 1) {
            client.stream.createFrame(132);
            client.stream.method433(localX + client.baseX); // X (Little Endian A)
            client.stream.writeWord(objectId);              // ID (Standard)
            client.stream.method432(localY + client.baseY); // Y (Little Endian)
        } else if (option == 2) {
            client.stream.createFrame(252);
            client.stream.method433(objectId);              // ID (Little Endian A)
            client.stream.method432(localY + client.baseY); // Y (Little Endian)
            client.stream.method431(localX + client.baseX); // X (Little Endian A)
        }
    }

    // ==========================================
    //           COMBAT & INVENTORY
    // ==========================================

    public static boolean inCombat(Game client) {
        if (client.myPlayer == null) return false;
        return client.myPlayer.loopCycleStatus > Game.loopCycle;
    }

    public static int getHealth(Game client) {
        return client.currentStats[3];
    }

    public static boolean isAnimating(Game client) {
        if (client.myPlayer == null) return false;
        return client.myPlayer.anim != -1;
    }

    public static boolean isInventoryFull(Game client) {
        RSInterface inv = RSInterface.interfaceCache[3214];
        if (inv == null || inv.inv == null) return false;
        int count = 0;
        for (int item : inv.inv) {
            if (item > 0) count++;
        }
        return count == 28;
    }

    public static int getInventoryCount(Game client, int itemId) {
        RSInterface inv = RSInterface.interfaceCache[3214];
        if (inv == null || inv.inv == null) return 0;
        int count = 0;
        for (int i = 0; i < inv.inv.length; i++) {
            if (inv.inv[i] == itemId + 1) {
                count += inv.invStackSizes[i];
            }
        }
        return count;
    }
}
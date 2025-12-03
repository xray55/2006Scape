import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClientAccessor {
    // Method to get an item definition by ID using reflection
    public static ItemDef getItemDefinition(int id) {
        try {
            Field itemDefsField = Client.class.getDeclaredField("itemDefs");
            itemDefsField.setAccessible(true);
            ItemDef[] itemDefs = (ItemDef[]) itemDefsField.get(null);

            if (id >= 0 && id < itemDefs.length) {
                return itemDefs[id];
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to get the local player using reflection
    public static Player getMyPlayer() {
        try {
            Field myPlayerField = Client.class.getDeclaredField("myPlayer");
            myPlayerField.setAccessible(true);
            return (Player) myPlayerField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to get a client variable using reflection
    public static int getClientVar(int index) {
        try {
            Field variousSettingsField = Client.class.getDeclaredField("variousSettings");
            variousSettingsField.setAccessible(true);
            int[] variousSettings = (int[]) variousSettingsField.get(null);

            if (index >= 0 && index < variousSettings.length) {
                return variousSettings[index];
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Method to walk to a given coordinate using reflection
    public static boolean walkTo(int x, int y) {
        try {
            Field gameInstanceField = Game.class.getDeclaredField("game");
            gameInstanceField.setAccessible(true);
            Game game = (Game) gameInstanceField.get(null);

            if (game != null) {
                Method doWalkToMethod = Game.class.getDeclaredMethod("doWalkTo", int.class, int.class);
                doWalkToMethod.setAccessible(true);
                return (boolean) doWalkToMethod.invoke(game, x, y);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
}

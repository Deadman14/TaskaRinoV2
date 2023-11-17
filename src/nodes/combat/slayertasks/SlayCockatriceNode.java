package nodes.combat.slayertasks;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import utils.ItemUtilities;
import utils.SlayerUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayCockatriceNode extends TaskNode {
    private final Area cockatriceArea = new Area(2781, 10044, 2805, 10028);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Varrock teleport", "Camelot teleport",
            ItemUtilities.currentFood));

    @Override
    public int execute() {
        Logger.log("Slay Cockatrice");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.currentFood)) {
            if (cockatriceArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonster(cockatriceArea, "Cockatrice");
            } else {
                Utilities.walkToArea(cockatriceArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay cockatrice");
    }
}

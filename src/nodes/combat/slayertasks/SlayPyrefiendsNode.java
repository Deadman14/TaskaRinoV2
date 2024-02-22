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

public class SlayPyrefiendsNode extends TaskNode {
    private final Area pyrefiendsArea = new Area(2752, 10015, 2769, 9990);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Camelot teleport", "Varrock teleport",
            ItemUtilities.getCurrentFood()));
    @Override
    public int execute() {
        Logger.log("Slay Pyrefiends");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
            if (pyrefiendsArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(pyrefiendsArea, List.of("Pyrefiend"), false, "");
            } else {
                Utilities.walkToArea(pyrefiendsArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay pyrefiends");
    }
}

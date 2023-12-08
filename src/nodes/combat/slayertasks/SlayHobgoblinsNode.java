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

public class SlayHobgoblinsNode extends TaskNode {
    private final Area hobgoblinArea = new Area(2911, 3287, 2901, 3299);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Varrock teleport", "Falador teleport",
            ItemUtilities.currentFood));

    @Override
    public int execute() {
        Logger.log("- Slay Hobgoblins -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.currentFood)) {
            if (hobgoblinArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(hobgoblinArea, "Hobgoblin", false, "");
            } else {
                Utilities.walkToArea(hobgoblinArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay hobgoblins");
    }
}

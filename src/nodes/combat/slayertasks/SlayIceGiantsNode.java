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

public class SlayIceGiantsNode extends TaskNode {
    private final Area giantArea = new Area(3053, 9563, 3068, 9577);
    private List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Falador teleport", ItemUtilities.currentFood));

    @Override
    public int execute() {
        Logger.log("- Slay Ice Giants -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.currentFood)) {
            if (giantArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(giantArea, "Ice giant");
            } else {
                Utilities.walkToArea(giantArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay ice giants");
    }
}

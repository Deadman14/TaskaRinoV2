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

public class SlayMossGiantsNode extends TaskNode {
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Varrock teleport", "Camelot teleport",
            ItemUtilities.getCurrentFood()));
    private final Area giantArea = new Area(2547, 3412, 2560, 3401);

    @Override
    public int execute() {
        Logger.log("- Slay Moss Giants -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
            if (giantArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(giantArea, List.of("Moss giant"), false, "");
            } else {
                Utilities.walkToArea(giantArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay moss giants");
    }
}

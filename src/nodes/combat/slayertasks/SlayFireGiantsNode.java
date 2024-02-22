package nodes.combat.slayertasks;

import constants.ItemNameConstants;
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

public class SlayFireGiantsNode extends TaskNode {
    private final Area fireGiantArea = new Area(2387, 9791, 2406, 9768);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Varrock teleport",
            "Camelot teleport", ItemUtilities.getCurrentFood()));

    @Override
    public int execute() {
        Logger.log("- Slay Fire Giants -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
            if (fireGiantArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(fireGiantArea, List.of("Fire giant"), false, "");
            } else {
                Utilities.walkToArea(fireGiantArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay fire giants");
    }
}

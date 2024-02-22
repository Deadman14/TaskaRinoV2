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

public class SlayHillGiantsNode extends TaskNode {
    private final Area hillGiantArea = new Area(3099, 9850, 3125, 9823);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Varrock teleport",
            ItemUtilities.getCurrentFood()));

    @Override
    public int execute() {
        Logger.log("Slay Hill Giants");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
            if (hillGiantArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(hillGiantArea, List.of("Hill Giant"), false, "");
            } else {
                Utilities.walkToArea(hillGiantArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay hill giants");
    }
}

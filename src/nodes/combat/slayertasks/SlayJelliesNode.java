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

public class SlayJelliesNode extends TaskNode {
    private final Area jellyArea = new Area(2693, 10035, 2714, 10020);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Varrock teleport",
            "Camelot teleport", ItemUtilities.getCurrentFood()));

    @Override
    public int execute() {
        Logger.log("- Slay Jellies -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
            if (jellyArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(jellyArea, List.of("Jelly"), false, "");
            } else {
                Utilities.walkToArea(jellyArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay jellies");
    }
}

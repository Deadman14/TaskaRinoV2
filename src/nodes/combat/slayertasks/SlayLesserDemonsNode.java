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

public class SlayLesserDemonsNode extends TaskNode {
    private final Area lesserDemonArea = new Area(2828, 9568, 2848, 9550);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Varrock teleport",
            "Lumbridge teleport", "Coins", ItemUtilities.getCurrentFood()));

    @Override
    public int execute() {
        Logger.log("- Slay Lesser Demons -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
            if (lesserDemonArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(lesserDemonArea, List.of("Lesser demon"), false, "");
            } else {
                Utilities.walkToArea(lesserDemonArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay lesser demons");
    }
}

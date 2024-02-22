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

public class SlayCaveCrawlersNode extends TaskNode {
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Camelot teleport",
            "Varrock teleport", ItemUtilities.getCurrentFood()));
    private final Area caveCrawlerArea = new Area(2795, 10005, 2780, 9989);

    @Override
    public int execute() {
        Logger.log("- Slay Cave Crawlers -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
            if (caveCrawlerArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(caveCrawlerArea, List.of("Cave crawler"), false, "");
            } else {
                Utilities.walkToArea(caveCrawlerArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "Antipoison");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay cave crawlers");
    }
}

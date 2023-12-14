package nodes.combat.slayertasks;

import constants.ItemNameConstants;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import utils.ItemUtilities;
import utils.SlayerUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayRockslugsNode extends TaskNode {
    private final Area rockslugArea = new Area(2787, 10024, 2811, 10010);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Camelot teleport",
            "Varrock teleport", "Bag of salt", ItemUtilities.currentFood));

    @Override
    public int execute() {
        Logger.log("- Slay Rockslugs -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.currentFood)) {
            if (rockslugArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(rockslugArea, List.of("Rockslug"), true, "Bag of salt");
            } else {
                Utilities.walkToArea(rockslugArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay rockslugs");
    }
}

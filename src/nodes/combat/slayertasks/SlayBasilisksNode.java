package nodes.combat.slayertasks;

import constants.ItemNameConstants;
import constants.NpcNameConstants;
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

public class SlayBasilisksNode extends TaskNode {
    private final Area basiliskArea = new Area(2734, 10019, 2750, 9999);

    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, ItemNameConstants.CAMELOT_TELE,
            ItemNameConstants.VARROCK_TELE, ItemUtilities.currentFood));

    @Override
    public int execute() {
        Logger.log("- Slay Basilisks -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.currentFood)) {
            if (basiliskArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(basiliskArea, List.of(NpcNameConstants.BASILISK), false, "");
            } else {
                Utilities.walkToArea(basiliskArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay basilisks");
    }

}

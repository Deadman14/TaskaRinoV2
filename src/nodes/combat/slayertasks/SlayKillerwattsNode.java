package nodes.combat.slayertasks;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import utils.ItemUtilities;
import utils.SlayerUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayKillerwattsNode extends TaskNode {
    private final Area portalArea = new Area(3104, 3369, 3112, 3357, 2);
    private final Area killerwattArea = new Area(2650, 5229, 2729, 5159, 2);

    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Varrock teleport", "Lumbridge teleport",
            ItemUtilities.currentFood));
    @Override
    public int execute() {
        Logger.log("- Slay Killerwatts -");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.currentFood)) {
            if (killerwattArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(killerwattArea, "Killerwatt", false, "");
            } else if (portalArea.contains(Players.getLocal())) {
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable())
                        Dialogues.chooseOption(2);
                    else
                        Dialogues.continueDialogue();
                } else {
                    GameObject portal = GameObjects.closest(i -> i != null && i.getName().equals("Interdimensional rift"));
                    if (portal.canReach()) {
                        if (portal.interact())
                            Sleep.sleepUntil(() -> killerwattArea.contains(Players.getLocal()) || Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Walking.walk(portal.getTile());
                    }
                }
            } else {
                Utilities.walkToArea(portalArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay killerwatts");
    }
}

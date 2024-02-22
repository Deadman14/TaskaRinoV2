package nodes.combat.slayertasks;

import constants.ItemNameConstants;
import constants.NpcNameConstants;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import utils.ItemUtilities;
import utils.SlayerUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayAnkousNode extends TaskNode {
    private final Area ankouArea = new Area(2466, 9811, 2485, 9794);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Varrock teleport",
            "Camelot teleport", ItemUtilities.getCurrentFood()));

    @Override
    public int execute() {
        Logger.log("- Slay Ankou -");

        if (Dialogues.inDialogue()) {
            if (Dialogues.areOptionsAvailable()) {
                if (Dialogues.chooseOption(2))
                    Sleep.sleepUntil(Dialogues::canContinue, Utilities.getRandomSleepTime());
            }
            else
                Dialogues.continueDialogue();
        } else {
            if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.getCurrentFood())) {
                if (ankouArea.contains(Players.getLocal())) {
                    SlayerUtilities.slayMonsterMelee(ankouArea, List.of(NpcNameConstants.ANKOU), false, "");
                } else {
                    Utilities.walkToArea(ankouArea);
                }
            } else {
                SlayerUtilities.bankForTask(reqItems, false, "");
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay ankou");
    }
}

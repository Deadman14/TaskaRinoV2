package nodes.quests.freetoplay;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImpCatcherNode extends TaskNode {
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Red bead", "Yellow bead", "Black bead", "White bead"));
    private final Area wizardArea = new Area(3101, 3165, 3114, 3154, 2);

    @Override
    public int execute() {
        Utilities.currentNode = "ImpCatcherNode";
        Logger.log("Imp Catcher");

        if (FreeQuest.IMP_CATCHER.isFinished()) {
            TaskUtilities.currentTask = "";
            ItemUtilities.buyables = new ArrayList<>();
            return Utilities.getRandomExecuteTime();
        }

        if (Inventory.containsAll(reqItems) && !BankUtilities.areItemsNoted(reqItems)) {
            if (wizardArea.contains(Players.getLocal())) {
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable())
                        Dialogues.chooseOption(1);
                    else
                        Dialogues.continueDialogue();
                } else {
                    NPC wizard = NPCs.closest(i -> i != null && i.getName().equals("Wizard Mizgog"));
                    if (wizard.canReach()) {
                        if (wizard.interact())
                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Walking.walk(wizard.getTile());
                    }
                }
            } else {
                Utilities.walkToArea(wizardArea);
            }
        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty()) {
                    if (Bank.depositAllItems())
                        Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                }

                BankUtilities.setBankMode(BankMode.ITEM);
                for (String item : reqItems) {
                    if (Bank.contains(item)) {
                        if (Bank.withdraw(item))
                            Sleep.sleepUntil(() -> Inventory.contains(item), Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(item, 1, LivePrices.getHigh(item)));
                    }
                }
            } else {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Imp Catcher");
    }
}

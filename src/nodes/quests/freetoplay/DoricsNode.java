package nodes.quests.freetoplay;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
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

public class DoricsNode extends TaskNode {
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Clay", "Copper ore", "Iron ore"));
    private final Area doricsArea = new Area(2942, 3457, 2956, 3445);

    @Override
    public int execute() {
        Utilities.currentNode = "DoricsNode";
        Logger.log("Doric's Quest");

        if (FreeQuest.DORICS_QUEST.isFinished()) {
            TaskUtilities.currentTask = "";
            ItemUtilities.buyables = new ArrayList<>();
            return Utilities.getRandomExecuteTime();
        }

        if (Inventory.containsAll(reqItems) && !BankUtilities.areItemsNoted(reqItems)) {
            if (Dialogues.inDialogue()) {
                if (Dialogues.areOptionsAvailable())
                    Dialogues.chooseOption(1);
                else
                    Dialogues.continueDialogue();
            } else {
                if (doricsArea.contains(Players.getLocal())) {
                    NPC doric = NPCs.closest(i -> i != null & i.getName().equals("Doric"));
                    if (doric.canReach()) {
                        if (doric.interact())
                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Walking.walk(doric.getTile());
                    }
                } else {
                    Utilities.walkToArea(doricsArea);
                }
            }
        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty()) {
                    if (Bank.depositAllItems())
                        Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                }

                for (String item : reqItems) {
                    int amount = setAmount(item);
                    if (Bank.contains(item) && Bank.count(item) >= amount) {
                        if (Bank.withdraw(item, amount))
                            Sleep.sleepUntil(() -> Inventory.contains(item), Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(item, amount, LivePrices.getHigh(item)));
                    }
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Doric's Quest");
    }

    private int setAmount (String item) {
        return switch (item) {
            case "Iron ore" -> 2;
            case "Copper ore" -> 4;
            case "Clay" -> 6;
            default -> 1;
        };
    }
}

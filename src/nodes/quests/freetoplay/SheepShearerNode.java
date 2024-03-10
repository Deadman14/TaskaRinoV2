package nodes.quests.freetoplay;

import constants.ItemNameConstants;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
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
import utils.TaskUtilities;
import utils.Utilities;

public class SheepShearerNode extends TaskNode {
    private Area farmerArea = new Area(3184, 3279, 3192, 3270);

    @Override
    public int execute() {
        Logger.log("Sheep Shearer");

        if (FreeQuest.SHEEP_SHEARER.isFinished()) {
            TaskUtilities.currentTask = "";
            return Utilities.getRandomExecuteTime();
        }

        if (Inventory.count(ItemNameConstants.BALL_OF_WOOL) >= 20) {
            if (farmerArea.contains(Players.getLocal())) {
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable()) {
                        Dialogues.chooseOption(1);
                    } else {
                        Dialogues.continueDialogue();
                    }
                } else {
                    NPC fred = NPCs.closest("Fred the Farmer");
                    if (fred != null) {
                        if (fred.canReach()) {
                            if (fred.interact())
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            Walking.walk(fred.getTile());
                        }
                    }
                }
            } else {
                Utilities.walkToArea(farmerArea);
            }
        } else {
            if (Bank.isOpen()) {
                if (Bank.count(ItemNameConstants.BALL_OF_WOOL) >= 20) {
                    if (Inventory.emptySlotCount() < 20) {
                        if (Bank.depositAllItems())
                            Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                    }

                    if (Bank.withdraw(ItemNameConstants.BALL_OF_WOOL, 20))
                        Sleep.sleepUntil(() -> Inventory.count(ItemNameConstants.BALL_OF_WOOL) >= 20, Utilities.getRandomSleepTime());
                } else {
                    TaskUtilities.currentTask = "";
                    return Utilities.getRandomExecuteTime();
                }
            } else {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Sheep Shearer");
    }
}
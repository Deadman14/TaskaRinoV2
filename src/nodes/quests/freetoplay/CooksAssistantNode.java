package nodes.quests.freetoplay;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
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

public class CooksAssistantNode extends TaskNode {
    private Area cookArea = new Area(3204, 3217, 3212, 3209);

    @Override
    public int execute() {
        Utilities.currentNode = "CooksAssistantNode";
        Logger.log("Cooks Assistant Quest");

        if (FreeQuest.COOKS_ASSISTANT.isFinished()) {
            TaskUtilities.currentTask = "";
            return Utilities.getRandomExecuteTime();
        }

        if (FreeQuest.COOKS_ASSISTANT.isStarted() || PlayerSettings.getConfig(29) != 1) {
            if (Inventory.contains("Egg", "Bucket of milk", "Pot of flour") && !Inventory.isFull()) {
                if (cookArea.contains(Players.getLocal())) {
                    NPC cook = NPCs.closest("Cook");
                    if (Dialogues.inDialogue()) {
                        if (Dialogues.areOptionsAvailable()) {
                            Dialogues.chooseOption(1);
                        } else {
                            Dialogues.continueDialogue();
                        }
                    } else {
                        if (cook != null) {
                            if (cook.interact())
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        }
                    }
                } else {
                    Utilities.walkToArea(cookArea);
                }
            } else {
                if (Bank.isOpen()) {
                    if (Inventory.isFull() || !Inventory.onlyContains("Egg", "Bucket of milk", "Pot of flour")) {
                        if (Bank.depositAllExcept("Egg", "Bucket of milk", "Pot of flour"))
                            Sleep.sleepUntil(() -> Inventory.onlyContains("Egg", "Bucket of milk", "Pot of flour"), Utilities.getRandomSleepTime());
                    }

                    ItemUtilities.buyables.add(new GeItem("Egg", 1, LivePrices.getHigh("Egg")));
                    ItemUtilities.buyables.add(new GeItem("Bucket of milk", 1, LivePrices.getHigh("Bucket of milk")));
                    ItemUtilities.buyables.add(new GeItem("Pot of flour", 1, LivePrices.getHigh("Pot of flour")));
                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                    BankUtilities.openBank();
                }
            }
        } else {
            if (cookArea.contains(Players.getLocal())) {
                NPC cook = NPCs.closest("Cook");
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable()) {
                        Dialogues.chooseOption(1);
                    } else {
                        Dialogues.continueDialogue();
                    }
                }

                if (cook != null && !Dialogues.inDialogue()) {
                    if (cook.interact())
                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                }
            } else {
                Utilities.walkToArea(cookArea);
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Cooks Assistant");
    }
}

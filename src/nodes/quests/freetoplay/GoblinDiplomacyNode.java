package nodes.quests.freetoplay;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
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
import org.dreambot.api.wrappers.items.Item;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;

public class GoblinDiplomacyNode extends TaskNode {
    private Area goblinArea = new Area(2951, 3512, 2959, 3506);

    @Override
    public int execute() {
        Utilities.currentNode = "GoblinDiplomacyNode";
        Logger.log("Goblin Diplomacy");

        if (FreeQuest.GOBLIN_DIPLOMACY.isFinished()) {
            TaskUtilities.currentTask = "";
            return Utilities.getRandomExecuteTime();
        }

        if (Inventory.contains("Blue goblin mail", "Orange goblin mail", "Goblin mail") && FreeQuest.GOBLIN_DIPLOMACY.isStarted() && !Inventory.get("Goblin mail").isNoted()) {
            if (Dialogues.inDialogue()) {
                if (Dialogues.areOptionsAvailable())
                    Dialogues.chooseOption(1);
                else if (Dialogues.canContinue())
                    Dialogues.continueDialogue();
            } else {
                NPC general = NPCs.closest(i -> i.getName().equals("General Bentnoze") || i.getName().equals("General Wartface"));
                if (general != null) {
                    if (general.canReach()) {
                        if (general.interact("Talk-to"))
                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                        Walking.walk(general.getTile());
                    }
                }
            }
        } else if (Inventory.containsAll("Blue goblin mail", "Orange goblin mail", "Goblin mail") && !FreeQuest.GOBLIN_DIPLOMACY.isStarted()) {
            if (goblinArea.contains(Players.getLocal())) {
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable()) {
                        if (Dialogues.chooseFirstOptionContaining("Yes."))
                            Dialogues.chooseOption(1);
                        else
                            Dialogues.chooseOption(3);
                    } else if (Dialogues.canContinue()) {
                        Dialogues.continueDialogue();
                    }
                } else {
                    NPC general = NPCs.closest(i -> i.getName().equals("General Bentnoze") || i.getName().equals("General Wartface"));
                    if (general != null) {
                        if (general.canReach()) {
                            if (general.interact("Talk-to"))
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            Walking.walk(general.getTile());
                        }
                    }
                }
            } else {
                Utilities.walkToArea(goblinArea);
            }
        } else {
            if (Inventory.contains("Goblin mail") && Inventory.contains(i -> i.getName().contains("dye"))) {
                if (Inventory.get("Goblin mail").isNoted()) {
                    if (Bank.isOpen()) {
                        if (Bank.depositAll("Goblin mail"))
                            Sleep.sleepUntil(() -> !Inventory.contains("Goblin mail"), Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())){
                        BankUtilities.openBank();
                    }
                }

                if (Inventory.contains("Blue dye") && !Inventory.get("Goblin mail").isNoted()) {
                    if (Bank.isOpen())
                        Bank.close();
                    Item dye = Inventory.get("Blue dye");
                    if (dye != null) {
                        if (dye.useOn("Goblin mail"))
                            Sleep.sleepUntil(() -> Inventory.contains("Blue Goblin mail"), Utilities.getRandomSleepTime());
                    }
                }

                if (Inventory.contains("Orange dye") && !Inventory.get("Goblin mail").isNoted()) {
                    Logger.log("nig 4");
                    if (Bank.isOpen())
                        Bank.close();
                    Item dye = Inventory.get("Orange dye");
                    if (dye != null) {
                        if (dye.useOn("Goblin mail"))
                            Sleep.sleepUntil(() -> Inventory.contains("Orange Goblin mail"), Utilities.getRandomSleepTime());
                    }
                }
            } else {
                if (Bank.isOpen()) {
                    BankUtilities.setBankMode(BankMode.ITEM);

                    if (Inventory.emptySlotCount() < 6) {
                        if (Bank.depositAllItems())
                            Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                    }

                    if (!Inventory.contains("Goblin mail") || Inventory.count("Goblin mail") < 3) {
                        if (Bank.contains("Goblin mail") && Bank.count("Goblin mail") >= 3) {
                            if (Bank.withdraw("Goblin mail", 3 - Inventory.count("Goblin mail")))
                                Sleep.sleepUntil(() -> Inventory.count("Goblin mail") == 3, Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem("Goblin mail", 3 - Bank.count("Goblin mail"), LivePrices.getHigh("Goblin mail")));
                        }
                    }

                    if (!Inventory.contains("Blue dye")) {
                        if (Bank.contains("Blue dye")) {
                            if (Bank.withdraw("Blue dye", 1))
                                Sleep.sleepUntil(() -> Inventory.contains("Blue dye"), Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem("Blue dye", 1, LivePrices.getHigh("Blue dye")));
                        }
                    }

                    if (!Inventory.contains("Orange dye")) {
                        if (Bank.contains("Orange dye")) {
                            if (Bank.withdraw("Orange dye", 1))
                                Sleep.sleepUntil(() -> Inventory.contains("Orange dye"), Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem("Orange dye", 1, LivePrices.getHigh("Orange dye")));
                        }
                    }
                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                    BankUtilities.openBank();
                }
            }
        }
        
        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Goblin Diplomacy");
    }
}

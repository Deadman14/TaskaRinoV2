package nodes.moneymaking;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import utils.BankUtilities;
import utils.TaskUtilities;
import utils.Utilities;

public class SheepShearNode extends TaskNode {
    private Area sheepArea = new Area(3192, 3275, 3211, 3257);
    private Area shearArea = new Area(3188, 3275, 3192, 3270);
    private Area loomArea = new Area(3208, 3217, 3212, 3212, 1);

    private boolean hasCheckedBankForShears = false;

    @Override
    public int execute() {
        Utilities.currentNode = "SheepShearNode";
        Logger.log("Shear Sheep");

        if (Inventory.contains("Shears")) {
            hasCheckedBankForShears = false;

            if (!Inventory.isFull()) {
                if (sheepArea.contains(Players.getLocal())) {
                    NPC sheep = NPCs.closest(s -> s != null && s.getName().equals("Sheep") && s.hasAction("Shear") && !s.hasAction("Talk-to"));
                    if (sheep.interact())
                        Sleep.sleepUntil(() -> !sheep.hasAction("Shear") || Players.getLocal().isStandingStill(), Utilities.getRandomSleepTime());
                } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                    Walking.walk(sheepArea.getRandomTile());
                }
            } else {
                if (Inventory.contains("Wool")) {
                    if (loomArea.contains(Players.getLocal())) {
                        Item wool = Inventory.get("Wool");
                        GameObject loom = GameObjects.closest("Spinning wheel");
                        if (wool != null && loom != null) {
                            if (ItemProcessing.isOpen()) {
                                if (ItemProcessing.makeAll("Ball of wool"))
                                    Sleep.sleepUntil(() -> !Inventory.contains("Wool") || Dialogues.canContinue(), 60000);
                            } else {
                                if (wool.useOn(loom)) {
                                    Sleep.sleepUntil(ItemProcessing::isOpen, Utilities.getRandomSleepTime());
                                }
                            }
                        }
                    } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                        Walking.walk(loomArea.getRandomTile());
                    }
                } else {
                    if (FreeQuest.SHEEP_SHEARER.isFinished()) {
                        if (Bank.isOpen()) {
                            if (Bank.depositAllExcept("Shears"))
                                Sleep.sleepUntil(() -> !Inventory.contains("Ball of wool"), Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            if (Bank.open())
                                Sleep.sleepUntil(Bank::isOpen, Utilities.getRandomSleepTime());
                        }
                    } else {
                        if (shearArea.contains(Players.getLocal())) {
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
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            Walking.walk(shearArea.getRandomTile());
                        }
                    }
                }
            }
        } else {
            if (hasCheckedBankForShears) {
                if (shearArea.contains(Players.getLocal())) {
                    GroundItem shears = GroundItems.closest("Shears");
                    if (shears != null) {
                        Logger.log(shears == null);
                        if (Map.canReach(shears.getTile(), true)) {
                            Logger.log("Can reach");
                            if (shears.interact())
                                Logger.log("Interact");
                                Sleep.sleepUntil(() -> Inventory.contains("Shears"), Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            Walking.walk(shears.getTile());
                        }
                    }
                } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                    Walking.walk(shearArea.getRandomTile());
                }
            } else if (Bank.isOpen()) {
                hasCheckedBankForShears = true;
                if (Bank.depositAllItems())
                    Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());

                if (Bank.contains("Shears")) {
                    BankUtilities.setBankMode(BankMode.ITEM);
                    if (Bank.withdraw("Shears"))
                        Sleep.sleepUntil(() -> Inventory.contains("Shears"), Utilities.getRandomSleepTime());
                }
            } else {
                if (Walking.shouldWalk(Calculations.random(3, 6)))
                    if (Bank.open())
                        Sleep.sleepUntil(Bank::isOpen, Utilities.getRandomSleepTime());
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Shear Sheep");
    }
}

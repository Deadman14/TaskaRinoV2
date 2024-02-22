package nodes.quests.freetoplay;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import utils.BankUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.Arrays;

public class RestlessGhostNode extends TaskNode {
    private final Area aereckArea = new Area(3236, 3216, 3248, 3204);
    private final Area urhneyArea = new Area(3143, 3178, 3152, 3172);
    private final Area graveyardArea = new Area(3237, 3203, 3252, 3190);
    private final Area altarArea = new Area(3108, 9569, 3121, 9554);

    @Override
    public int execute() {
        Utilities.currentNode = "RestlessGhostNode";
        Logger.log("Restless Ghost");

        if (Inventory.emptySlotCount() > 4) {
            if (FreeQuest.THE_RESTLESS_GHOST.isFinished()) {
                TaskUtilities.currentTask = "";
                return Utilities.getRandomExecuteTime();
            }

            if (FreeQuest.THE_RESTLESS_GHOST.isStarted()) {
                Logger.log(PlayerSettings.getConfig(107));
                switch (PlayerSettings.getConfig(107)) {
                    case 1:
                        if (urhneyArea.contains(Players.getLocal())) {
                            if (Dialogues.inDialogue()) {
                                if (Dialogues.areOptionsAvailable()) {
                                    if (Arrays.asList(Dialogues.getOptions()).contains("Father Aereck sent me to talk to you."))
                                        Dialogues.chooseOption("Father Aereck sent me to talk to you.");
                                    else
                                        Dialogues.chooseOption(1);
                                } else {
                                    Dialogues.continueDialogue();
                                }
                            } else {
                                NPC urny = NPCs.closest("Father Urhney");
                                if (urny != null) {
                                    if (urny.canReach()) {
                                        if (urny.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                                        Walking.walk(urny.getTile());
                                    }
                                }
                            }
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            Walking.walk(urhneyArea.getRandomTile());
                        }
                        break;
                    case 2:
                        if (Inventory.contains("Ghostspeak amulet") || Equipment.contains("Ghostspeak amulet")) {
                            if (graveyardArea.contains(Players.getLocal())) {
                                if (Equipment.contains("Ghostspeak amulet")) {
                                    GameObject coffin = GameObjects.closest("Coffin");
                                    if (coffin != null) {
                                        if (coffin.canReach()) {
                                            if (coffin.hasAction("Open")) {
                                                if (coffin.interact())
                                                    Sleep.sleepUntil(() -> !coffin.hasAction("Open"), Utilities.getRandomSleepTime());
                                            } else {
                                                if (Dialogues.inDialogue()) {
                                                    if (Dialogues.areOptionsAvailable())
                                                        Dialogues.chooseOption(1);
                                                    else
                                                        Dialogues.continueDialogue();
                                                } else {
                                                    NPC ghost = NPCs.closest("Restless ghost");
                                                    if (ghost != null) {
                                                        if (ghost.interact())
                                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                                    }
                                                }
                                            }
                                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                                            Walking.walk(coffin.getTile());
                                        }
                                    }
                                } else {
                                    Item amulet = Inventory.get("Ghostspeak amulet");
                                    if (amulet != null) {
                                        if (amulet.interact())
                                            Sleep.sleepUntil(() -> Equipment.contains("Ghostspeak amulet"), Utilities.getRandomSleepTime());
                                    }
                                }
                            } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                                Walking.walk(graveyardArea.getRandomTile());
                            }
                        } else {
                            if (Bank.isOpen()) {
                                if (Bank.contains("Ghostspeak amulet")) {
                                    if (Bank.withdraw("Ghostspeak amulet"))
                                        Sleep.sleepUntil(() -> Inventory.contains("Ghostspeak amulet"), Utilities.getRandomSleepTime());
                                } else {
                                    Logger.log("Wtf where's my amulet?");
                                }
                            } else {
                                BankUtilities.openBank();
                            }
                        }
                        break;
                    case 3:
                        if (altarArea.contains(Players.getLocal())) {
                            GameObject altar = GameObjects.closest("Altar");
                            if (altar != null) {
                                if (altar.canReach()) {
                                    if (altar.interact("Search"))
                                        Sleep.sleepUntil(() -> Inventory.contains("Ghost's skull"), Utilities.getRandomSleepTime());
                                } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                                    Walking.walk(altar.getTile());
                                }
                            }
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            Walking.walk(altarArea.getRandomTile());
                        }
                        break;
                    case 4:
                        if (graveyardArea.contains(Players.getLocal())) {
                            GameObject coffin = GameObjects.closest("Coffin");
                            if (coffin != null) {
                                if (coffin.canReach()) {
                                    if (coffin.hasAction("Open")) {
                                        if (coffin.interact())
                                            Sleep.sleepUntil(() -> !coffin.hasAction("Open"), Utilities.getRandomSleepTime());
                                    } else {
                                        Item skull = Inventory.get("Ghost's skull");
                                        if (skull != null) {
                                            if (skull.useOn(coffin))
                                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                        }
                                    }
                                } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                                    Walking.walk(coffin.getTile());
                                }
                            }
                        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                            Walking.walk(graveyardArea.getRandomTile());
                        }
                }
            } else {
                if (aereckArea.contains(Players.getLocal())) {
                    if (Dialogues.inDialogue()) {
                        if (Dialogues.areOptionsAvailable()) {
                            if (Arrays.asList(Dialogues.getOptions()).contains("I'm looking for a quest!"))
                                Dialogues.chooseOption("I'm looking for a quest!");
                            else
                                Dialogues.chooseOption(1);
                        } else {
                            Dialogues.continueDialogue();
                        }
                    } else {
                        NPC father = NPCs.closest("Father Aereck");
                        if (father != null) {
                            if (father.canReach()) {
                                if (father.interact())
                                    Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                            } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                                Walking.walk(father.getTile());
                            }
                        }
                    }
                } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                    Walking.walk(aereckArea.getRandomTile());
                }
            }
        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty()) {
                    if (Bank.depositAllItems())
                        Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                }
            } else {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Restless Ghost");
    }
}

package nodes.quests.freetoplay;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
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

public class RuneMysteriesNode extends TaskNode {
    private final Area dukeArea = new Area(3205, 3225, 3213, 3217, 1);
    private final Area archmageArea = new Area(3096, 9574, 3110, 9566);
    private final Area auburyArea = new Area(3249, 3404, 3255, 3398);

    @Override
    public int execute() {
        Utilities.currentNode = "RuneMysteriesNode";
        Logger.log("Rune Mysteries");

        if (FreeQuest.RUNE_MYSTERIES.isFinished()) {
            TaskUtilities.currentTask = "";
            ItemUtilities.buyables = new ArrayList<>();
            return Utilities.getRandomExecuteTime();
        }

        if (!Inventory.isFull()) {
            if (FreeQuest.RUNE_MYSTERIES.isStarted()) {
                switch (PlayerSettings.getConfig(63)) {
                    case 1:
                        if (Inventory.contains("Air talisman")) {
                            if (archmageArea.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue()) {
                                    if (Dialogues.areOptionsAvailable())
                                        Dialogues.chooseOption(1);
                                    else
                                        Dialogues.continueDialogue();
                                } else {
                                    NPC mage = NPCs.closest(i -> i != null && i.getName().equals("Archmage Sedridor"));
                                    if (mage.canReach()) {
                                        if (mage.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                        Walking.walk(mage.getTile());
                                    }
                                }
                            } else {
                                Utilities.walkToArea(archmageArea);
                            }
                        } else {
                            if (Dialogues.inDialogue()) {
                                if (Dialogues.areOptionsAvailable())
                                    Dialogues.chooseOption(1);
                                else
                                    Dialogues.continueDialogue();
                            } else {
                                NPC duke = NPCs.closest(i -> i != null && i.getName().equals("Duke Horacio"));
                                if (duke.canReach()) {
                                    if (duke.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                    Walking.walk(duke.getTile());
                                }
                            }
                        }
                        break;
                    case 2:
                        if (Dialogues.inDialogue()) {
                            if (Dialogues.areOptionsAvailable())
                                Dialogues.chooseOption(1);
                            else
                                Dialogues.continueDialogue();
                        } else {
                            NPC mage = NPCs.closest(i -> i != null && i.getName().equals("Archmage Sedridor"));
                            if (mage.canReach()) {
                                if (mage.interact())
                                    Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                Walking.walk(mage.getTile());
                            }
                        }
                        break;
                    case 3:
                        if (Inventory.contains("Research package")) {
                            if (auburyArea.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue()) {
                                    if (Dialogues.areOptionsAvailable())
                                        Dialogues.chooseOption(2);
                                    else
                                        Dialogues.continueDialogue();
                                } else {
                                    NPC aubury = NPCs.closest(i -> i != null && i.getName().equals("Aubury"));
                                    if (aubury.canReach()) {
                                        if (aubury.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                        Walking.walk(aubury.getTile());
                                    }
                                }
                            } else {
                                Utilities.walkToArea(auburyArea);
                            }
                        } else {
                            if (Dialogues.inDialogue()) {
                                if (Dialogues.areOptionsAvailable())
                                    Dialogues.chooseOption(1);
                                else
                                    Dialogues.continueDialogue();
                            } else {
                                NPC mage = NPCs.closest(i -> i != null && i.getName().equals("Archmage Sedridor"));
                                if (mage.canReach()) {
                                    if (mage.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                    Walking.walk(mage.getTile());
                                }
                            }
                        }
                        break;
                    case 4:
                        if (auburyArea.contains(Players.getLocal())) {
                            if (Dialogues.inDialogue()) {
                                Dialogues.continueDialogue();
                            } else {
                                NPC aubury = NPCs.closest(i -> i != null && i.getName().equals("Aubury"));
                                if (aubury.canReach()) {
                                    if (aubury.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                    Walking.walk(aubury.getTile());
                                }
                            }
                        } else {
                            Utilities.walkToArea(auburyArea);
                        }
                        break;
                    case 5:
                        if (Inventory.contains("Research notes")) {
                            if (archmageArea.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue()) {
                                    Dialogues.continueDialogue();
                                } else {
                                    NPC mage = NPCs.closest(i -> i != null && i.getName().equals("Archmage Sedridor"));
                                    if (mage.canReach()) {
                                        if (mage.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                        Walking.walk(mage.getTile());
                                    }
                                }
                            } else {
                                Utilities.walkToArea(archmageArea);
                            }
                        } else {
                            if (auburyArea.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue()) {
                                    Dialogues.continueDialogue();
                                } else {
                                    NPC aubury = NPCs.closest(i -> i != null && i.getName().equals("Aubury"));
                                    if (aubury.canReach()) {
                                        if (aubury.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                        Walking.walk(aubury.getTile());
                                    }
                                }
                            } else
                                Utilities.walkToArea(auburyArea);
                        }
                        break;
                }
            } else if (dukeArea.contains(Players.getLocal())) {
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable())
                        Dialogues.chooseOption(1);
                    else
                        Dialogues.continueDialogue();
                } else {
                    NPC duke = NPCs.closest(i -> i != null && i.getName().equals("Duke Horacio"));
                    if (duke.canReach()) {
                        if (duke.interact())
                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Walking.walk(duke.getTile());
                    }
                }
            } else {
                Utilities.walkToArea(dukeArea);
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
        return TaskUtilities.currentTask.equals("Rune Mysteries");
    }
}

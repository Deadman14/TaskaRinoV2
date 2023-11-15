package nodes.quests.freetoplay;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
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
import utils.BankUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.Arrays;

public class RandJNode extends TaskNode {
    private Area romeoArea = new Area(3200, 3437, 3220, 3410);
    private Area julietArea = new Area(3154, 3426, 3161, 3425, 1);
    private Area lawrenceArea = new Area(3251, 3488, 3259, 3471);
    private Area cadavaArea = new Area(3259, 3374, 3278, 3363);
    private Area apothecaryArea = new Area(3192, 3406, 3198, 3402);

    @Override
    public int execute() {
        Utilities.currentNode = "RandJNode";
        Logger.log("Romeo and Juilet");

        if (FreeQuest.ROMEO_AND_JULIET.isFinished()) {
            TaskUtilities.currentTask = "";
            return Utilities.getRandomExecuteTime();
        }

        if (!Inventory.isFull()) {
            if (FreeQuest.ROMEO_AND_JULIET.isStarted()) {
                switch (PlayerSettings.getConfig(144)) {
                    case 10 -> {
                        if (julietArea.contains(Players.getLocal())) {
                            NPC juilet = NPCs.closest("Juliet");
                            if (juilet != null) {
                                if (Dialogues.inDialogue()) {
                                    if (Dialogues.areOptionsAvailable()) {
                                        Dialogues.chooseOption(1);
                                    } else {
                                        Dialogues.continueDialogue();
                                    }
                                } else {
                                    if (juilet.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                }
                            }
                        } else {
                            if (Walking.shouldWalk(Calculations.random(3, 6))) {
                                Walking.walk(julietArea.getRandomTile());
                            }
                        }
                    }
                    case 20 -> {
                        if (romeoArea.contains(Players.getLocal())) {
                            NPC romeo = NPCs.closest("Romeo");
                            if (romeo != null) {
                                if (Dialogues.inDialogue()) {
                                    if (Dialogues.areOptionsAvailable()) {
                                        Dialogues.chooseOption(4);
                                    } else {
                                        Dialogues.continueDialogue();
                                    }
                                } else {
                                    if (romeo.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                }
                            }
                        } else {
                            if (Walking.shouldWalk(Calculations.random(3, 6)))
                                Walking.walk(romeoArea.getRandomTile());
                        }
                    }
                    case 30 -> {
                        if (lawrenceArea.contains(Players.getLocal())) {
                            NPC lawrence = NPCs.closest("Father Lawrence");
                            if (lawrence != null) {
                                if (Dialogues.inDialogue()) {
                                    Dialogues.continueDialogue();
                                } else {
                                    if (lawrence.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                }
                            }
                        } else {
                            if (Walking.shouldWalk(Calculations.random(3, 6)))
                                Walking.walk(lawrenceArea.getRandomTile());
                        }
                    }
                    case 40 -> {
                        if (Inventory.contains("Cadava berries")) {
                            if (apothecaryArea.contains(Players.getLocal())) {
                                NPC apothecary = NPCs.closest("Apothecary");
                                if (apothecary != null) {
                                    if (Dialogues.inDialogue()) {
                                        if (Dialogues.areOptionsAvailable()) {
                                            if (Arrays.asList(Dialogues.getOptions()).contains("Talk about something else."))
                                                Dialogues.chooseOption("Talk about something else.");
                                            if (Arrays.asList(Dialogues.getOptions()).contains("Talk about Romeo & Juliet."))
                                                Dialogues.chooseOption("Talk about Romeo & Juliet.");
                                        } else {
                                            Dialogues.continueDialogue();
                                        }
                                    } else {
                                        if (apothecary.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    }
                                }
                            } else {
                                if (Walking.shouldWalk(Calculations.random(3, 6)))
                                    Walking.walk(apothecaryArea.getRandomTile());
                            }
                        } else {
                            if (cadavaArea.contains(Players.getLocal())) {
                                GameObject cadavaBush = GameObjects.closest("Cadava bush");
                                if (cadavaBush != null) {
                                    if (cadavaBush.interact())
                                        Sleep.sleepUntil(() -> Inventory.contains("Cadava berries"), Utilities.getRandomSleepTime());
                                }
                            } else {
                                if (Walking.shouldWalk(Calculations.random(3, 6)))
                                    Walking.walk(cadavaArea.getRandomTile());
                            }
                        }
                    }
                    case 50 -> {
                        if (Dialogues.inDialogue() && Dialogues.canContinue())
                            Dialogues.continueDialogue();
                        if (Inventory.contains("Cadava potion")) {
                            if (julietArea.contains(Players.getLocal())) {
                                if (!Dialogues.inDialogue()) {
                                    NPC juliet = NPCs.closest("Juliet");
                                    if (juliet != null) {
                                        if (Dialogues.inDialogue()) {
                                            Dialogues.continueDialogue();
                                        } else {
                                            if (juliet.interact())
                                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                        }
                                    }
                                } else {
                                    Dialogues.continueDialogue();
                                }
                            } else {
                                if (Walking.shouldWalk(Calculations.random(3, 6)))
                                    Walking.walk(julietArea.getRandomTile());
                            }
                        } else {
                            if (apothecaryArea.contains(Players.getLocal())) {
                                NPC apothecary = NPCs.closest("Apothecary");
                                if (apothecary != null) {
                                    if (Dialogues.inDialogue()) {
                                        if (Dialogues.areOptionsAvailable()) {
                                            if (Arrays.asList(Dialogues.getOptions()).contains("Talk about something else."))
                                                Dialogues.chooseOption("Talk about something else.");
                                            if (Arrays.asList(Dialogues.getOptions()).contains("Talk about Romeo & Juliet."))
                                                Dialogues.chooseOption("Talk about Romeo & Juliet.");
                                        } else {
                                            Dialogues.continueDialogue();
                                        }
                                    } else {
                                        if (apothecary.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    }
                                }
                            } else {
                                if (Walking.shouldWalk(Calculations.random(3, 6)))
                                    Walking.walk(apothecaryArea.getRandomTile());
                            }
                        }
                    }
                    case 60 -> {
                        if (Dialogues.inDialogue() && Dialogues.canContinue())
                            Dialogues.continueDialogue();
                        if (romeoArea.contains(Players.getLocal())) {
                            NPC romeo = NPCs.closest("Romeo");
                            if (romeo != null) {
                                if (Dialogues.inDialogue()) {
                                    Dialogues.continueDialogue();
                                } else {
                                    if (romeo.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                }
                            }
                        } else {
                            if (Walking.shouldWalk(Calculations.random(3, 6)))
                                Walking.walk(romeoArea.getRandomTile());
                        }
                    }
                }

            } else {
                if (romeoArea.contains(Players.getLocal())) {
                    NPC romeo = NPCs.closest("Romeo");
                    if (romeo != null) {
                        if (Dialogues.inDialogue()) {
                            if (Dialogues.areOptionsAvailable()) {
                                Dialogues.chooseOption(1);
                            } else {
                                Dialogues.continueDialogue();
                            }
                        } else {
                            if (romeo.interact())
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        }
                    }
                } else {
                    if (Walking.shouldWalk(Calculations.random(3, 6))) {
                        Walking.walk(romeoArea.getRandomTile());
                    }
                }
            }
        } else {
            if (Bank.isOpen()) {
                if (Bank.depositAllItems())
                    Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Romeo And Juliet");
    }
}

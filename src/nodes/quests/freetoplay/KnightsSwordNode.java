package nodes.quests.freetoplay;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
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
import utils.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** CURRENTLY NOT IN USE **/
public class KnightsSwordNode extends TaskNode {
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Redberry pie", "Iron bar", EquipmentUtilities.getCurrentPickaxe(),
            ItemUtilities.currentFood));
    private final Area squireArea = new Area(2966, 3348, 2979, 3337);
    private final Area reldoArea = new Area(3207, 3497, 3216, 3490);
    private final Area thurgoArea = new Area(2995, 3148, 3002, 3141);
    private final Area portraitArea = new Area(2980, 3343, 2986, 3333, 2);
    private final Area bluriteOreArea = new Area(3044, 9578, 3060, 9564);

    @Override
    public int execute() {
        Utilities.currentNode = "KnightsSwordNode";
        Logger.log("Knights Sword");

        if (FreeQuest.THE_KNIGHTS_SWORD.isFinished()) {
            TaskUtilities.currentTask = "";
            ItemUtilities.buyables = new ArrayList<>();
            return Utilities.getRandomExecuteTime();
        }

        if (Inventory.containsAll(reqItems) || PlayerSettings.getConfig(122) >= 2) {
            if (FreeQuest.THE_KNIGHTS_SWORD.isStarted()) {
                Logger.log("Quest Stage: " + PlayerSettings.getConfig(122));
                switch (PlayerSettings.getConfig(122)) {
                    case 1:
                        if (reldoArea.contains(Players.getLocal())) {
                            if (Dialogues.inDialogue()) {
                                if (Dialogues.areOptionsAvailable())
                                    Dialogues.chooseOption(4);
                                else
                                    Dialogues.continueDialogue();
                            } else {
                                NPC reldo = NPCs.closest(i -> i != null && i.getName().equals("Reldo"));
                                if (reldo.canReach()) {
                                    if (reldo.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                    Walking.walk(reldo.getTile());
                                }
                            }
                        } else {
                            Utilities.walkToArea(reldoArea);
                        }
                        break;
                    case 2:
                        if (thurgoArea.contains(Players.getLocal())) {
                            if (Dialogues.inDialogue()) {
                                if (Dialogues.areOptionsAvailable())
                                    Dialogues.chooseOption(2);
                                else
                                    Dialogues.continueDialogue();
                            } else {
                                NPC thurgo = NPCs.closest(i -> i != null && i.getName().equals("Thurgo"));
                                if (thurgo.canReach()) {
                                    if (thurgo.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                    Walking.walk(thurgo.getTile());
                                }
                            }
                        } else {
                            Utilities.walkToArea(thurgoArea);
                        }
                        break;
                    case 3:
                        if (Dialogues.inDialogue()) {
                            if (Dialogues.areOptionsAvailable())
                                Dialogues.chooseOption(1);
                            else
                                Dialogues.continueDialogue();
                        } else {
                            NPC thurgo = NPCs.closest(i -> i != null && i.getName().equals("Thurgo"));
                            if (thurgo.canReach()) {
                                if (thurgo.interact())
                                    Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                Walking.walk(thurgo.getTile());
                            }
                        }
                        break;
                    case 4:
                        if (squireArea.contains(Players.getLocal())) {
                            if (Dialogues.inDialogue()) {
                                if (Dialogues.areOptionsAvailable()) {
                                    Dialogues.continueDialogue();
                                } else {
                                    Dialogues.continueDialogue();
                                }
                            } else {
                                NPC squire = NPCs.closest(i -> i != null && i.getName().equals("Squire"));
                                if (squire.interact())
                                    Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                            }
                        } else {
                            Utilities.walkToArea(squireArea);
                        }
                        break;
                    case 5:
                        if (Inventory.contains("Portrait")) {
                            if (thurgoArea.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue()) {
                                    if (Dialogues.areOptionsAvailable())
                                        Dialogues.chooseOption(1);
                                    else
                                        Dialogues.continueDialogue();
                                } else {
                                    NPC thurgo = NPCs.closest(i -> i != null && i.getName().equals("Thurgo"));
                                    if (thurgo.canReach()) {
                                        if (thurgo.interact())
                                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                        Walking.walk(thurgo.getTile());
                                    }
                                }
                            } else {
                                Utilities.walkToArea(thurgoArea);
                            }
                        } else {
                            if (portraitArea.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue())
                                    Dialogues.continueDialogue();
                                else {
                                    GameObject cupboard = GameObjects.closest(i -> i != null && i.getName().equals("Cupboard"));
                                    if (cupboard.canReach()) {
                                        if (cupboard.interact())
                                            Sleep.sleepUntil(() -> Dialogues.inDialogue() || cupboard.hasAction("Search"), Utilities.getRandomSleepTime());
                                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                        Walking.walk(cupboard.getTile());
                                    }
                                }
                            } else {
                                Utilities.walkToArea(portraitArea);
                            }
                        }
                        break;
                    case 6:
                        if (Inventory.contains("Blurite sword")) {
                            if (squireArea.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue()) {
                                    Dialogues.continueDialogue();
                                } else {
                                    NPC squire = NPCs.closest(i -> i != null && i.getName().equals("Squire"));
                                    if (squire.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                }
                            } else {
                                Utilities.walkToArea(squireArea);
                            }
                        } else {
                            if (Inventory.contains("Blurite ore")) {
                                if (thurgoArea.contains(Players.getLocal())) {
                                    if (Dialogues.inDialogue()) {
                                        if (Dialogues.areOptionsAvailable())
                                            Dialogues.chooseOption(1);
                                        else
                                            Dialogues.continueDialogue();
                                    } else {
                                        NPC thurgo = NPCs.closest(i -> i != null && i.getName().equals("Thurgo"));
                                        if (thurgo.canReach()) {
                                            if (thurgo.interact())
                                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                            Walking.walk(thurgo.getTile());
                                        }
                                    }
                                } else {
                                    Utilities.walkToArea(thurgoArea);
                                }
                            } else {
                                if (bluriteOreArea.contains(Players.getLocal())) {
                                    GameObject ore = GameObjects.closest(i -> i != null && i.getName().equals("Blurite rocks"));
                                    if (ore.interact())
                                        Sleep.sleepUntil(() -> Inventory.contains("Blurite ore"), Utilities.getRandomSleepTime());
                                } else {
                                    Utilities.walkToArea(bluriteOreArea);
                                }
                            }
                        }
                }
            } else {
                if (squireArea.contains(Players.getLocal())) {
                    if (Dialogues.inDialogue()) {
                        if (Dialogues.areOptionsAvailable()) {
                            if (Arrays.asList(Dialogues.getOptions()).contains("I can make a new sword if you like..."))
                                Dialogues.chooseOption(2);
                            else
                                Dialogues.chooseOption(1);
                        } else {
                            Dialogues.continueDialogue();
                        }
                    } else {
                        NPC squire = NPCs.closest(i -> i != null && i.getName().equals("Squire"));
                        if (squire.interact())
                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                    }
                } else {
                    Utilities.walkToArea(squireArea);
                }
            }
        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty()) {
                    if (Bank.depositAllItems())
                        Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                }

                BankUtilities.setBankMode(BankMode.ITEM);
                for (String i : reqItems) {
                    int amount =setAmount(i);
                    if (Bank.contains(i) && Bank.count(i) >= amount) {
                        if (Bank.withdraw(i, amount))
                            Sleep.sleepUntil(() -> Inventory.contains(i), Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(i, amount, LivePrices.getHigh(i)));
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
        return TaskUtilities.currentTask.equals("Knights Sword");
    }

    private int setAmount(String itemName) {
        if (itemName.equals("Iron bar")) return 2;

        if (itemName.equals(ItemUtilities.currentFood)) return Calculations.random(5, 7);

        return 1;
    }
}

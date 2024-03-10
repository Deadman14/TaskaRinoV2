package nodes.quests.freetoplay;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErnestTheChickenNode extends TaskNode {
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Spade", "Fish food", "Poison"));
    private final List<String> erniesItems = new ArrayList<>(Arrays.asList("Oil can", "Pressure gauge", "Rubber tube", "Key"));
    private final Area veronicaArea = new Area(3107, 3333, 3114, 3324);
    private final Area puzzleArea = new Area(3088, 9767, 3118, 9745);
    private final Area bookcaseEastArea = new Area(3097, 3361, 3099, 3357);
    private final Area bookcaseWestArea = new Area(3091, 3363, 3096, 3354);
    private final Area CDArea = new Area(3105, 9767, 3112, 9758);
    private final Area ABArea = new Area(3101, 9756, 3118, 9745);
    private final Area EFArea = new Area(3096, 9767, 3099, 9763);
    private final Area oilArea = new Area(3090, 9757, 3099, 9753);
    private final Area compostArea = new Area(3084, 3363, 3089, 3358);
    private final Area fountainArea = new Area(3084, 3338, 3092, 3331);
    private final Area rubberTubeArea = new Area(3108, 3368, 3112, 3366);
    private final Area professorArea = new Area(3108, 3370, 3112, 3362, 2);
    private String nextLever = "B1";
    private boolean reset = false;
    private boolean inPuzzleArea = false;

    @Override
    public int execute() {
        Utilities.currentNode = "ErnestTheChickenNode";
        Logger.log("Ernest The Chicken");

        if (FreeQuest.ERNEST_THE_CHICKEN.isFinished()) {
            TaskUtilities.currentTask = "";
            ItemUtilities.buyables = new ArrayList<>();
            return Utilities.getRandomExecuteTime();
        }

        if (FreeQuest.ERNEST_THE_CHICKEN.isStarted()) {
            Logger.log("Quest Stage: " + PlayerSettings.getConfig(32));
            switch (PlayerSettings.getConfig(32)) {
                case 1:
                    if (Inventory.containsAll(erniesItems)) {
                        if (professorArea.contains(Players.getLocal())) {
                            if (Dialogues.inDialogue()) {
                                if (Dialogues.areOptionsAvailable()) {
                                    if (Arrays.asList(Dialogues.getOptions()).contains("I'm looking for a guy called Ernest."))
                                        Dialogues.chooseOption(1);
                                    else
                                        Dialogues.chooseOption(2);
                                } else {
                                    Dialogues.continueDialogue();
                                }
                            } else {
                                NPC professor = NPCs.closest(i -> i != null && i.getName().equals("Professor Oddenstein"));
                                if (professor.interact())
                                    Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                            }
                        } else {
                            Utilities.walkToArea(professorArea);
                        }
                    } else if (!Inventory.contains("Oil can")) {
                        doErniesPuzzle();
                    } else if (!Inventory.contains("Key")) {
                        if (!inPuzzleArea) {
                            if (compostArea.contains(Players.getLocal())) {
                                GameObject compost = GameObjects.closest(i -> i != null && i.getName().equals("Compost heap"));
                                if (compost.interact())
                                    Sleep.sleepUntil(() -> Inventory.contains("Key"), Utilities.getRandomSleepTime());
                            } else {
                                Utilities.walkToArea(compostArea);
                            }
                        } else {
                            if (bookcaseWestArea.contains(Players.getLocal())) {
                                GameObject lever = GameObjects.closest(i -> i != null && i.getName().equals("Lever"));
                                if (lever.interact()) {
                                    Sleep.sleepUntil(() -> !bookcaseWestArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                                    inPuzzleArea = false;
                                }
                            } else {
                                if (ABArea.contains(Players.getLocal())) {
                                    GameObject ladder = GameObjects.closest(i -> i != null && i.getName().equals("Ladder"));
                                    if (ladder.interact())
                                        Sleep.sleepUntil(() -> bookcaseWestArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                                } else {
                                    Utilities.walkToArea(ABArea);
                                }
                            }
                        }
                    } else if (!Inventory.contains("Pressure gauge")) {
                        if (fountainArea.contains(Players.getLocal())) {
                            if (Inventory.contains("Poisoned fish food")) {
                                Logger.log("poison fountain");
                                GameObject fountain = GameObjects.closest(i -> i != null && i.getName().equals("Fountain"));
                                Item poison = Inventory.get(i -> i != null && i.getName().equals("Poisoned fish food"));
                                if (poison.useOn(fountain))
                                    Sleep.sleepUntil(() -> !Inventory.contains("Poisoned fish food"), Utilities.getRandomSleepTime());
                            } else if (Inventory.contains("Fish food")){
                                Logger.log("make poison");
                                if (Inventory.combine("Fish food", "Poison"))
                                    Sleep.sleepUntil(() -> Inventory.contains("Poisoned fish food"), Utilities.getRandomSleepTime());
                            } else {
                                Logger.log("Get gage");
                                if (Dialogues.inDialogue()) {
                                    Dialogues.continueDialogue();
                                } else {
                                    GameObject fountain = GameObjects.closest(i -> i != null && i.getName().equals("Fountain"));
                                    if (fountain.interact())
                                        Sleep.sleepUntil(() -> Inventory.contains("Pressure gauge") || Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                                }
                            }
                        } else {
                            Utilities.walkToArea(fountainArea);
                        }
                    } else if (!Inventory.contains("Rubber tube")) {
                        if (rubberTubeArea.contains(Players.getLocal())) {
                            GroundItem rubberTube = GroundItems.closest(i -> i != null && i.getName().equals("Rubber tube"));
                            if (rubberTube.interact())
                                Sleep.sleepUntil(() -> Inventory.contains("Rubber tube"), Utilities.getRandomSleepTime());
                        } else {
                            Utilities.walkToArea(rubberTubeArea);
                        }
                    }
                    break;
                case 2:
                    if (professorArea.contains(Players.getLocal())) {
                        if (Dialogues.inDialogue()) {
                            Dialogues.continueDialogue();
                        } else {
                            NPC professor = NPCs.closest(i -> i != null && i.getName().equals("Professor Oddenstein"));
                            if (professor.interact())
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        }
                    } else {
                        Utilities.walkToArea(professorArea);
                    }
                    break;
            }
        } else {
            if (Inventory.containsAll(reqItems) && Inventory.emptySlotCount() >= 10) {
                if (veronicaArea.contains(Players.getLocal())) {
                    if (Dialogues.inDialogue()) {
                        if (Dialogues.areOptionsAvailable())
                            Dialogues.chooseOption(1);
                        else
                            Dialogues.continueDialogue();
                    } else {
                        NPC veronica = NPCs.closest(i -> i != null && i.getName().equals("Veronica"));
                        if (veronica.canReach()) {
                            if (veronica.interact())
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                            Walking.walk(veronica.getTile());
                        }
                    }
                } else {
                    Utilities.walkToArea(veronicaArea);
                }
            } else {
                if (Bank.isOpen()) {
                    if (Inventory.emptySlotCount() < 10) {
                        if(Bank.depositAllExcept(i -> reqItems.contains(i.getName())))
                            Sleep.sleepUntil(() -> Inventory.emptySlotCount() >= 10, Utilities.getRandomSleepTime());
                    }

                    String item = reqItems.stream().filter(i -> !Inventory.contains(i)).toList().get(0);
                    if (!item.isEmpty()) {
                        if (Bank.contains(item)) {
                            if (Bank.withdraw(item, 1))
                                Sleep.sleepUntil(() -> Inventory.contains(item), Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem(item, 1, LivePrices.getHigh(item)));
                        }
                    }
                } else {
                    BankUtilities.openBank();
                }
            }
        }
        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Ernest The Chicken");
    }

    private void PullLever(String leverName, String next) {
        GameObject lever = GameObjects.closest(i -> i != null && i.getName().equals(leverName));
        Logger.log("pull lever");
        if (lever.interact()) {
            Sleep.sleep(Calculations.random(9000, 11000));
            nextLever = next;
        }
    }

    private void PullLeverInNewArea(String leverName, String next, Area area) {
        if (area.contains(Players.getLocal())) {
            PullLever(leverName, next);
        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
            if (nextLever.equals("E2") || nextLever.equals("Oil"))
                Sleep.sleep(Calculations.random(2000, 3000));

            if (!Walking.canWalk(area.getRandomTile())) {
                Logger.log("Reset Puzzle");
                reset = true;
                nextLever = "B1";
            } else {
                Walking.walk(area);
            }
        }
    }

    private void doErniesPuzzle() {
        if (puzzleArea.contains(Players.getLocal())) {
            if (!reset) {
                Logger.log(nextLever);
                switch (nextLever) {
                    case "B1":
                        PullLever("Lever B", "A1");
                        break;
                    case "A1":
                        PullLever("Lever A", "D");
                        break;
                    case "D":
                        PullLeverInNewArea("Lever D", "B2", CDArea);
                        break;
                    case "B2":
                        PullLeverInNewArea("Lever B", "A2", ABArea);
                        break;
                    case "A2":
                        PullLever("Lever A", "E1");
                        break;
                    case "E1":
                        PullLeverInNewArea("Lever E", "F", EFArea);
                        break;
                    case "F":
                        PullLever("Lever F", "C");
                        break;
                    case "C":
                        PullLeverInNewArea("Lever C", "E2", CDArea);
                        break;
                    case "E2":
                        PullLeverInNewArea("Lever E", "Oil", EFArea);
                        break;
                    case "Oil":
                        if (oilArea.contains(Players.getLocal())) {
                            GroundItem oil = GroundItems.closest(i -> i != null && i.getName().equals("Oil can"));
                            if (oil.interact()) {
                                Sleep.sleepUntil(() -> Inventory.contains("Oil can"), Utilities.getRandomSleepTime());
                                nextLever = "B1";
                                break;
                            }
                        } else {
                            Sleep.sleep(Calculations.random(2000, 3000));
                            if (!Walking.canWalk(oilArea.getRandomTile())) {
                                reset = true;
                                nextLever = "B1";
                                break;
                            } else {
                                Utilities.walkToArea(oilArea);
                            }
                        }
                        break;
                }
            } else {
                if (!EFArea.contains(Players.getLocal())) {
                    if (ABArea.contains(Players.getLocal())) {
                        GameObject ladder = GameObjects.closest(i -> i != null && i.getName().equals("Ladder"));
                        if (ladder.interact()) {
                            Sleep.sleepUntil(() -> bookcaseWestArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                            reset = false;
                        }
                    } else {
                        Utilities.walkToArea(ABArea);
                    }
                } else {
                    Logger.log("Reset too E2");
                    nextLever = "E2";
                    reset = false;
                }
            }
        } else {
            if (bookcaseEastArea.contains(Players.getLocal())) {
                GameObject bookcase = GameObjects.closest(i -> i != null && i.getName().equals("Bookcase"));
                if (bookcase.canReach()) {
                    if (bookcase.interact())
                        Sleep.sleepUntil(() -> bookcaseWestArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                    Walking.walk(bookcase.getTile());
                }
            } else if (bookcaseWestArea.contains(Players.getLocal())) {
                inPuzzleArea = true;
                GameObject ladder = GameObjects.closest(i -> i != null && i.getName().equals("Ladder"));
                if (ladder.canReach()) {
                    if (ladder.interact())
                        Sleep.sleepUntil(() -> puzzleArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                    Walking.walk(ladder.getTile());
                }
            } else {
                Utilities.walkToArea(bookcaseEastArea);
            }
        }
    }
}

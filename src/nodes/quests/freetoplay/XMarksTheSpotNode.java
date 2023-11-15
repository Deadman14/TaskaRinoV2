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
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class XMarksTheSpotNode extends TaskNode {
    private final Area veosArea = new Area(3226, 3242, 3233, 3236);
    private final Area veosPortSarimArea = new Area(3048, 3249, 3055, 3245);
    private final Tile bobsTile = new Tile(3230, 3209, 0);
    private final Tile kitchenTile = new Tile (3203, 3212, 0);
    private final Tile draynorTile = new Tile (3108, 3264, 0);
    private final Tile pigPenTile = new Tile (3077, 3260, 0);
    private final List<Integer> childWidgetIds = new ArrayList<>(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 12, 13, 16, 17, 18, 19, 20));

    @Override
    public int execute() {
        Utilities.currentNode = "XMarksTheSpotNode";
        Logger.log("X Mark's The Spot");

        if (FreeQuest.X_MARKS_THE_SPOT.isFinished()) {
            if (Inventory.contains("Antique lamp")) {
                if (Widgets.isVisible(240)) {
                    Widget skills = Widgets.getWidget(240);
                    if (skills != null) {
                        Collections.shuffle(childWidgetIds);
                        if (skills.getChild(childWidgetIds.get(0)).interact()) {
                            if (skills.getChild(26).interact())
                                Sleep.sleepUntil(() -> !Widgets.isVisible(240), Utilities.getRandomSleepTime());
                        }
                    }
                } else {
                    Item lamp = Inventory.get(i -> i != null && i.getName().equals("Antique lamp"));
                    if (lamp.interact())
                        Sleep.sleepUntil(() -> Widgets.isVisible(240), Utilities.getRandomSleepTime());
                }
            } else {
                TaskUtilities.currentTask = "";
                ItemUtilities.buyables = new ArrayList<>();
            }

            return Utilities.getRandomExecuteTime();
        }

        if (Inventory.contains("Spade")) {
            if (FreeQuest.X_MARKS_THE_SPOT.isStarted()) {
                Logger.log("Quest Stage: " + PlayerSettings.getBitValue(8063));
                switch (PlayerSettings.getBitValue(8063)) {
                    case 1:
                        if (Dialogues.inDialogue()) {
                            if (Dialogues.areOptionsAvailable())
                                Dialogues.chooseOption(1);
                            else
                                Dialogues.continueDialogue();
                        } else {
                            if (veosArea.contains(Players.getLocal())) {
                                NPC veos = NPCs.closest(i -> i != null && i.getName().equals("Veos"));
                                if (veos.canReach()) {
                                    if (veos.interact())
                                        Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                    Walking.walk(veos.getTile());
                                }
                            } else {
                                Utilities.walkToArea(veosArea);
                            }
                        }
                        break;
                    case 2:
                        if (Dialogues.inDialogue())
                            Dialogues.continueDialogue();

                        if (Players.getLocal().getTile().equals(bobsTile)) {
                            Item spade = Inventory.get(i -> i != null && i.getName().equals("Spade"));
                            if (spade.interact())
                                Sleep.sleepUntil(() -> Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                            if (Walking.getRunEnergy() > Calculations.random(20, 40) && !Walking.isRunEnabled())
                                Walking.toggleRun();

                            Walking.walk(bobsTile);
                        }
                        break;
                    case 3:
                        if (Dialogues.inDialogue())
                            Dialogues.continueDialogue();

                        if (Players.getLocal().getTile().equals(kitchenTile)) {
                            Item spade = Inventory.get(i -> i != null && i.getName().equals("Spade"));
                            if (spade.interact())
                                Sleep.sleepUntil(() -> Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                            if (Walking.getRunEnergy() > Calculations.random(20, 40) && !Walking.isRunEnabled())
                                Walking.toggleRun();

                            Walking.walk(kitchenTile);
                        }
                        break;
                    case 4:
                        if (Dialogues.inDialogue())
                            Dialogues.continueDialogue();

                        if (Players.getLocal().getTile().equals(draynorTile)) {
                            Item spade = Inventory.get(i -> i != null && i.getName().equals("Spade"));
                            if (spade.interact())
                                Sleep.sleepUntil(() -> Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                            if (Walking.getRunEnergy() > Calculations.random(20, 40) && !Walking.isRunEnabled())
                                Walking.toggleRun();

                            Walking.walk(draynorTile);
                        }
                        break;
                    case 5:
                        if (Dialogues.inDialogue())
                            Dialogues.continueDialogue();

                        if (Players.getLocal().getTile().equals(pigPenTile)) {
                            Item spade = Inventory.get(i -> i != null && i.getName().equals("Spade"));
                            if (spade.interact())
                                Sleep.sleepUntil(() -> Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                            if (Walking.getRunEnergy() > Calculations.random(20, 40) && !Walking.isRunEnabled())
                                Walking.toggleRun();

                            Walking.walk(pigPenTile);
                        }
                        break;
                    case 6:
                        if (veosPortSarimArea.contains(Players.getLocal())) {
                            Item casket = Inventory.get(i -> i != null && i.getName().equals("Ancient casket"));
                            NPC veos = NPCs.closest(i -> i != null && i.getName().equals("Veos"));
                            if (casket.useOn(veos))
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        } else {
                            Utilities.walkToArea(veosPortSarimArea);
                        }
                        break;
                    case 7:
                        if (Dialogues.inDialogue())
                            Dialogues.continueDialogue();
                        break;

                }
            } else {
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable())
                        Dialogues.chooseOption(1);
                    else
                        Dialogues.continueDialogue();
                } else {
                    if (veosArea.contains(Players.getLocal())) {
                        NPC veos = NPCs.closest(i -> i != null && i.getName().equals("Veos"));
                        if (veos.canReach()) {
                            if (veos.interact())
                                Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                            Walking.walk(veos.getTile());
                        }
                    } else {
                        Utilities.walkToArea(veosArea);
                    }
                }
            }
        } else {
            if (Bank.isOpen()) {
                if (Bank.contains("Spade")) {
                    BankUtilities.setBankMode(BankMode.ITEM);
                    if (Bank.withdraw("Spade"))
                        Sleep.sleepUntil(() -> Inventory.contains("Spade"), Utilities.getRandomSleepTime());
                } else {
                    ItemUtilities.buyables.add(new GeItem("Spade",1, LivePrices.getHigh("Spade")));
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("X Mark's The Spot");
    }

    /*   Pick Skill From Ancient Lamp Widget (ID 240) children ID's
        2 - Attack
        3 - Strength
        4 - Ranged
        5 - Magic
        6 - Defense
        7 - HitPoints
        8 - Prayer
        9 - Agility
        10 - Herblore
        11 - Thieving
        12 - Crafting
        13 - Rune Crafting
        14 - Slayer
        15 - Farming
        16 - Mining
        17 - Smithing
        18 - Fishing
        19 - Cooking
        20 - Firemaking
        21 - Woodcutting
        22 - Fletching
        23 - Construction
        24 - Hunter
        26 - Confirm
     */
}

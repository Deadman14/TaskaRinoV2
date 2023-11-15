package nodes.skilling;

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
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
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

public class FishNode extends TaskNode {
    private final Area shrimpArea = new Area(3234, 3160, 3248, 3142);
    private final Area flyFishingArea = new Area(3100, 3422, 3111, 3435);

    @Override
    public int execute() {
        Utilities.currentNode = "FishNode";
        Logger.log("Fish");

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if (!Inventory.isFull() && Inventory.contains(getCurrentFishingRod())) {
            if (Skills.getRealLevel(Skill.FISHING) > 19) {
                if (Inventory.contains(getCurrentBait()) && Inventory.count(getCurrentBait()) > 25) {
                    goFishing();
                } else {
                    if (Bank.isOpen()) {
                        BankUtilities.setBankMode(BankMode.ITEM);

                        if (Bank.contains(getCurrentBait())) {
                            if (Bank.withdraw(getCurrentBait(), 100))
                                Sleep.sleepUntil(() -> Inventory.contains(getCurrentBait()), Utilities.getRandomSleepTime());
                        } else {
                            if (BankUtilities.canBuyItem(getCurrentBait(), 2000))
                                ItemUtilities.buyables.add(new GeItem(getCurrentBait(), 2000, LivePrices.getHigh(getCurrentBait())));
                            else {
                                TaskUtilities.currentTask = "";
                                ItemUtilities.buyables = new ArrayList<>();
                                return Utilities.getRandomExecuteTime();
                            }
                        }
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        BankUtilities.openBank();
                    }
                }
            } else {
                goFishing();
            }
        } else {
            if (Bank.isOpen()) {
                if (Inventory.isFull() || (!Inventory.onlyContains(getCurrentFishingRod()) && !Inventory.isEmpty())) {
                    if (Bank.depositAllExcept(getCurrentFishingRod(), getCurrentBait()))
                        Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
                } else {
                    BankUtilities.setBankMode(BankMode.ITEM);

                    if (!Inventory.contains(getCurrentFishingRod())) {
                        if (Bank.contains(getCurrentFishingRod())) {
                            if (Bank.withdraw(getCurrentFishingRod(), 1))
                                Sleep.sleepUntil(() -> Inventory.contains(getCurrentFishingRod()), Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem(getCurrentFishingRod(), 1, LivePrices.getHigh(getCurrentFishingRod())));
                        }
                    }
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Fishing");
    }

    private void goFishing() {
        if (getCurrentFishingArea().contains(Players.getLocal())) {
            Item net = Inventory.get(getCurrentFishingRod());
            NPC spot = NPCs.closest(s -> s.getName().equals(getCurrentFishingSpotName()) && s.hasAction(getCurrentFishingAction()));

            if (net != null && spot != null && Players.getLocal().isStandingStill()) {
                if (net.useOn(spot))
                    Sleep.sleepUntil(() -> Players.getLocal().isInteracting(spot), Utilities.getRandomSleepTime());
            }
        } else {
            Utilities.walkToArea(getCurrentFishingArea());
        }
    }

    private Area getCurrentFishingArea() {
        int level = Skills.getRealLevel(Skill.FISHING);
        if (level > 19)
            return flyFishingArea;
        else
            return shrimpArea;
    }

    private String getCurrentFishingAction() {
        int level = Skills.getRealLevel(Skill.FISHING);
        if (level > 19)
            return "Bait";
        else
            return "Net";
    }

    private String getCurrentFishingSpotName() {
        int level = Skills.getRealLevel(Skill.FISHING);
        if (level > 19)
            return "Rod Fishing spot";
        else
            return "Fishing spot";
    }

    private String getCurrentFishingRod() {
        int level = Skills.getRealLevel(Skill.FISHING);
        if (level > 19)
            return "Fly fishing rod";
        else
            return "Small fishing net";
    }

    private String getCurrentBait() {
        int level = Skills.getRealLevel(Skill.FISHING);
        if (level > 19)
            return "Feather";
        else
            return "";
    }
}

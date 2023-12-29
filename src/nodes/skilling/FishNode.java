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
import utils.*;

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

        if (!Inventory.isFull() && Inventory.contains(EquipmentUtilities.getCurrentFishingRod())) {
            if (Skills.getRealLevel(Skill.FISHING) > 19) {
                if (Inventory.contains(ItemUtilities.getCurrentBait()) && Inventory.count(ItemUtilities.getCurrentBait()) > 25) {
                    goFishing();
                } else {
                    if (Bank.isOpen()) {
                        BankUtilities.setBankMode(BankMode.ITEM);

                        if (Bank.contains(ItemUtilities.getCurrentBait())) {
                            if (Bank.withdraw(ItemUtilities.getCurrentBait(), 100))
                                Sleep.sleepUntil(() -> Inventory.contains(ItemUtilities.getCurrentBait()), Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem(ItemUtilities.getCurrentBait(), 2000, LivePrices.getHigh(ItemUtilities.getCurrentBait())));
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
                if (Inventory.emptySlotCount() < 20) {
                    if (Bank.depositAllExcept(EquipmentUtilities.getCurrentFishingRod(), ItemUtilities.getCurrentBait()))
                        Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
                } else {
                    BankUtilities.setBankMode(BankMode.ITEM);

                    if (!Inventory.contains(EquipmentUtilities.getCurrentFishingRod())) {
                        if (Bank.contains(EquipmentUtilities.getCurrentFishingRod())) {
                            if (Bank.withdraw(EquipmentUtilities.getCurrentFishingRod(), 1))
                                Sleep.sleepUntil(() -> Inventory.contains(EquipmentUtilities.getCurrentFishingRod()), Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem(EquipmentUtilities.getCurrentFishingRod(), 1, LivePrices.getHigh(EquipmentUtilities.getCurrentFishingRod())));
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
            Item net = Inventory.get(EquipmentUtilities.getCurrentFishingRod());
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
}

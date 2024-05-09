package nodes.skilling;

import constants.ItemNameConstants;
import constants.TaskNameConstants;
import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.Arrays;
import java.util.List;

public class CookingNode extends TaskNode {
    private final Area rangeArea = new Area(3230, 3195, 3236, 3198);

    private String currentCookable = ItemNameConstants.RAW_SHRIMP;

    @Override
    public int execute() {
        Logger.log("Cook");

        if (Dialogues.canContinue())
            Dialogues.continueDialogue();

        if (Inventory.contains(currentCookable) && !BankUtilities.areItemsNoted(Arrays.asList(currentCookable))) {
            if (rangeArea.contains(Players.getLocal()) && Tabs.isOpen(Tab.INVENTORY)) {
                Item cookable = Inventory.get(currentCookable);
                GameObject range = GameObjects.closest("Range");
                if (range != null) {
                    if (range.canReach()) {
                        if (cookable != null) {
                            if (ItemProcessing.isOpen()) {
                                if (ItemProcessing.makeAll(currentCookable))
                                    Sleep.sleepUntil(() -> !Inventory.contains(currentCookable) || Dialogues.canContinue(), 60000);
                            } else {
                                if (cookable.useOn(range))
                                    Sleep.sleepUntil(ItemProcessing::isOpen, Utilities.getRandomSleepTime());
                            }
                        }
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Walking.walk(range.getTile());
                    }
                }
            } else {
                if (!Tabs.isOpen(Tab.INVENTORY))
                    if (Tabs.open(Tab.INVENTORY))
                        Sleep.sleepUntil(() -> Tabs.isOpen(Tab.INVENTORY), Utilities.getRandomSleepTime());

                Utilities.walkToArea(rangeArea);
            }
        } else if (Bank.isOpen()) {
            currentCookable = getCurrentCookable();

            if (!Inventory.isEmpty() && (!Inventory.contains(currentCookable) || BankUtilities.areItemsNoted(List.of(currentCookable)))) {
                if (Bank.depositAllItems())
                    Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
            }

            if (Bank.contains(currentCookable)) {
                BankUtilities.setBankMode(BankMode.ITEM);
                if (Bank.withdrawAll(currentCookable))
                    Sleep.sleepUntil(Inventory::isFull, Utilities.getRandomSleepTime());
            } else {
                TaskUtilities.endCurrentTask();
            }
        } else {
            BankUtilities.openBank();
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals(TaskNameConstants.COOK);
    }

    private String getCurrentCookable() {
        int cookingLevel = Skills.getRealLevel(Skill.COOKING);
        int fishingLevel = Skills.getRealLevel(Skill.FISHING);

        if (fishingLevel > 29 && cookingLevel > 24 && Bank.contains(ItemNameConstants.RAW_SALMON))
            return ItemNameConstants.RAW_SALMON;

        if (fishingLevel > 19 && cookingLevel > 14 && Bank.contains(ItemNameConstants.RAW_TROUT))
            return ItemNameConstants.RAW_TROUT;

        if (Bank.contains(ItemNameConstants.RAW_ANCHOVIE))
            return ItemNameConstants.RAW_ANCHOVIE;

        return ItemNameConstants.RAW_SHRIMP;
    }
}
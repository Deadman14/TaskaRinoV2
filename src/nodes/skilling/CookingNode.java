package nodes.skilling;

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
    private final Area rangeArea = new Area(3205, 3217, 3212, 3212);

    @Override
    public int execute() {
        Utilities.currentNode = "CookingNode";
        Logger.log("Cook");

        if (Dialogues.canContinue())
            Dialogues.continueDialogue();

        if (Inventory.contains(getCurrentCookable()) && !BankUtilities.areItemsNoted(Arrays.asList(getCurrentCookable()))) {
            if (rangeArea.contains(Players.getLocal()) && Tabs.isOpen(Tab.INVENTORY)) {
                Item cookable = Inventory.get(getCurrentCookable());
                GameObject range = GameObjects.closest("Cooking range");
                if (cookable != null && range != null) {
                    if (ItemProcessing.isOpen()) {
                        if (ItemProcessing.makeAll(getCurrentCookable()))
                            Sleep.sleepUntil(() -> !Inventory.contains(getCurrentCookable()) || Dialogues.canContinue(), 60000);
                    } else {
                        if (cookable.useOn(range))
                            Sleep.sleepUntil(ItemProcessing::isOpen, Utilities.getRandomSleepTime());
                    }
                }
            } else {
                if (!Tabs.isOpen(Tab.INVENTORY))
                    if (Tabs.open(Tab.INVENTORY))
                        Sleep.sleepUntil(() -> Tabs.isOpen(Tab.INVENTORY), Utilities.getRandomSleepTime());

                Utilities.walkToArea(rangeArea);
            }
        } else if (Bank.isOpen()) {
            if (!Inventory.isEmpty() && (!Inventory.contains(getCurrentCookable()) || BankUtilities.areItemsNoted(List.of(getCurrentCookable())))) {
                if (Bank.depositAllItems())
                    Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
            }

            if (Bank.contains(getCurrentCookable()) && Bank.count(getCurrentCookable()) > 27) {
                BankUtilities.setBankMode(BankMode.ITEM);
                if (Bank.withdrawAll(getCurrentCookable()))
                    Sleep.sleepUntil(Inventory::isFull, Utilities.getRandomSleepTime());
            } else {
                int amount = 1000;
                if (getCurrentCookable().equals("Raw shrims")) amount = 300;
                if (getCurrentCookable().equals("Raw trout")) amount = 650;
                ItemUtilities.buyables.add(new GeItem(getCurrentCookable(), amount, LivePrices.getHigh(getCurrentCookable())));
            }
        } else {
            BankUtilities.openBank();
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Cook");
    }

    private String getCurrentCookable() {
        int level = Skills.getRealLevel(Skill.COOKING);

        if (level > 39)
            return "Raw tuna";
        if (level > 14)
            return "Raw trout";

        return "Raw sardine";
    }
}

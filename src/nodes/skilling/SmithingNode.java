package nodes.skilling;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmithingNode extends TaskNode {
    private final Area furnaceArea = new Area(3071, 3506, 3110, 3491);

    @Override
    public int execute() {
        Logger.log("- Smithing -");

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if (furnaceArea.contains(Players.getLocal())) {
            if (Inventory.contains(getCurrentOre()) && Inventory.onlyContains(getCurrentOre(), getCurrentAction()) && !BankUtilities.areItemsNoted(Collections.singletonList(getCurrentOre()))) {
                if (ItemProcessing.isOpen()) {
                    if (ItemProcessing.makeAll(getCurrentAction()))
                        Sleep.sleepUntil(() -> (Dialogues.inDialogue() && Dialogues.canContinue()) || !Inventory.contains(getCurrentOre()), 90000);
                } else {
                    GameObject furnace = GameObjects.closest(i -> i != null && i.getName().equals("Furnace"));

                    if (furnace.interact())
                        Sleep.sleepUntil(ItemProcessing::isOpen, Utilities.getRandomSleepTime());
                }
            } else {
                if (Bank.isOpen()) {
                    if (!Inventory.isEmpty()) {
                        if (Bank.depositAllItems())
                            Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                    }

                    if (Bank.contains(getCurrentOre()) && Bank.count(getCurrentOre()) > 28) {
                        if (Bank.withdrawAll(getCurrentOre()))
                            Sleep.sleepUntil(Inventory::isFull, Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(getCurrentOre(), 1200, LivePrices.getHigh(getCurrentOre())));
                    }
                } else {
                    BankUtilities.openBank();
                }
            }
        } else {
            Utilities.walkToArea(furnaceArea);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Smith");
    }

    private String getCurrentOre() {
        //int level = Skills.getRealLevel(Skill.SMITHING);

        return "Silver ore";
    }

    private String getCurrentAction() {
        //int level = Skills.getRealLevel(Skill.SMITHING);

        return "Silver bar";
    }
}

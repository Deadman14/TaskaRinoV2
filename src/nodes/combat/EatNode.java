package nodes.combat;

import constants.ItemNameConstants;
import models.GeItem;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.Utilities;

public class EatNode extends TaskNode {

    @Override
    public int execute() {
        Logger.log("- Eat -");

        Utilities.closeInterfaces();

        if (Inventory.contains(ItemUtilities.currentFood)) {
            if (Tabs.isOpen(Tab.INVENTORY)) {
                if (Combat.getHealthPercent() < 60) {
                    int amount = Inventory.count(ItemUtilities.currentFood);
                    Item food = Inventory.get(ItemUtilities.currentFood);

                    if (food != null) {
                        if (food.interact())
                            Sleep.sleepUntil(() -> Inventory.count(ItemUtilities.currentFood) == (amount - 1), Utilities.getRandomSleepTime());
                    }
                }

                if (Combat.isPoisoned()) {
                    Item ap = Inventory.get(i -> i.getName().contains(ItemNameConstants.ANTIPOISON));
                    if (ap != null) {
                        if (ap.interact())
                            Sleep.sleepUntil(() -> !Combat.isPoisoned(), Utilities.getRandomSleepTime());
                    }
                }
            } else {
                if (Tabs.open(Tab.INVENTORY))
                    Sleep.sleepUntil(() -> Tabs.isOpen(Tab.INVENTORY), Utilities.getRandomSleepTime());
            }
        } else {
            if (Bank.isOpen()) {
                ItemUtilities.setCurrentFood();
                if (Bank.contains(ItemUtilities.currentFood) && Bank.count(ItemUtilities.currentFood) >= 10) {
                    if (Bank.withdraw(ItemUtilities.currentFood, 10))
                        Sleep.sleepUntil(() -> Inventory.contains(ItemUtilities.currentFood), Utilities.getRandomSleepTime());
                } else {
                    ItemUtilities.buyables.add(new GeItem(ItemUtilities.currentFood, 100, LivePrices.getHigh(ItemUtilities.currentFood)));
                }
            } else {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return Combat.getHealthPercent() < 60 && Inventory.contains(ItemUtilities.currentFood) || Combat.isPoisoned();
    }

    @Override
    public int priority() {
        return 7;
    }
}
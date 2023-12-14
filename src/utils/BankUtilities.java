package utils;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

import java.util.List;

public class BankUtilities {
    public static boolean canBuyItem(String item, int geAmount) {
        return Bank.get("Coins").getAmount() > (LivePrices.getHigh(item) * 2.5) * geAmount;
    }

    public static void openBank() {
        if (Players.getLocal().distance(Bank.getClosestBankLocation().getTile()) > 6) {
            Bank.open();
        } else if (Bank.open()) {
            Sleep.sleepUntil(Bank::isOpen, Utilities.getRandomSleepTime());
        }
    }

    public static boolean canBuyItemsForTask() {
        int totalPrice = 0;
        for(GeItem i : ItemUtilities.buyables) {
            totalPrice += (LivePrices.getHigh(i.getName()) * i.getAmount()) + 1;
        }

        return Bank.count("Coins") > totalPrice * 2.5;
    }

    public static boolean areItemsNoted(List<String> items) {
        for (String item : items) {
            if (Inventory.contains(item)) {
                Item noted = Inventory.get(i -> i != null && i.getName().equals(item));

                if (noted.isNoted())
                    return true;
            }
        }

        return false;
    }

    public static void setBankMode(BankMode mode) {
        if (!Bank.getWithdrawMode().equals(mode)) {
            if (Bank.setWithdrawMode(mode))
                Sleep.sleepUntil(() -> Bank.getWithdrawMode().equals(mode), Utilities.getRandomSleepTime());
        }
    }

    public static void withdrawMultiUseItems(String itemName, int amount) {
        if (Bank.contains(i -> i.getName().contains(itemName)) && Bank.count(i -> i.getName().contains(itemName)) >= amount) {
            int emptySpace = Inventory.getEmptySlots();
            int currentAmount = Inventory.count(i -> i.getName().contains(itemName));

            if (Bank.contains(itemName + "(0)") && currentAmount < amount) {
                if (Bank.withdraw(itemName + "(0)", amount - currentAmount)) {
                    Sleep.sleepUntil(() -> emptySpace > Inventory.emptySlotCount(), Utilities.getRandomSleepTime());
                    currentAmount = Inventory.count(i -> i.getName().contains(itemName));
                }
            }

            if (Bank.contains(itemName + "(1)") && currentAmount < amount) {
                if (Bank.withdraw(itemName + "(1)", amount - currentAmount)) {
                    Sleep.sleepUntil(() -> emptySpace > Inventory.emptySlotCount(), Utilities.getRandomSleepTime());
                    currentAmount = Inventory.count(i -> i.getName().contains(itemName));
                }
            }

            if (Bank.contains(itemName + "(2)") && currentAmount < amount) {
                if (Bank.withdraw(itemName + "(2)", amount - currentAmount)) {
                    Sleep.sleepUntil(() -> emptySpace > Inventory.emptySlotCount(), Utilities.getRandomSleepTime());
                    currentAmount = Inventory.count(i -> i.getName().contains(itemName));
                }
            }

            if (Bank.contains(itemName + "(3)") && currentAmount < amount) {
                if (Bank.withdraw(itemName + "(3)", amount - currentAmount)) {
                    Sleep.sleepUntil(() -> emptySpace > Inventory.emptySlotCount(), Utilities.getRandomSleepTime());
                    currentAmount = Inventory.count(i -> i.getName().contains(itemName));
                }
            }

            if (Bank.contains(itemName + "(4)") && currentAmount < amount) {
                if (Bank.withdraw(itemName + "(4)", amount - currentAmount)) {
                    Sleep.sleepUntil(() -> emptySpace > Inventory.emptySlotCount(), Utilities.getRandomSleepTime());
                    currentAmount = Inventory.count(i -> i.getName().contains(itemName));
                }
            }
        } else {
            ItemUtilities.buyables.add(new GeItem(itemName + "(4)", SlayerUtilities.getGeAmount(itemName), LivePrices.getHigh(itemName + "(4)")));
        }
    }
}
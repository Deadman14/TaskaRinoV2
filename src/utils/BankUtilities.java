package utils;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

import java.util.ArrayList;
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

    public static List<String> getListOfSellables() {
        List<String> allItems = new ArrayList<>(Bank.all().stream().map(Item::getName).toList());

        allItems.removeIf(i -> i.equals(ItemUtilities.currentFood) || SlayerUtilities.slayerItems.contains(i)
        || i.equals(EquipmentUtilities.getCurrentFullHelm()) || i.equals(EquipmentUtilities.getCurrentPlatebody())
        || i.equals(EquipmentUtilities.getCurrentPlatelegs()) || i.equals(EquipmentUtilities.getCurrentKiteshield())
        || i.equals(EquipmentUtilities.getCurrentMeleeHandslot()) || i.equals(EquipmentUtilities.getCurrentSword())
        || i.equals(EquipmentUtilities.getCurrentMeleeBoots()) || i.equals(EquipmentUtilities.getCurrentRangedHelm())
        || i.equals(EquipmentUtilities.getCurrentRangedBody()) || i.equals(EquipmentUtilities.getCurrentRangedPants())
        || i.equals(EquipmentUtilities.getCurrentRangedGloves()) || i.equals(EquipmentUtilities.getCurrentBow())
        || i.equals(EquipmentUtilities.getCurrentArrow()) || i.equals(EquipmentUtilities.getCurrentRangedOffhand())
        || i.equals(EquipmentUtilities.getCurrentMagicHelm()) || i.equals(EquipmentUtilities.getCurrentMagicBody())
        || i.equals(EquipmentUtilities.getCurrentMagicPants()) || i.equals(EquipmentUtilities.getCurrentMagicNecklace())
        || i.equals(EquipmentUtilities.getCurrentMagicWeapon()) || i.equals(EquipmentUtilities.getCurrentPickaxe())
        || i.equals(EquipmentUtilities.getCurrentAxe()) || i.equals(EquipmentUtilities.getCurrentFishingRod())
        || (!ItemUtilities.getCurrentBait().isEmpty() && i.equals(ItemUtilities.getCurrentBait())));

        return allItems;
    }
}
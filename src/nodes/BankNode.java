package nodes;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import utils.*;

import java.util.ArrayList;

public class BankNode extends TaskNode {

    @Override
    public int execute() {
        Utilities.currentNode = "BankNode";
        Logger.log("Bank");

        if (EquipmentUtilities.requiredEquipment.contains("Mirror shield")
                && !Inventory.contains("Mirror shield") && !Equipment.contains("Mirror shield")) {
            if (SlayerUtilities.hasCheckedBankForSlayerEquipment) {
                SlayerUtilities.buyItemFromSlayerMaster("Mirror shield", 5000);
            } else {
                if (Bank.isOpen()) {
                    if (Bank.contains("Mirror shield")) {
                        if (Bank.withdraw("Mirror shield"))
                            Sleep.sleepUntil(() -> Inventory.contains("Mirror shield"), Utilities.getRandomSleepTime());
                        SlayerUtilities.hasCheckedBankForSlayerEquipment = true;
                    }
                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                    BankUtilities.openBank();
                }
            }

            return Utilities.getRandomExecuteTime();
        }

        if (EquipmentUtilities.requiredEquipment.contains("Spiny helmet")
                && !Inventory.contains("Spiny helmet") && !Equipment.contains("Spiny helmet")) {
            SlayerUtilities.buyItemFromSlayerMaster("Spiny helmet", 1000);
            return Utilities.getRandomExecuteTime();
        }

        if (Bank.isOpen()) {
            BankUtilities.setBankMode(BankMode.ITEM);

            for (String i : EquipmentUtilities.requiredEquipment) {
                int amount = 1;

                if (Inventory.isFull() || Inventory.emptySlotCount() <= EquipmentUtilities.requiredEquipment.size()) {
                    if (Bank.depositAllExcept(j -> EquipmentUtilities.requiredEquipment.contains(j.getName())
                            && !j.getName().equals(ItemUtilities.currentFood)))
                        Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
                }

                if (!Inventory.contains(i) && !Equipment.contains(i)) {
                    if (Bank.contains(i)) {
                        if (i.contains("arrow")) amount = 400;
                        if (!i.equals("Mirror shield")) {
                            if (Bank.withdraw(i, amount))
                                Sleep.sleepUntil(() -> Inventory.contains(i), Utilities.getRandomSleepTime());
                        }
                    } else if (Utilities.isGeFullyOpen()) {
                        if (i.contains("arrow")) amount = 2000;
                        ItemUtilities.buyables.add(new GeItem(i, amount, LivePrices.getHigh(i)));
                    } else {
                        TaskUtilities.currentTask = "";
                        ItemUtilities.buyables = new ArrayList<>();
                        return Utilities.getRandomExecuteTime();
                    }
                }
            }

        } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
            BankUtilities.openBank();
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return !EquipmentUtilities.hasAllEquipment();
    }

    @Override
    public int priority() {
        return 3;
    }
}

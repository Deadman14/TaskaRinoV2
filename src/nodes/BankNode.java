package nodes;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import utils.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BankNode extends TaskNode {
    private final List<String> slayerEquipment = new ArrayList<>(Arrays.asList("Mirror shield", "Spiny helmet", "Insulated boots"));
    private final Predicate<String> runesPredicate = i -> !Inventory.contains(i);

    @Override
    public int execute() {
        Logger.log("- Bank -");

        List<String> slayerItemsToBuy = new ArrayList<>(EquipmentUtilities.requiredEquipment.stream()
                .filter(i -> slayerEquipment.contains(i) && !Inventory.contains(i) && !Equipment.contains(i)).toList());
        if (!slayerItemsToBuy.isEmpty()) {
            String item = slayerItemsToBuy.get(0);
            if (SlayerUtilities.hasCheckedBankForSlayerEquipment) {
                SlayerUtilities.buyItemFromSlayerMaster(item, Calculations.random(7000, 10000));
            } else {
                if (Bank.isOpen()) {
                    if (Bank.contains(item)) {
                        if (Bank.withdraw(item))
                            Sleep.sleepUntil(() -> Inventory.contains(item), Utilities.getRandomSleepTime());
                    }

                    SlayerUtilities.hasCheckedBankForSlayerEquipment = true;
                } else {
                    BankUtilities.openBank();
                }
            }

            return Utilities.getRandomExecuteTime();
        }

        if (Bank.isOpen()) {
            BankUtilities.setBankMode(BankMode.ITEM);

            if (!Equipment.isEmpty() && !EquipmentUtilities.hasAllEquipment()) {
                if (Bank.depositAllEquipment())
                    Sleep.sleepUntil(Equipment::isEmpty, Utilities.getRandomSleepTime());
            }

            if (Inventory.isFull() || Inventory.emptySlotCount() <= EquipmentUtilities.requiredEquipment.size()) {
                if (Bank.depositAllExcept(BankUtilities.depositAllExceptCombatGearFilter))
                    Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
            }

            Optional<String> itemOptional = EquipmentUtilities.requiredEquipment.stream().filter(i -> !Inventory.contains(i) && !Equipment.contains(i)).findFirst();
            if (itemOptional.isPresent()) {
                String item = itemOptional.get();
                int amount = getAmount(item, false);
                if (Bank.contains(item) && Bank.count(item) >= amount) {
                    if (Bank.withdraw(item, amount))
                        Sleep.sleepUntil(() -> Inventory.contains(item), Utilities.getRandomSleepTime());
                } else if (Utilities.isGeFullyOpen()) {
                    EquipmentUtilities.requiredEquipment.stream()
                            .filter(i -> !Bank.contains(i) && !Inventory.contains(i))
                            .forEach(i -> ItemUtilities.buyables.add(new GeItem(i, getAmount(i, true), LivePrices.getHigh(i))));
                }
            }

            if (CombatUtilities.needRunes) {
                List<String> runes = CombatUtilities.getCurrentRunes();
                if (runes.stream().anyMatch(runesPredicate)) {
                    String rune = runes.stream().filter(runesPredicate).findFirst().get();
                    int runeAmount = getRuneAmount(rune.equals("Air rune"));
                    if (Bank.contains(rune) && Bank.count(rune) > runeAmount) {
                        if (Bank.withdraw(rune, runeAmount))
                            Sleep.sleepUntil(() -> Inventory.contains(rune), Utilities.getRandomSleepTime());
                    } else {
                        CombatUtilities.getCurrentRunes().stream()
                                .filter(i -> !Bank.contains(i) && !Inventory.contains(i))
                                .forEach(i -> ItemUtilities.buyables.add(new GeItem(i, getAmount(i, true), LivePrices.getHigh(i))));
                    }
                } else {
                    CombatUtilities.needRunes = false;
                }
            }

        } else {
            BankUtilities.openBank();
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return !EquipmentUtilities.hasAllEquipment() || CombatUtilities.needRunes;
    }

    @Override
    public int priority() {
        return 3;
    }

    private int getAmount(String name, boolean isGeAmount) {
        if (name.contains("arrow") && !isGeAmount) return 400;
        if (name.contains("arrow") && isGeAmount) return 2000;

        return 1;
    }

    private int getRuneAmount(boolean isAirRune) {
        Normal spell = CombatUtilities.getCurrentSpell();
        if (isAirRune) {
            if (spell.equals(Normal.FIRE_STRIKE)) return 800;
            if (spell.equals(Normal.FIRE_BOLT)) return  1200;
            if (spell.equals(Normal.FIRE_BLAST)) return 1600;
        }

        return 400;
    }
}
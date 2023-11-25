package nodes.moneymaking;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;

public class MakeSoftclayNode extends TaskNode {
    Area clayRockArea = new Area(3178, 3379, 3184, 3374);
    Area fountainArea = new Area(3205, 3435, 3220, 3420);

    @Override
    public int execute() {
        Logger.log("- Make Soft Clay -");

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if ((Inventory.contains("Bucket") || Inventory.contains("Bucket of water"))
                && (Inventory.contains(getCurrentPickaxe()) || Equipment.contains(getCurrentPickaxe()))) {
            if (!Equipment.contains(getCurrentPickaxe()) && canEquipCurrentPickaxe()) {
                if (Inventory.interact(getCurrentPickaxe()))
                    Sleep.sleepUntil(() -> Equipment.contains(getCurrentPickaxe()), Utilities.getRandomSleepTime());
            }

            if (!Inventory.isFull()) {
                if (clayRockArea.contains(Players.getLocal())) {
                    GameObject clayRock = GameObjects.closest(rock -> rock.getName().equals("Clay rocks") && clayRockArea.contains(rock) && rock.exists());
                    if (clayRock != null && clayRock.exists()) {
                        if (clayRock.interact())
                            Sleep.sleepUntil(() -> !clayRock.exists(), Utilities.getRandomSleepTime());
                    }
                } else {
                    Utilities.walkToArea(clayRockArea);
                }
            } else {
                if (Inventory.contains("Clay")) {
                    if (Inventory.contains("Bucket of water")) {
                        org.dreambot.api.wrappers.items.Item item = Inventory.get("Bucket of water");
                        if (item != null) {
                            if (item.useOn("Clay"))
                                Sleep.sleepUntil(() -> Inventory.contains("Bucket"), Utilities.getRandomSleepTime());
                        }
                    } else {
                        if (fountainArea.contains(Players.getLocal())) {
                            org.dreambot.api.wrappers.items.Item item = Inventory.get("Bucket");
                            GameObject fountain = GameObjects.closest("Fountain");
                            if (item != null && fountain != null) {
                                if (item.useOn(fountain))
                                    Sleep.sleepUntil(() -> Inventory.contains("Bucket of water"), Utilities.getRandomSleepTime());
                            }
                        } else {
                            Utilities.walkToArea(fountainArea);
                        }
                    }
                } else {
                    if (Bank.isOpen()) {
                        Bank.depositAllExcept(item -> item.getName().contains("pickaxe") || item.getName().contains("Bucket"));
                    } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                        BankUtilities.openBank();
                    }
                }
            }
        } else {
            if (Bank.isOpen()) {
                if (Inventory.emptySlotCount() < 3 || !Inventory.onlyContains(i -> i.getName().equals(getCurrentPickaxe()) || i.getName().contains("Bucket"))) {
                    if (Bank.depositAllItems())
                        Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                }

                if (!Inventory.contains(getCurrentPickaxe()) || !Equipment.contains(getCurrentPickaxe())) {
                    BankUtilities.setBankMode(BankMode.ITEM);

                    if (Bank.contains(getCurrentPickaxe())) {
                        if (Bank.withdraw(getCurrentPickaxe(), 1))
                            Sleep.sleepUntil(() -> Inventory.contains(getCurrentPickaxe()), Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(getCurrentPickaxe(), 1, LivePrices.getHigh(getCurrentPickaxe())));
                    }
                }

                if (Bank.contains("Bucket of water")) {
                    if (Bank.withdraw("Bucket of water", 1))
                        Sleep.sleepUntil(() -> Inventory.contains("Bucket of water"), Utilities.getRandomSleepTime());
                } else if (Bank.contains("Bucket")) {
                    BankUtilities.setBankMode(BankMode.ITEM);
                    if (Bank.withdraw("Bucket", 1))
                        Sleep.sleepUntil(() -> Inventory.contains("Bucket"), Utilities.getRandomSleepTime());
                } else {
                    ItemUtilities.buyables.add(new GeItem("Bucket", 1, LivePrices.getHigh("Bucket")));
                }
            } else {
                if (Bank.open())
                    Sleep.sleepUntil(Bank::isOpen, Utilities.getRandomSleepTime());
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Make Soft Clay");
    }

    public static String getCurrentPickaxe() {
        int level = Skills.getRealLevel(Skill.MINING);
        if (level > 40)
            return "Rune pickaxe";
        if (level > 30)
            return "Adamant pickaxe";
        if (level > 20)
            return "Mithril pickaxe";
        if (level > 5)
            return "Steel pickaxe";

        return "Bronze pickaxe";
    }

    private boolean canEquipCurrentPickaxe() {
        int level = Skills.getRealLevel(Skill.ATTACK);

        if (getCurrentPickaxe().equals("Rune pickaxe") && level >= 40)
            return true;
        if (getCurrentPickaxe().equals("Adamant pickaxe") && level >= 30)
            return true;
        if (getCurrentPickaxe().equals("Mithril pickaxe") && level >= 20)
            return true;
        if (getCurrentPickaxe().equals("Steel pickaxe") && level >= 5)
            return true;

        return getCurrentPickaxe().equals("Bronze pickaxe");
    }
}

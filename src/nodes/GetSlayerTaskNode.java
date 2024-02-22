package nodes;

import constants.TaskNameConstants;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import utils.*;

public class GetSlayerTaskNode extends TaskNode {
    private boolean checkedForGem = false;
    private boolean checkedGemForTask = false;

    @Override
    public int execute() {
        Logger.log("- Get New Slayer Task -");

        if (!SlayerUtilities.currentSlayerTask.isEmpty()) {
            TaskUtilities.currentTask = "Slay " + SlayerUtilities.currentSlayerTask;
            EquipmentUtilities.setRequiredEquipment();

            if (!SlayerUtilities.getNewSlayerTaskAfterTask) {
                TaskUtilities.taskTimer = new Timer(Calculations.random(7200000, 10800000));
                TaskUtilities.taskTimer.start();
            } else {
                SlayerUtilities.getNewSlayerTaskAfterTask = false;
            }

            return Utilities.getRandomExecuteTime();
        }

        if (Inventory.contains("Enchanted gem")) {
            if (checkedGemForTask) {
                if (SlayerUtilities.getCurrentSlayerMasterArea().contains(Players.getLocal())) {
                    if (Dialogues.inDialogue()) {
                        if (Dialogues.areOptionsAvailable()) {
                            checkedGemForTask = false;
                            Dialogues.chooseOption(2);
                        } else
                            Dialogues.continueDialogue();
                    } else {
                        NPC master = NPCs.closest(i -> i != null && i.getName().equals(SlayerUtilities.getCurrentSlayerMaster()));
                        if (master.interact("Assignment"))
                            Sleep.sleepUntil(Shop::isOpen, Utilities.getRandomSleepTime());
                    }
                } else {
                    Utilities.walkToArea(SlayerUtilities.getCurrentSlayerMasterArea());
                }
            } else {
                Item gem = Inventory.get(i -> i != null && i.getName().equals("Enchanted gem"));
                if (gem.interact("Check")) {
                    Sleep.sleepUntil(() -> !SlayerUtilities.currentSlayerTask.isEmpty(), Utilities.getRandomSleepTime());
                    checkedGemForTask = true;
                }
            }
        } else if (checkedForGem) {
            if (SlayerUtilities.getCurrentSlayerMasterArea().contains(Players.getLocal())) {
                if (Shop.isOpen()) {
                    if (Shop.purchase("Enchanted gem", 1))
                        Sleep.sleepUntil(() -> Inventory.contains("Enchanted gem"), Utilities.getRandomSleepTime());
                } else {
                    NPC master = NPCs.closest(i -> i != null && i.getName().equals(SlayerUtilities.getCurrentSlayerMaster()));
                    if (master.interact("Trade"))
                        Sleep.sleepUntil(Shop::isOpen, Utilities.getRandomSleepTime());
                }
            } else {
                Utilities.walkToArea(SlayerUtilities.getCurrentSlayerMasterArea());
            }
        } else {
            if (Bank.isOpen()) {
                if (Bank.contains("Enchanted gem")) {
                    if (Bank.withdraw("Enchanted gem"))
                        Sleep.sleepUntil(() -> Inventory.contains("Enchanted gem"), Utilities.getRandomSleepTime());
                } else {
                    if (Bank.withdraw("Coins", 1))
                        Sleep.sleepUntil(() -> Inventory.contains("Coins"), Utilities.getRandomSleepTime());
                }

                checkedForGem = true;
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals(TaskNameConstants.SLAYER);
    }
}
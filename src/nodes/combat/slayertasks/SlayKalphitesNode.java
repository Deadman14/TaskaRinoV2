package nodes.combat.slayertasks;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayKalphitesNode extends TaskNode {
    private final Area kalphiteEntranceArea = new Area(3219, 3113, 3236, 3099);
    private final Area preShantyPassArea = new Area(3300, 3128, 3307, 3118);
    private final Area kalphiteWorkerArea = new Area(3483, 9507, 3514, 9530, 2);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Coins", "Enchanted gem", "Varrock teleport", ItemUtilities.currentFood));
    private boolean checkedBankForPass = false;

    @Override
    public int execute() {
        Utilities.currentNode = "SlayKalphitesNode";
        Logger.log("Slay Kalphites");

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if (!Inventory.isFull() && Inventory.containsAll(reqItems)
                && Inventory.count("Coins") >= 1000 && Inventory.count(ItemUtilities.currentFood) >= 1) {
            if (kalphiteWorkerArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonster(kalphiteWorkerArea, "Kalphite Worker");
            } else {
                if (Inventory.containsAll(Arrays.asList("Shantay pass", "Rope")) || checkedBankForPass) {
                    if (Inventory.contains("Shantay pass") || checkedBankForPass) {
                        if (kalphiteEntranceArea.contains(Players.getLocal())) {
                            GameObject tunnel = GameObjects.closest(i -> i != null && i.getName().equals("Tunnel entrance"));
                            Item rope = Inventory.get(i -> i != null && i.getName().equals("Rope"));
                            if (rope.useOn(tunnel))
                                Sleep.sleepUntil(() -> kalphiteWorkerArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                        } else {
                            Utilities.walkToArea(kalphiteEntranceArea);
                        }
                    } else {
                        if (preShantyPassArea.contains(Players.getLocal())) {
                            SlayerUtilities.buyShantayPass();
                        } else {
                            Utilities.walkToArea(preShantyPassArea);
                        }
                    }
                } else {
                    if (Bank.isOpen()) {
                        if (!Inventory.contains("Rope")) {
                            BankUtilities.setBankMode(BankMode.ITEM);
                            if (Bank.withdraw("Rope"))
                                Sleep.sleepUntil(() -> Inventory.contains("Rope"), Utilities.getRandomSleepTime());
                        }

                        if (!Inventory.contains("Shantay pass")) {
                            if (Bank.contains("Shantay pass")) {
                                if (Bank.withdraw("Shantay pass"))
                                    Sleep.sleepUntil(() -> Inventory.contains("Shantay pass"), Utilities.getRandomSleepTime());
                            }
                        }

                        checkedBankForPass = true;
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        BankUtilities.openBank();
                    }
                }
            }

        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty()) {
                    if (Bank.depositAllExcept(i -> reqItems.contains(i.getName())))
                        Sleep.sleepUntil(() -> Inventory.onlyContains( i -> reqItems.contains(i.getName())), Utilities.getRandomSleepTime());
                }

                if (!Inventory.containsAll(reqItems) || Inventory.count("Coins") < 1000
                        || Inventory.count(ItemUtilities.currentFood) < 10) {
                    for (String item : reqItems) {
                        int amount = 1;
                        if (!item.equals("Enchanted gem")) {
                            if (item.equals("Coins"))
                                amount = 2000;
                            else if (item.contains("teleport"))
                                amount = 2;
                        }

                        if (!Inventory.contains("Enchanted gem")) {
                            if (Bank.withdraw("Enchanted gem"))
                                Sleep.sleepUntil(() -> Inventory.contains("Enchanted gem"), Utilities.getRandomSleepTime());
                        }

                        if (Bank.contains(item) && Bank.count(item) > amount) {
                            if (Bank.withdraw(item, amount - Inventory.count(item)))
                                Sleep.sleepUntil(() -> Inventory.count(item) >= (item.equals("Coins") ? 50 : 10), Utilities.getRandomSleepTime());
                        } else {
                            if (item.contains("teleport")) amount = 10;

                            if (!item.equals("Enchanted gem"))
                                ItemUtilities.buyables.add(new GeItem(item, amount * 5, LivePrices.getHigh(item)));
                        }
                    }
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        Logger.log(TaskUtilities.currentTask);
        return TaskUtilities.currentTask.equals("Slay kalphite");
    }
}

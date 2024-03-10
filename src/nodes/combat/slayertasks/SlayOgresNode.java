package nodes.combat.slayertasks;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayOgresNode extends TaskNode {
    private final Area ogreArea = new Area(2514, 2992, 2604, 2956);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Feldip hills teleport", "Varrock teleport",
            ItemUtilities.currentFood));

    @Override
    public int execute() {
        Utilities.currentNode = "SlayOgresNode";
        Logger.log("- Slay Ogres -");

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.count(ItemUtilities.currentFood) > 2) {
            if (ogreArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(ogreArea, List.of("Ogre"), false, "");
            } else {
                Utilities.walkToArea(ogreArea);
            }
        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty() && (Inventory.isFull() || !Inventory.onlyContains(i -> reqItems.contains(i.getName())))) {
                    if (Bank.depositAllExcept(i -> reqItems.contains(i.getName())))
                        Sleep.sleepUntil(() -> Inventory.onlyContains(i -> reqItems.contains(i.getName())), Utilities.getRandomSleepTime());
                }

                BankUtilities.setBankMode(BankMode.ITEM);
                for (String item : reqItems) {
                    int amount = !item.equals("Enchanted gem") ? 15 : 1;
                    if (item.contains("teleport")) amount = 2;

                    if (Bank.contains(item) && Bank.count(item) >= amount) {
                        if (Bank.withdraw(item, amount - Inventory.count(item)))
                            Sleep.sleepUntil(() -> Inventory.count(item) >= (!item.equals("Enchanted gem") && !item.contains("teleport") ? 15 : 1),
                                    Utilities.getRandomSleepTime());
                    } else {
                        if (item.equals(ItemUtilities.currentFood)) amount = 50;
                        if (item.contains("teleport")) amount = 10;

                        if (!item.equals("Enchanted gem"))
                            ItemUtilities.buyables.add(new GeItem(item, amount * 5, LivePrices.getHigh(item)));
                    }
                }
            } else {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay ogres");
    }
}

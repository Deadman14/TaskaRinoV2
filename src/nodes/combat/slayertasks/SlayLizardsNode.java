package nodes.combat.slayertasks;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayLizardsNode extends TaskNode {
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Varrock teleport", "Lumbridge teleport",
            "Coins", ItemUtilities.currentFood, "Ice cooler"));
    private final Area lizardArea = new Area(3381, 3085, 3451, 3012);
    private boolean passedShantay = false;
    private boolean checkedBankForPass = false;

    @Override
    public int execute() {
        Logger.log("Slay Lizards");

        if (lizardArea.contains(Players.getLocal()))
            passedShantay = true;

        if (Inventory.containsAll(reqItems) && !BankUtilities.areItemsNoted(reqItems)
                && Inventory.count("Waterskin(4)") >= 1 && !Inventory.isFull()) {
            if (passedShantay) {
                if (lizardArea.contains(Players.getLocal())) {
                    SlayerUtilities.slayMonsterMelee(lizardArea, Arrays.asList("Lizard", "Small Lizard", "Desert Lizard"), true, "Ice cooler");
                } else {
                    Utilities.walkToArea(lizardArea);
                }
            } else {
                if (SlayerUtilities.preShantyPassArea.contains(Players.getLocal())) {
                    if (Inventory.contains("Shantay pass")) {
                        Utilities.walkToArea(lizardArea);
                        passedShantay = true;
                    } else {
                        if (checkedBankForPass) {
                            SlayerUtilities.buyShantayPass();
                        } else {
                            if (Bank.isOpen()) {
                                if (Bank.contains("Shantay pass")) {
                                    if (Bank.withdraw("Shantay pass"))
                                        Sleep.sleepUntil(() -> Inventory.contains("Shantay pass"), Utilities.getRandomSleepTime());
                                }
                                checkedBankForPass = true;
                            } else {
                                BankUtilities.openBank();
                            }
                        }
                    }
                } else {
                    Utilities.walkToArea(SlayerUtilities.preShantyPassArea);
                }
            }
        } else {
            if (passedShantay) {
                Item tab = Inventory.get(i -> i != null && i.getName().equals("Lumbridge teleport"));
                if (tab.interact()) {
                    Sleep.sleepUntil(() -> SlayerUtilities.lumbridgeFountainArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                    passedShantay = false;
                }
            } else {
                if (Inventory.contains("Waterskin(3)") || Inventory.contains("Waterskin(2)")
                        || Inventory.contains("Waterskin(1)") || Inventory.contains("Waterskin(0)")) {
                    if (Bank.isOpen()) {
                        if (Bank.close())
                            Sleep.sleepUntil(() -> !Bank.isOpen(), Utilities.getRandomSleepTime());
                    }

                    GameObject fountain = GameObjects.closest(i -> i != null && i.getName().equals("Fountain"));
                    Item waterskin = Inventory.get(i -> i != null && !i.getName().equals("Waterskin(4)")
                            && i.getName().contains("Waterskin"));

                    if (fountain.canReach() && fountain.distance(Players.getLocal()) <= 7) {
                        if (waterskin.useOn(fountain))
                            Sleep.sleepUntil(() -> Inventory.count("Waterskin(4)") >= SlayerUtilities.getInventoryAmount("Waterskin"), Utilities.getRandomSleepTime());
                    } else {
                        if (Walking.shouldWalk(Utilities.getShouldWalkDistance()))
                            Walking.walk(fountain.getTile());
                    }
                } else {
                    SlayerUtilities.bankForTask(new ArrayList<>(reqItems), true, "Waterskin");
                }
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay lizards");
    }

}

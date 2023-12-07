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

public class SlayCrocodilesNode extends TaskNode {
    private final Area fountainArea = new Area(3356, 2974, 3362, 2967);
    private final Area crocodileArea = new Area(3251, 2877, 3306, 2929);
    private final Area preShantyPassArea = new Area(3300, 3128, 3307, 3118);
    private final Area lumbridgeArea = new Area(3217, 3224, 3226, 3213);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Varrock teleport", "Lumbridge teleport",
            "Waterskin(4)", "Coins", ItemUtilities.currentFood));
    private boolean passedShantay = false;
    private boolean checkedBankForPass = false;

    @Override
    public int execute() {
        Logger.log("- Slay Crocodiles -");

        if (crocodileArea.contains(Players.getLocal()))
            passedShantay = true;

        if (Inventory.containsAll(reqItems) && !BankUtilities.areItemsNoted(reqItems)) {
            if (passedShantay) {
                if (crocodileArea.contains(Players.getLocal())) {
                    SlayerUtilities.slayMonsterMelee(crocodileArea, "Crocodile", false, "");
                } else {
                    Utilities.walkToArea(crocodileArea);
                }
            } else {
                if (preShantyPassArea.contains(Players.getLocal())) {
                    if (Inventory.contains("Shantay pass")) {
                        Utilities.walkToArea(crocodileArea);
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
                            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                BankUtilities.openBank();
                            }
                        }
                    }
                } else {
                    Utilities.walkToArea(preShantyPassArea);
                }
            }
        } else {
            if (passedShantay) {
                Item tab = Inventory.get(i -> i != null && i.getName().equals("Lumbridge teleport"));
                if (tab.interact()) {
                    Sleep.sleepUntil(() -> lumbridgeArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                    passedShantay = false;
                }
            } else {
                if (Inventory.contains("Waterskin(3)") || Inventory.contains("Waterskin(2)")
                        || Inventory.contains("Waterskin(1)") || Inventory.contains("Waterskin(0)")) {
                    GameObject fountain = GameObjects.closest(i -> i != null && i.getName().equals("Fountain"));
                    Item waterskin = Inventory.get(i -> i != null && !i.getName().equals("Waterskin(4)")
                            && i.getName().contains("Waterskin"));

                    if (waterskin.useOn(fountain))
                        Sleep.sleepUntil(() -> Inventory.count("Waterskin(4)") >= 8, Utilities.getRandomSleepTime());
                } else {
                    SlayerUtilities.bankForTask(new ArrayList<>(reqItems), true, "");
                }
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay crocodiles");
    }
}

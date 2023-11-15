package nodes.combat.slayertasks;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
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

public class SlayCrocodilesNode extends TaskNode {
    private final Area fountainArea = new Area(3356, 2974, 3362, 2967);
    private final Area crocodileArea = new Area(3251, 2877, 3306, 2929);
    private final Area preShantyPassArea = new Area(3300, 3128, 3307, 3118);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Varrock teleport", "Lumbridge teleport",
            "Waterskin(4)", "Coins", ItemUtilities.currentFood));
    private boolean passedShantay = false;
    private boolean checkedBankForPass = false;


    @Override
    public int execute() {
        Logger.log("Slay Crocodiles");
        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && !BankUtilities.areItemsNoted(reqItems)) {
            if (passedShantay) {
                if (crocodileArea.contains(Players.getLocal())) {
                    SlayerUtilities.slayMonster(crocodileArea, "Crocodile");
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
            if (Inventory.contains("Waterskin(3)") || Inventory.contains("Waterskin(2)")
                    || Inventory.contains("Waterskin(1)") || Inventory.contains("Waterskin(0)")) {
                //fill me in fountain daddddyyyyy
                Logger.log("Fill waterskins");
            } else {
                Logger.log("Bank");
                passedShantay = false;
                SlayerUtilities.bankForTask(reqItems, true);
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay crocodiles");
    }
}

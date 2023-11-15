package nodes.combat;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import utils.ItemUtilities;
import utils.Utilities;

public class EatNode extends TaskNode {

    @Override
    public int execute() {
        Utilities.currentNode = "Eat Node";
        Logger.log("Eat Node");

        if (Tabs.isOpen(Tab.INVENTORY)) {
            int amount = Inventory.count(ItemUtilities.currentFood);
            Item food = Inventory.get(ItemUtilities.currentFood);

            if (food != null) {
                if (food.interact())
                    Sleep.sleepUntil(() -> Inventory.count(ItemUtilities.currentFood) == (amount - 1), Utilities.getRandomSleepTime());
            }
        } else {
            if (Tabs.open(Tab.INVENTORY))
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.INVENTORY), Utilities.getRandomSleepTime());
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return Combat.getHealthPercent() < 60 && Inventory.contains(ItemUtilities.currentFood);
    }

    @Override
    public int priority() {
        return 6;
    }
}

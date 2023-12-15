package nodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import utils.EquipmentUtilities;
import utils.Utilities;

public class EquipmentNode extends TaskNode {

    @Override
    public int execute() {
        Logger.log("- Equip -");

        if (!Bank.isOpen() && !GrandExchange.isOpen() && !Shop.isOpen()) {
            Item item = Inventory.get(i -> i != null && EquipmentUtilities.requiredEquipment.contains(i.getName()));
            if (item != null) {
                if (item.interact())
                    Sleep.sleepUntil(() -> Equipment.contains(item.getName()), Utilities.getRandomSleepTime());
            }
        } else {
            if (Bank.isOpen()) {
                if (Bank.close())
                    Sleep.sleepUntil(() -> !Bank.isOpen(), Utilities.getRandomSleepTime());
            }

            if (Shop.isOpen()) {
                if (Shop.close())
                    Sleep.sleepUntil(() -> !Shop.isOpen(), Utilities.getRandomSleepTime());
            }

            Utilities.closeGeAndBank();
        }

        return Utilities.getRandomExecuteTime();
    }

        @Override
        public boolean accept () {
            return !Equipment.containsAll(EquipmentUtilities.requiredEquipment);
        }

        @Override
        public int priority () {
            return 2;
        }
    }

package nodes.combat.slayertasks;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
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

public class SlayIceWarriorsNode extends TaskNode {
    private final Area warriorArea = new Area(3037, 9599, 3055, 9564);
    private List<String> reqItems = new ArrayList<>(Arrays.asList("Enchanted gem", "Falador teleport", ItemUtilities.currentFood));

    @Override
    public int execute() {
        Logger.log("Slay ice Warriors");

        if (!Inventory.isFull() && Inventory.containsAll(reqItems) && Inventory.contains(ItemUtilities.currentFood)) {
            if (warriorArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonster(warriorArea, "Ice warrior");
            } else {
                Utilities.walkToArea(warriorArea);
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay ice warriors");
    }
}

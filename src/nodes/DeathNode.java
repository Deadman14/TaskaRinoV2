package nodes;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class DeathNode extends TaskNode {
    @Override
    public int execute() {
        Logger.log("- Death Node -");

        if (Players.getLocal().distance(Utilities.playerDeathTile) < Calculations.random(5, 8) && Players.getLocal().canReach(Utilities.playerDeathTile)) {
            NPC grave = NPCs.closest(i -> i!= null && i.getName().equals("Grave"));
            if (grave != null && grave.exists()) {
                if (grave.interact("Loot"))
                    Sleep.sleepUntil(() -> grave == null || !grave.exists(), Utilities.getRandomSleepTime());
            } else {
                Utilities.hasDied = false;
                Utilities.playerDeathTile = null;
            }
        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
            Walking.walk(Utilities.playerDeathTile);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return Utilities.hasDied;
    }

    @Override
    public int priority() {
        return 6;
    }
}

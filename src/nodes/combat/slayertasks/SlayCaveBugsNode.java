package nodes.combat.slayertasks;

import constants.ItemNameConstants;
import constants.NpcNameConstants;
import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import utils.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayCaveBugsNode extends TaskNode {
    private final Area caveBugArea = new Area(3142, 9598, 3207, 9538);
    private final Area swampEntranceArea = new Area(3165, 3176, 3173, 3169);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, ItemNameConstants.LUMBRIDGE_TELE,
            ItemUtilities.currentFood, ItemNameConstants.TINDERBOX, ItemNameConstants.CANDLE_LANTERN));

    //TODO: set to false and check game message when clicking the hole without rope to set it
    private boolean hasPlacedRopeBefore = true;

    @Override
    public int execute() {
        Logger.log("- Slay Cave Bugs -");

        if (Skills.getRealLevel(Skill.FIREMAKING) < 4) {
            TaskUtilities.currentTask = "Firemaking";
            TaskUtilities.taskTimer = new Timer(Calculations.random(1800000, 3600000));
            TaskUtilities.taskTimer.start();
            ItemUtilities.buyables = new ArrayList<>();
            return Utilities.getRandomExecuteTime();
        }

        if (Dialogues.inDialogue()) {
            Dialogues.continueDialogue();
            return Utilities.getRandomExecuteTime();
        }

        if (!Inventory.isFull() && Inventory.containsAll(reqItems)) {
            if (caveBugArea.contains(Players.getLocal())) {
                SlayerUtilities.slayMonsterMelee(caveBugArea, List.of(NpcNameConstants.CAVE_BUG), false, "");
            } else {
                if (Inventory.contains(ItemNameConstants.ROPE) || hasPlacedRopeBefore) {
                    if (swampEntranceArea.contains(Players.getLocal())) {
                        if (hasPlacedRopeBefore && Inventory.get(i -> i != null && i.getName().equals(ItemNameConstants.CANDLE_LANTERN)).hasAction("Extinguish")) {
                            GameObject darkHole = GameObjects.closest(i -> i != null && i.getName().equals("Dark hole"));

                            if (darkHole.interact())
                                Sleep.sleepUntil(() -> caveBugArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                        } else if (Inventory.get(i -> i != null && i.getName().equals("Candle lantern")).hasAction("Extinguish")) {
                            GameObject darkHole = GameObjects.closest(i -> i != null && i.getName().equals("Dark hole"));
                            Item rope = Inventory.get(i -> i != null && i.getName().equals("Rope"));

                            if (rope.useOn(darkHole))
                                Sleep.sleepUntil(() -> caveBugArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
                        } else {
                            if (Inventory.combine("Tinderbox", "Candle lantern"))
                                Sleep.sleepUntil(() ->
                                                Inventory.get(i -> i != null && i.getName().equals("Candle lantern")).hasAction("Extinguish"),
                                        Utilities.getRandomSleepTime());
                        }
                    } else {
                        Utilities.walkToArea(swampEntranceArea);
                    }
                } else {
                    if (Bank.isOpen()) {
                        if (Bank.contains("Rope")) {
                            if (Bank.withdraw("Rope"))
                                Sleep.sleepUntil(() -> Inventory.contains("Rope"), Utilities.getRandomSleepTime());
                        } else {
                            ItemUtilities.buyables.add(new GeItem("Rope", 1, LivePrices.getHigh(("Rope"))));
                        }
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        BankUtilities.openBank();
                    }
                }
            }
        } else {
            SlayerUtilities.bankForTask(reqItems, false, "");
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay cave bugs");
    }
}

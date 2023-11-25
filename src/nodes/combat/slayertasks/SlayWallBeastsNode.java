package nodes.combat.slayertasks;

import constants.ItemNameConstants;
import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import utils.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayWallBeastsNode extends TaskNode {
    private final Area wallBeastArea = new Area(3142, 9598, 3207, 9538);
    private final Area swampEntranceArea = new Area(3165, 3176, 3173, 3169);
    private final List<String> reqItems = new ArrayList<>(Arrays.asList(ItemNameConstants.ENCHANTED_GEM, "Lumbridge teleport",
            ItemUtilities.currentFood, "Tinderbox", "Candle lantern"));
    private Tile previousTile = null;

    @Override
    public int execute() {
        Logger.log("- Slay Wall Beasts -");

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
            if (wallBeastArea.contains(Players.getLocal())) {
                Utilities.shouldLoot = true;
                if (PlayerSettings.getConfig(43) == SlayerUtilities.GetAttackStyleConfig()) {
                    if (!Players.getLocal().isInCombat()) {
                        Character c = Players.getLocal().getCharacterInteractingWithMe();
                        NPC npc = c != null && (c.getName().equals("Hole in the wall") || c.getName().equals("Wall beast")) && wallBeastArea.contains(c) ? (NPC)c : NPCs.closest(g -> g.getName().equals("Hole in the wall") && !g.isInCombat() && wallBeastArea.contains(g) && !g.getTile().equals(previousTile));
                        if (npc != null) {
                            if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                Walking.walk(npc.getTile());
                            }
                        }
                    } else {
                        Character c = Players.getLocal().getCharacterInteractingWithMe();
                        if (c != null)
                            previousTile = c.getTile();
                    }
                } else {
                    SlayerUtilities.SetCombatStyle();
                }
            } else {
                if (Inventory.contains("Rope")) {
                    if (swampEntranceArea.contains(Players.getLocal())) {
                        if (Inventory.get(i -> i != null && i.getName().equals("Candle lantern")).hasAction("Extinguish")) {
                            GameObject darkHole = GameObjects.closest(i -> i != null && i.getName().equals("Dark hole"));
                            Item rope = Inventory.get(i -> i != null && i.getName().equals("Rope"));

                            if (rope.useOn(darkHole))
                                Sleep.sleepUntil(() -> wallBeastArea.contains(Players.getLocal()), Utilities.getRandomSleepTime());
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
            SlayerUtilities.bankForTask(reqItems, false);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Slay wall beasts");
    }
}

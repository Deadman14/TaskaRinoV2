package nodes.combat;

import constants.TaskNameConstants;
import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import utils.*;

import java.util.Collections;

public class TrainMeleeAndRangeNode extends TaskNode {
    private static final Area faladorSouthEntrance = new Area(3001, 3325, 3012, 3307);

    @Override
    public int execute() {
        Logger.log("- " + TaskUtilities.currentTask + " -");

        EquipmentUtilities.setRequiredEquipment();

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if (!Inventory.isFull() && !BankUtilities.areItemsNoted(Collections.singletonList(ItemUtilities.getCurrentFood()))
                && (Inventory.contains(ItemUtilities.getCurrentFood()) && Inventory.count(ItemUtilities.getCurrentFood()) > 1)) {
            Utilities.closeInterfaces();

            if (CombatUtilities.getCurrentCombatArea().contains(Players.getLocal())) {
                if (PlayerSettings.getConfig(43) == SlayerUtilities.GetAttackStyleConfig()) {
                    if (!Players.getLocal().isInCombat()) {
                        Character c = Players.getLocal().getCharacterInteractingWithMe();
                        NPC npc = c != null && c.getName().equals(SlayerUtilities.getCurrentCombatTrainingNpc()) && CombatUtilities.getCurrentCombatArea().contains(c)
                                ? (NPC) Players.getLocal().getCharacterInteractingWithMe()
                                : NPCs.closest(g -> g.getName().equals(SlayerUtilities.getCurrentCombatTrainingNpc()) && !g.isInCombat() && CombatUtilities.getCurrentCombatArea().contains(g));
                        if (npc != null) {
                            if (npc.canReach()) {
                                if (npc.interact()) {
                                    ItemUtilities.lootTile = npc.getTrueTile();
                                    Sleep.sleepUntil(() -> npc.isInCombat() || Players.getLocal().isInCombat() || Dialogues.canContinue(), Utilities.getRandomSleepTime());
                                }
                            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                Walking.walk(npc.getTile());
                            }
                        } else if (TaskUtilities.currentTask.equals(TaskNameConstants.KILL_IMPS)) {
                            if (!faladorSouthEntrance.contains(Players.getLocal()))
                                Utilities.walkToArea(faladorSouthEntrance);
                        }
                    } else {
                        NPC c = (NPC)Players.getLocal().getInteractingCharacter();

                        if (c != null && (c.isInteracting(Players.getLocal()) || !c.isInteractedWith()))
                            ItemUtilities.lootTile = c.getTrueTile();

                    }
                } else {
                    SlayerUtilities.SetCombatStyle();
                }
            } else {
                Utilities.walkToArea(CombatUtilities.getCurrentCombatArea());
            }
        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty() && (Inventory.isFull() || !Inventory.onlyContains(ItemUtilities.getCurrentFood()))) {
                    if (Bank.depositAllExcept(BankUtilities.depositAllExceptCombatGearFilter))
                        Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
                }

                if (BankUtilities.areItemsNoted(Collections.singletonList(ItemUtilities.getCurrentFood()))) {
                    if (Bank.depositAll(ItemUtilities.getCurrentFood()))
                        Sleep.sleepUntil(() -> !Inventory.contains(ItemUtilities.getCurrentFood()), Utilities.getRandomSleepTime());
                }

                if (Bank.contains(ItemUtilities.getCurrentFood()) && Bank.count(ItemUtilities.getCurrentFood()) > 10) {
                    BankUtilities.setBankMode(BankMode.ITEM);
                    if (Bank.withdraw(ItemUtilities.getCurrentFood(), 10 - Inventory.count(ItemUtilities.getCurrentFood())))
                        Sleep.sleepUntil(() -> Inventory.contains(ItemUtilities.getCurrentFood()) && Inventory.count(ItemUtilities.getCurrentFood()) == 10, Utilities.getRandomSleepTime());
                } else {
                    Logger.log("Buy me food");
                    ItemUtilities.buyables.add(new GeItem(ItemUtilities.getCurrentFood(), 100, LivePrices.getHigh(ItemUtilities.getCurrentFood())));
                }
            } else {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals(TaskNameConstants.TRAIN_COMBAT_MELEE)
                || TaskUtilities.currentTask.equals(TaskNameConstants.KILL_IMPS)
                || TaskUtilities.currentTask.equals(TaskNameConstants.TRAIN_COMBAT_RANGE);
    }
}
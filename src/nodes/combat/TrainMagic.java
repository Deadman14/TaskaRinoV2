package nodes.combat;

import constants.TaskNameConstants;
import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import utils.*;
import java.util.Collections;

public class TrainMagic extends TaskNode {
    @Override
    public int execute() {
        Logger.log("Train Combat Magic");

        EquipmentUtilities.setRequiredEquipment();

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if (!Equipment.isSlotEmpty(EquipmentSlot.SHIELD)) {
            if (Equipment.unequip(EquipmentSlot.SHIELD))
                Sleep.sleepUntil(() -> Equipment.isSlotEmpty(EquipmentSlot.SHIELD), Utilities.getRandomSleepTime());
        }

        if (!Inventory.containsAll(CombatUtilities.getCurrentRunes())) {
            CombatUtilities.needRunes = true;
            return Utilities.getRandomExecuteTime();
        }

        if (!Inventory.isFull() && !BankUtilities.areItemsNoted(Collections.singletonList(ItemUtilities.getCurrentFood()))
                && (Inventory.contains(ItemUtilities.getCurrentFood()) && Inventory.count(ItemUtilities.getCurrentFood()) > 1)) {
            Utilities.closeInterfaces();

            if (CombatUtilities.getCurrentCombatArea().contains(Players.getLocal())) {
                if (Magic.getAutocastSpell() != null && Magic.getAutocastSpell().equals(CombatUtilities.getCurrentSpell())
                        || ((Skills.getRealLevel(Skill.MAGIC) > 40 && Skills.getRealLevel(Skill.DEFENCE) < 40) && !Magic.isAutocastDefensive())) {
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
                        }
                    } else {
                        NPC c = (NPC)Players.getLocal().getInteractingCharacter();

                        if (c != null && (c.isInteracting(Players.getLocal()) || !c.isInteractedWith()))
                            ItemUtilities.lootTile = c.getTrueTile();

                    }
                } else {
                    if (Skills.getRealLevel(Skill.MAGIC) > 40 && Skills.getRealLevel(Skill.DEFENCE) < 40) {
                        if (Magic.getAutocastSpell() == null || !Magic.getAutocastSpell().equals(CombatUtilities.getCurrentSpell()) || !Magic.isAutocastDefensive()) {
                            if (Magic.setDefensiveAutocastSpell(CombatUtilities.getCurrentSpell()))
                                Sleep.sleepUntil(() -> Magic.getAutocastSpell().equals(CombatUtilities.getCurrentSpell()) && Magic.isAutocastDefensive(), Utilities.getRandomSleepTime());
                        }
                    } else {
                        if (Magic.getAutocastSpell() == null || !Magic.getAutocastSpell().equals(CombatUtilities.getCurrentSpell())) {
                            if (Magic.setAutocastSpell(CombatUtilities.getCurrentSpell()))
                                Sleep.sleepUntil(() -> Magic.getAutocastSpell().equals(CombatUtilities.getCurrentSpell()), Utilities.getRandomSleepTime());
                        }
                    }
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
        return TaskUtilities.currentTask.equals(TaskNameConstants.TRAIN_COMBAT_MAGIC) && !CombatUtilities.needRunes;
    }
}
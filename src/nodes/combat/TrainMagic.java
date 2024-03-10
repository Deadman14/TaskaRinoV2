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

        if (!Inventory.isFull() && !BankUtilities.areItemsNoted(Collections.singletonList(ItemUtilities.currentFood))
                && (Inventory.contains(ItemUtilities.currentFood) && Inventory.count(ItemUtilities.currentFood) > 1)) {
            Utilities.closeInterfaces();

            if (CombatUtilities.getCurrentCombatArea().contains(Players.getLocal())) {
                if (Magic.getAutocastSpell() != null && Magic.getAutocastSpell().equals(CombatUtilities.getCurrentSpell())
                        || ((Skills.getRealLevel(Skill.MAGIC) > 40 && Skills.getRealLevel(Skill.DEFENCE) < 40) && !Magic.isAutocastDefensive())) {
                    CombatUtilities.attackNpc();
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
            CombatUtilities.trainCombatBanking();
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals(TaskNameConstants.TRAIN_COMBAT_MAGIC) && Inventory.containsAll(CombatUtilities.getCurrentRunes());
    }
}
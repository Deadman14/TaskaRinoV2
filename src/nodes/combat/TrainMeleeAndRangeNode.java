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
                    CombatUtilities.attackNpc();
                } else {
                    SlayerUtilities.SetCombatStyle();
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
        return TaskUtilities.currentTask.equals(TaskNameConstants.TRAIN_COMBAT_MELEE)
                || TaskUtilities.currentTask.equals(TaskNameConstants.KILL_IMPS)
                || TaskUtilities.currentTask.equals(TaskNameConstants.TRAIN_COMBAT_RANGE);
    }
}
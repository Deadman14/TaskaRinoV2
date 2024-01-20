package nodes.combat;

import constants.ItemNameConstants;
import constants.NpcNameConstants;
import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrainCombatNode extends TaskNode {
    private final Area goblinArea = new Area(3240, 3250, 3264, 3225);
    private final Area cowArea = new Area(3242, 3296, 3264, 3256);
    private final Area hillGiantArea = new Area(3096, 9850, 3126, 9823);
    private final Area impArea = new Area(2953, 3330, 3059, 3293);
    private final Area mainImpArea = new Area(3002, 3325, 3010, 3310);

    private Timer walkAroundTimer = new Timer(Calculations.random(90000, 150000));

    @Override
    public int execute() {
        Logger.log("- Train Combat -");

        EquipmentUtilities.setRequiredEquipment();

        if (GrandExchange.isOpen()) {
            if (GrandExchange.close())
                Sleep.sleepUntil(() -> !GrandExchange.isOpen(), Utilities.getRandomSleepTime());
        }

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if (!Inventory.isFull() && (Inventory.contains(ItemUtilities.currentFood) && Inventory.count(ItemUtilities.currentFood) > 1
                && !BankUtilities.areItemsNoted(Collections.singletonList(ItemUtilities.currentFood)) || !Utilities.isGeFullyOpen())) {

            if (TaskUtilities.currentTask.equals("Train Combat Magic")) {
                if (!Inventory.containsAll(getCurrentRunes())) {
                    if (Bank.isOpen()) {
                        if (!Inventory.isEmpty()) {
                            if (Bank.depositAllExcept(i -> i.getName().equals(ItemUtilities.currentFood) || i.getName().contains(ItemNameConstants.RUNE)))
                                Sleep.sleepUntil(() -> Inventory.onlyContains(ItemUtilities.currentFood), Utilities.getRandomSleepTime());
                        }

                        for (String rune : getCurrentRunes()) {
                            int amount = 400;
                            if (getCurrentSpell().equals(Normal.FIRE_STRIKE) && rune.equals(ItemNameConstants.AIR_RUNE)) amount = 800;
                            if (getCurrentSpell().equals(Normal.FIRE_BOLT) && rune.equals(ItemNameConstants.AIR_RUNE)) amount = 1200;
                            if (getCurrentSpell().equals(Normal.FIRE_BLAST) && rune.equals(ItemNameConstants.AIR_RUNE)) amount = 1600;

                            if (Bank.contains(rune)) {
                                if (Bank.count(rune) > amount) {
                                    if (Bank.withdraw(rune, amount))
                                        Sleep.sleepUntil(() -> Inventory.contains(rune), Utilities.getRandomSleepTime());
                                } else {
                                    ItemUtilities.buyables.add(new GeItem(rune, amount * 3, LivePrices.getHigh(rune)));
                                }
                            } else {
                                ItemUtilities.buyables.add(new GeItem(rune, amount * 3, LivePrices.getHigh(rune)));
                            }
                        }
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        BankUtilities.openBank();
                    }

                    return Utilities.getRandomExecuteTime();
                }

                if (Skills.getRealLevel(Skill.MAGIC) > 40 && Skills.getRealLevel(Skill.DEFENCE) < 40) {
                    if (Magic.getAutocastSpell() == null || Magic.getAutocastSpell().equals(getCurrentSpell()) || !Magic.isAutocastDefensive()) {
                        if (Magic.setDefensiveAutocastSpell(getCurrentSpell()))
                            Sleep.sleepUntil(() -> Magic.getAutocastSpell().equals(getCurrentSpell()) && Magic.isAutocastDefensive(), Utilities.getRandomSleepTime());
                    }
                } else {
                    if (Magic.getAutocastSpell() == null || Magic.getAutocastSpell().equals(getCurrentSpell())) {
                        if (Magic.setAutocastSpell(getCurrentSpell()))
                            Sleep.sleepUntil(() -> Magic.getAutocastSpell().equals(getCurrentSpell()), Utilities.getRandomSleepTime());
                    }
                }

                if (!Equipment.isSlotEmpty(EquipmentSlot.SHIELD)) {
                    if (Equipment.unequip(EquipmentSlot.SHIELD))
                        Sleep.sleepUntil(() -> Equipment.isSlotEmpty(EquipmentSlot.SHIELD), Utilities.getRandomSleepTime());
                }
            }

            if (getCurrentCombatArea().contains(Players.getLocal())) {
                if (PlayerSettings.getConfig(43) == SlayerUtilities.GetAttackStyleConfig() || TaskUtilities.currentTask.equals("Train Combat Magic")) {
                    if (!Players.getLocal().isInCombat()) {
                        Character c = Players.getLocal().getCharacterInteractingWithMe();
                        NPC npc = c != null && c.getName().equals(SlayerUtilities.getCurrentCombatTrainingNpc()) && getCurrentCombatArea().contains(c) ? (NPC) Players.getLocal().getCharacterInteractingWithMe() : NPCs.closest(g -> g.getName().equals(SlayerUtilities.getCurrentCombatTrainingNpc()) && !g.isInCombat() && getCurrentCombatArea().contains(g));
                        if (npc != null) {
                            if (npc.canReach()) {
                                if (npc.interact()) {
                                    ItemUtilities.lootTile = npc.getTrueTile();
                                    Sleep.sleepUntil(() -> npc.isInCombat() || Players.getLocal().isInCombat() || Dialogues.canContinue(), Utilities.getRandomSleepTime());
                                }
                            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                                Walking.walk(npc.getTile());
                            }
                        } else if (TaskUtilities.currentTask.equals("Kill Imps")) {
                            if (walkAroundTimer.isPaused()) walkAroundTimer.start();

                            if (walkAroundTimer.remaining() <= 0) {
                                Utilities.walkToArea(mainImpArea);
                                walkAroundTimer = new Timer(Calculations.random(90000, 150000));
                                walkAroundTimer.start();
                            }
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
                Utilities.walkToArea(getCurrentCombatArea());
            }
        } else {
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty() && (Inventory.isFull() || !Inventory.onlyContains(ItemUtilities.currentFood))) {
                    if (Bank.depositAllExcept(i -> i.getName().equals(ItemUtilities.currentFood) || (!getCurrentRunes().isEmpty() && getCurrentRunes().contains(i.getName()))))
                        Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
                }

                if (BankUtilities.areItemsNoted(Collections.singletonList(ItemUtilities.currentFood))) {
                    if (Bank.depositAll(ItemUtilities.currentFood))
                        Sleep.sleepUntil(() -> !Inventory.contains(ItemUtilities.currentFood), Utilities.getRandomSleepTime());
                }

                if (!Inventory.contains(ItemUtilities.currentFood) || Inventory.count(ItemUtilities.currentFood) < 10) {
                    if (Bank.contains(ItemUtilities.currentFood) && Bank.count(ItemUtilities.currentFood) > 10) {
                        BankUtilities.setBankMode(BankMode.ITEM);
                        if (Bank.withdraw(ItemUtilities.currentFood, 10 - Inventory.count(ItemUtilities.currentFood)))
                            Sleep.sleepUntil(() -> Inventory.contains(ItemUtilities.currentFood) && Inventory.count(ItemUtilities.currentFood) == 10, Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(ItemUtilities.currentFood, 100, LivePrices.getHigh(ItemUtilities.currentFood)));

                        return Utilities.getRandomExecuteTime();
                    }
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return Equipment.containsAll(EquipmentUtilities.requiredEquipment)
                && (TaskUtilities.currentTask.contains("Train Combat") || TaskUtilities.currentTask.equals("Kill Imps"));
    }

    private Area getCurrentCombatArea() {
        if (TaskUtilities.currentTask.contains("Melee")) {
            int att = Skills.getRealLevel(Skill.ATTACK);
            int str = Skills.getRealLevel(Skill.STRENGTH);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (att > 39 && str > 39 && def > 39)
                return hillGiantArea;
            if (att > 19 && str > 19 && def > 19)
                return cowArea;
            else
                return goblinArea;
        } else if (TaskUtilities.currentTask.contains("Range")) {
            int rang = Skills.getRealLevel(Skill.RANGED);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (rang > 39 && def > 39)
                return hillGiantArea;
            if (rang > 19 && def > 19)
                return cowArea;
            else
                return goblinArea;
        } else if (TaskUtilities.currentTask.contains("Magic")) {
            int mage = Skills.getRealLevel(Skill.MAGIC);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (mage > 39 && def > 39)
                return hillGiantArea;
            if (mage > 19 && def > 19)
                return cowArea;
            else
                return goblinArea;
        } else if (TaskUtilities.currentTask.equals("Kill Imps")) {
            return impArea;
        }

        return goblinArea;
    }

    private List<String> getCurrentRunes() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        if (level > 58)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.DEATH_RUNE, ItemNameConstants.AIR_RUNE));
        if (level > 34)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.CHAOS_RUNE, ItemNameConstants.AIR_RUNE));
        if (level > 12)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.MIND_RUNE, ItemNameConstants.AIR_RUNE));

        return new ArrayList<>(List.of(ItemNameConstants.MIND_RUNE));
    }

    private Normal getCurrentSpell() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        if (level > 58)
            return Normal.FIRE_BLAST;
        if (level > 34)
            return Normal.FIRE_BOLT;
        if (level > 12)
            return Normal.FIRE_STRIKE;

        return Normal.WIND_STRIKE;
    }
}

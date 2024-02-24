package utils;

import constants.ItemNameConstants;
import constants.NpcNameConstants;
import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CombatUtilities {
    private static final Area goblinArea = new Area(3240, 3250, 3264, 3225);
    private static final Area cowArea = new Area(3242, 3296, 3264, 3256);
    private static final Area hillGiantArea = new Area(3096, 9850, 3126, 9823);
    private static final Area impArea = new Area(2953, 3330, 3059, 3293);

    public static Area getCurrentCombatArea() {
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

    public static Normal getCurrentSpell() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        if (level > 58)
            return Normal.FIRE_BLAST;
        if (level > 34)
            return Normal.FIRE_BOLT;
        if (level > 12)
            return Normal.FIRE_STRIKE;

        return Normal.WIND_STRIKE;
    }

    public static List<String> getCurrentRunes() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        if (level > 58)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.DEATH_RUNE, ItemNameConstants.AIR_RUNE));
        if (level > 34)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.CHAOS_RUNE, ItemNameConstants.AIR_RUNE));
        if (level > 12)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.MIND_RUNE, ItemNameConstants.AIR_RUNE));

        return new ArrayList<>(List.of(ItemNameConstants.MIND_RUNE));
    }

    public static void attackNpc() {
        if (!Players.getLocal().isInCombat()) {
            Character c = Players.getLocal().getCharacterInteractingWithMe();
            NPC npc = c != null && c.getName().equals(getCurrentCombatTrainingNpc()) && getCurrentCombatArea().contains(c)
                    ? (NPC) Players.getLocal().getCharacterInteractingWithMe()
                    : NPCs.closest(g -> g.getName().equals(getCurrentCombatTrainingNpc()) && !g.isInCombat() && getCurrentCombatArea().contains(g));
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
    }

    public static String getCurrentCombatTrainingNpc() {
        if (TaskUtilities.currentTask.contains("Melee")) {
            int att = Skills.getRealLevel(Skill.ATTACK);
            int str = Skills.getRealLevel(Skill.STRENGTH);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (att > 39 && str > 39 && def > 39)
                return NpcNameConstants.HILL_GIANTS;
            if (att > 19 && str > 19 && def > 19)
                return NpcNameConstants.COW;
            else
                return "Goblin";
        } else if (TaskUtilities.currentTask.contains("Range")) {
            int rang = Skills.getRealLevel(Skill.RANGED);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (rang > 39 && def > 39)
                return NpcNameConstants.HILL_GIANTS;
            if (rang > 19 && def > 19)
                return NpcNameConstants.COW;
            else
                return "Goblin";
        } else if (TaskUtilities.currentTask.contains("Magic")) {
            int mage = Skills.getRealLevel(Skill.MAGIC);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (mage > 39 && def > 39)
                return NpcNameConstants.HILL_GIANTS;
            if (mage > 19 && def > 19)
                return NpcNameConstants.COW;
            else
                return NpcNameConstants.GOBLIN;
        } else if (TaskUtilities.currentTask.equals("Kill Imps")) {
            return NpcNameConstants.IMP;
        }

        return NpcNameConstants.GOBLIN;
    }

    public static void trainCombatBanking() {
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
}
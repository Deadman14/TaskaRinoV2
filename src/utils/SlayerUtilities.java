package utils;

import constants.EquipmentNameConstants;
import constants.ItemNameConstants;
import constants.NpcNameConstants;
import models.GeItem;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.map.Area;

import java.util.List;

public class SlayerUtilities {
    public static String currentSlayerTask = "";
    public static boolean getNewSlayerTaskAfterTask = false;

    // rapid is 1
    public static int GetAttackStyleConfig() {
        switch (TaskUtilities.currentTask) {
            case "Slay ice warriors", "Slay kalphite", "Slay ogres", "Train Combat Melee", "Slay ice giants", "Slay crocodiles",
                    "Slay hobgoblins", "Slay cockatrice", "Slay wall beasts", "Slay cave bugs", "Slay moss giants" -> {
                return GetMeleeConfig();
            }
            case "Train Combat Range" -> {
                int defLevel = Skills.getRealLevel(Skill.DEFENCE);
                int rangeLevel = Skills.getRealLevel(Skill.RANGED);
                if (defLevel < rangeLevel)
                    return 3;
                return 1;
            }
            default -> {
                return 1;
            }
        }
    }
    public static CombatStyle GetAttackStyle() {
        switch (TaskUtilities.currentTask) {
            case "Slay ice warriors", "Slay kalphite", "Slay ogres", "Slay ice giants", "Train Combat Melee", "Slay crocodiles",
                "Slay hobgoblins", "Slay cockatrice", "Slay wall beasts", "Slay cave bugs", "Slay moss giants" -> {
                return GetMeleeStyle();
            }
            case "Train Combat Range" -> {
                int defLevel = Skills.getRealLevel(Skill.DEFENCE);
                int rangeLevel = Skills.getRealLevel(Skill.RANGED);
                if (defLevel < rangeLevel)
                    return CombatStyle.RANGED_DEFENCE;
                return CombatStyle.RANGED_RAPID;
            }
            default -> {
                return CombatStyle.STRENGTH;
            }
        }
    }

    public static void SetCombatStyle() {
        if (Tabs.isOpen(Tab.COMBAT)) {
            if (Combat.setCombatStyle(GetAttackStyle()))
                Sleep.sleepUntil(() -> PlayerSettings.getConfig(43) == GetAttackStyleConfig(), Utilities.getRandomSleepTime());
        } else {
            if (Tabs.open(Tab.COMBAT))
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.COMBAT), Utilities.getRandomSleepTime());
        }
    }

    public static void slayMonster(Area monsterArea, String monsterName) {
        Utilities.shouldLoot = true;
        if (PlayerSettings.getConfig(43) == SlayerUtilities.GetAttackStyleConfig()) {
            if (!Players.getLocal().isInCombat()) {
                Character c = Players.getLocal().getCharacterInteractingWithMe();
                NPC npc = c != null && c.getName().equals(monsterName) && monsterArea.contains(c) ? (NPC) Players.getLocal().getCharacterInteractingWithMe() : NPCs.closest(g -> g.getName().equals(monsterName) && !g.isInCombat() && monsterArea.contains(g));
                if (npc != null) {
                    if (npc.canReach()) {
                        if (npc.interact("Attack"))
                            Sleep.sleepUntil(() -> npc.isInCombat() || Players.getLocal().isInCombat() || Dialogues.canContinue(), Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Logger.log("SlayMonster - Walk to mob");
                        Walking.walk(npc.getTile());
                    }
                }
            }
        } else {
            SetCombatStyle();
        }
    }

    public static void bankForTask(List<String> reqItems, boolean needShantayPass) {
        Utilities.shouldLoot = false;
        if (Bank.isOpen()) {
            if (!Inventory.isEmpty() && (Inventory.isFull() || !Inventory.onlyContains(i -> reqItems.contains(i.getName())) || BankUtilities.areItemsNoted(reqItems))) {
                if (Bank.depositAllExcept(i -> reqItems.contains(i.getName()) && !i.isNoted()))
                    Sleep.sleepUntil(() -> Inventory.onlyContains(i -> reqItems.contains(i.getName())), Utilities.getRandomSleepTime());
            }

            BankUtilities.setBankMode(BankMode.ITEM);

            if (needShantayPass) reqItems.add(ItemNameConstants.SHANTAY_PASS);
            for (String item : reqItems) {
                if ((Bank.contains(item) && Bank.count(item) >= getInventoryAmount(item)) &&
                Inventory.count(item) < getInventoryAmount(item)) {
                    if (Bank.withdraw(item, getInventoryAmount(item) - Inventory.count(item)))
                        Sleep.sleepUntil(() -> Inventory.count(item) >= getInventoryAmount(item), Utilities.getRandomSleepTime());
                } else {
                    if (!item.equals(ItemNameConstants.ENCHANTED_GEM) && !item.equals(ItemNameConstants.SHANTAY_PASS))
                        ItemUtilities.buyables.add(new GeItem(item, getGeAmount(item), LivePrices.getHigh(item)));
                }
            }
        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
            BankUtilities.openBank();
        }
    }

    public static void buyShantayPass() {
        if (Shop.isOpen()) {
            if (Shop.purchase(ItemNameConstants.SHANTAY_PASS, 50))
                Sleep.sleepUntil(() -> Inventory.contains(ItemNameConstants.SHANTAY_PASS), Utilities.getRandomSleepTime());
        } else {
            NPC shanty = NPCs.closest(i -> i != null && i.getName().equals(NpcNameConstants.SHANTAY));
            if (shanty.interact("Trade"))
                Sleep.sleepUntil(Shop::isOpen, Utilities.getRandomSleepTime());
        }
    }

    public static void buyItemFromSlayerMaster(String item, int coins) {
        if (Inventory.count(ItemNameConstants.COINS) >= coins && Inventory.contains("Varrock teleport")) {
            if (getCurrentSlayerMasterArea().contains(Players.getLocal())) {
                if (Shop.isOpen()) {
                    if (Shop.purchase(item, 1))
                        Sleep.sleepUntil(() -> Inventory.contains(item), Utilities.getRandomSleepTime());
                } else {
                    NPC master = NPCs.closest(i -> i != null && i.getName().equals(getCurrentSlayerMaster()));
                    if (master.interact("Trade"))
                        Sleep.sleepUntil(Shop::isOpen, Utilities.getRandomSleepTime());
                }
            } else {
                Utilities.walkToArea(getCurrentSlayerMasterArea());
            }
        } else {
            if (Bank.isOpen()) {
                if (Inventory.emptySlotCount() <= 3) {
                    if (Bank.depositAllExcept(ItemNameConstants.COINS))
                        Sleep.sleepUntil(() -> Inventory.emptySlotCount() > 3, Utilities.getRandomSleepTime());
                }

                if (Inventory.count(ItemNameConstants.COINS) < coins) {
                    if (Bank.withdraw(ItemNameConstants.COINS, coins))
                        Sleep.sleepUntil(() -> Inventory.count(ItemNameConstants.COINS) >= coins, Utilities.getRandomSleepTime());
                }

                if (!Inventory.contains("Varrock teleport")) {
                    if (Bank.withdraw("Varrock teleport", 2))
                        Sleep.sleepUntil(() -> Inventory.contains("Varrock teleport"), Utilities.getRandomSleepTime());
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }
    }

    public static Area getCurrentSlayerMasterArea() {
        int level = Players.getLocal().getLevel();

        if (level > 74)
            return new Area(1305, 3788, 1312, 3781);

        if (level > 70 && PaidQuest.LOST_CITY.isFinished())
            return new Area(2440, 4434, 2454, 4422);

        return new Area(3138, 9916, 3150, 9902);
    }

    public static String getCurrentSlayerMaster() {
        int level = Players.getLocal().getLevel();

        if (level > 74)
            return NpcNameConstants.KONAR;

        if (level > 70 && PaidQuest.LOST_CITY.isFinished())
            return NpcNameConstants.CHAELDAR;

        return NpcNameConstants.VANNAKA;
    }

    private static int GetMeleeConfig() {
        //Strength
        if (Skills.getRealLevel(Skill.STRENGTH) == Skills.getRealLevel(Skill.ATTACK) && Skills.getRealLevel(Skill.STRENGTH) == Skills.getRealLevel(Skill.DEFENCE)) {
            return 1;
        }

        //Defence
        if (Skills.getRealLevel(Skill.STRENGTH) > Skills.getRealLevel(Skill.DEFENCE) && Skills.getRealLevel(Skill.ATTACK) > Skills.getRealLevel(Skill.DEFENCE))
            return 3;

        //Attack
        if (Skills.getRealLevel(Skill.STRENGTH) > Skills.getRealLevel(Skill.ATTACK) && Skills.getRealLevel(Skill.STRENGTH) > Skills.getRealLevel(Skill.DEFENCE)) {
            return 0;
        }



        return 1;
    }

    private static CombatStyle GetMeleeStyle() {
        //Strength
        if (Skills.getRealLevel(Skill.STRENGTH) == Skills.getRealLevel(Skill.ATTACK) && Skills.getRealLevel(Skill.STRENGTH) == Skills.getRealLevel(Skill.DEFENCE))
            return CombatStyle.STRENGTH;

        //Defence
        if (Skills.getRealLevel(Skill.STRENGTH) > Skills.getRealLevel(Skill.DEFENCE) && Skills.getRealLevel(Skill.ATTACK) > Skills.getRealLevel(Skill.DEFENCE))
            return CombatStyle.DEFENCE;

        //Attack
        if (Skills.getRealLevel(Skill.STRENGTH) > Skills.getRealLevel(Skill.ATTACK) && Skills.getRealLevel(Skill.STRENGTH) > Skills.getRealLevel(Skill.DEFENCE))
            return CombatStyle.ATTACK;

        return CombatStyle.STRENGTH;
    }

    private static int getInventoryAmount(String itemName) {
        int amount = 1;
        if (itemName.equals(ItemUtilities.currentFood)) {
            amount = 15;
            if (Skills.getRealLevel(Skill.STRENGTH) >= 55)
                amount = 10;
        }
        if (itemName.contains("teleport")) amount = 2;
        if (itemName.contains("Waterskin")) amount = 8;
        if (itemName.equals(ItemNameConstants.COINS)) amount = 10000;

        return amount;
    }

    private static int getGeAmount(String itemName) {
        int amount = 1;
        if (itemName.equals(ItemUtilities.currentFood)) amount = 250;
        if (itemName.contains("teleport")) amount = 50;
        if (itemName.contains("Waterskin")) amount = 8;

        return amount;
    }
}

package utils;

import constants.ItemNameConstants;
import constants.NpcNameConstants;
import models.GeItem;
import org.dreambot.api.methods.Calculations;
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
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.items.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayerUtilities {
    public static String currentSlayerTask = "";
    public static boolean getNewSlayerTaskAfterTask = false;
    public static boolean hasCheckedBankForSlayerEquipment = false;
    public static final List<String> slayerItems = new ArrayList<>(Arrays.asList("Bag of salt", ItemNameConstants.ENCHANTED_GEM));
    public static NPC deathItemMonster = null;

    // rapid is 1
    public static int GetAttackStyleConfig() {
        switch (TaskUtilities.currentTask) {
            case "Slay ice warriors", "Slay kalphite", "Slay ogres", "Train Combat Melee", "Slay ice giants", "Slay crocodiles",
                    "Slay hobgoblins", "Slay cockatrice", "Slay wall beasts", "Slay cave bugs", "Slay moss giants",
                    "Slay basilisks", "Slay killerwatts", "Slay pyrefiends", "Slay rockslugs", "Slay cave slimes",
                    "Slay ankou", "Slay cave crawlers", "Slay hill giants", "Slay fire giants", "Slay lesser demons" -> {
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
                    "Slay hobgoblins", "Slay cockatrice", "Slay wall beasts", "Slay cave bugs", "Slay moss giants",
                    "Slay basilisks", "Slay killerwatts", "Slay pyrefiends", "Slay rockslugs", "Slay cave slimes", "Slay ankou",
                    "Slay cave crawlers", "Slay hill giants", "Slay fire giants", "Slay lesser demons" -> {
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

    public static void slayMonsterMelee(Area monsterArea, String monsterName, boolean useDeathItem, String deathItem) {
        //TODO: remove this
        if (TaskUtilities.taskTimer.remaining() < 100000) {
            TaskUtilities.taskTimer = new Timer(999999999);
            TaskUtilities.taskTimer.start();
        }

        Utilities.shouldLoot = true;

        ItemUtilities.dropDropables();
        if (PlayerSettings.getConfig(43) == SlayerUtilities.GetAttackStyleConfig()) {
            if (useDeathItem && deathItemMonster != null) {
                Logger.log("-- Slay Monster Melee For Task With Death Item --");
                if (deathItemMonster.getHealthPercent() < 15) {
                    Item i = Inventory.get(deathItem);
                    if (i != null) {
                        if (i.useOn(deathItemMonster)) {
                            Sleep.sleepUntil(() -> !deathItemMonster.exists(), Utilities.getRandomSleepTime());
                            deathItemMonster = null;
                        }
                    }
                }
            } else {
                Logger.log("-- Slay Monster Melee For Task --");
                if (!Players.getLocal().isInCombat()) {
                    Character c = Players.getLocal().getCharacterInteractingWithMe();
                    NPC npc = c != null && c.getName().equals(monsterName) && monsterArea.contains(c) ? (NPC) Players.getLocal().getCharacterInteractingWithMe() : NPCs.closest(g -> g.getName().equals(monsterName) && !g.isInCombat() && monsterArea.contains(g));
                    if (npc != null) {
                        if (npc.canReach()) {
                            if (npc.interact("Attack")) {
                                Sleep.sleepUntil(() -> npc.isInCombat() || Players.getLocal().isInCombat() || Dialogues.canContinue(), Utilities.getRandomSleepTime());

                                if (useDeathItem)
                                    deathItemMonster = npc;
                            }
                        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                            Walking.walk(npc.getTile());
                        }
                    }
                }
            }
        } else {
            SetCombatStyle();
        }
    }

    public static void bankForTask(List<String> reqItems, boolean needShantayPass, String multiUseItem) {
        Logger.log("-- Bank For Slayer Task --");
        Utilities.shouldLoot = false;

        List<String> slayerItemsToBuy = reqItems.stream().filter(i -> slayerItems.contains(i) && !Inventory.contains(i)).toList();
        if (!slayerItemsToBuy.isEmpty()) {
            for (String m : slayerItemsToBuy) {
                Logger.log("Slayer ITem To Buy: " + m);
            }

            String item = slayerItemsToBuy.get(0);
            if (SlayerUtilities.hasCheckedBankForSlayerEquipment) {
                SlayerUtilities.buyItemFromSlayerMaster(item, Calculations.random(7000, 10000));
            } else {
                if (Bank.isOpen()) {
                    if (Bank.contains(item)) {
                        if (Bank.withdraw(item))
                            Sleep.sleepUntil(() -> Inventory.contains(item), Utilities.getRandomSleepTime());
                    }

                    SlayerUtilities.hasCheckedBankForSlayerEquipment = true;
                } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                    BankUtilities.openBank();
                }
            }
        } else {
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
                        if (!slayerItems.contains(item) && !item.equals(ItemNameConstants.SHANTAY_PASS) && !item.equals("Waterskin(4)")
                                && Bank.count(item) < getInventoryAmount(item))
                            ItemUtilities.buyables.add(new GeItem(item, getGeAmount(item), LivePrices.getHigh(item)));
                    }
                }

                if (!multiUseItem.isEmpty())
                    BankUtilities.withdrawMultiUseItems(multiUseItem, getInventoryAmount(multiUseItem));
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
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
        Logger.log("-- Buy Item From Slayer Master --");

        if (Inventory.count(ItemNameConstants.COINS) >= coins) {
            if (getCurrentSlayerMasterArea().contains(Players.getLocal())) {
                if (Shop.isOpen()) {
                    if (Shop.purchase(item, getGeAmount(item)))
                        Sleep.sleepUntil(() -> Inventory.count(item) >= getGeAmount(item), Utilities.getRandomSleepTime());
                    hasCheckedBankForSlayerEquipment = false;
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
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }
    }

    public static Area getCurrentSlayerMasterArea() {
        if (Combat.getCombatLevel() >= 75)
            return new Area(1305, 3788, 1312, 3781);

        if (PaidQuest.LOST_CITY.isFinished() && Combat.getCombatLevel() < 75)
            return new Area(2440, 4434, 2454, 4422);

        return new Area(3138, 9916, 3150, 9902);
    }

    public static String getCurrentSlayerMaster() {
        //TODO: Check for melee stats at 60, magic at 55, range at 40
        if (Combat.getCombatLevel() >= 75)
            return NpcNameConstants.KONAR;

        if (PaidQuest.LOST_CITY.isFinished() && Combat.getCombatLevel() < 75)
            return NpcNameConstants.CHAELDAR;

        return NpcNameConstants.VANNAKA;
    }

    public static int getInventoryAmount(String itemName) {
        int amount = 1;
        if (itemName.equals(ItemUtilities.currentFood)) {
            amount = 15;
            if (Players.getLocal().getLevel() >= 60 && !TaskUtilities.currentTask.contains("killerwatts"))
                amount = 10;
        }
        if (itemName.contains("teleport")) amount = 2;
        if (itemName.contains("Waterskin")) amount = 7;
        if (itemName.contains("Antipoison")) amount = 6;
        if (itemName.equals("Bag of salt")) amount = 40;
        if (itemName.equals(ItemNameConstants.COINS)) amount = 10000;

        return amount;
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

    public static int getGeAmount(String itemName) {
        int amount = 1;
        if (itemName.equals(ItemUtilities.currentFood)) amount = 350;
        if (itemName.contains("teleport")) amount = 50;
        if (itemName.contains("Waterskin")) amount = 8;
        if (itemName.contains("Antipoison")) amount = 30;
        if (itemName.equals("Bag of salt")) amount = 200;

        return amount;
    }
}

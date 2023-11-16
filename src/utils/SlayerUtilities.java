package utils;

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
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
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
                    "Slay hobgoblins" -> {
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
                "Slay hobgoblins" -> {
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
                        Walking.walk(npc.getTile());
                    }
                }
            }
        } else {
            SetCombatStyle();
        }
    }

    public static void bankForTask(List<String> reqItems, boolean needShantayPass) {
        if (Bank.isOpen()) {
            if (!Inventory.isEmpty() && (Inventory.isFull() || !Inventory.onlyContains(i -> reqItems.contains(i.getName())) || BankUtilities.areItemsNoted(reqItems))) {
                if (Bank.depositAllExcept(i -> reqItems.contains(i.getName()) && !i.isNoted()))
                    Sleep.sleepUntil(() -> Inventory.onlyContains(i -> reqItems.contains(i.getName())), Utilities.getRandomSleepTime());
            }

            BankUtilities.setBankMode(BankMode.ITEM);

            if (needShantayPass) reqItems.add("Shantay pass");
            for (String item : reqItems) {
                if ((Bank.contains(item) && Bank.count(item) >= getInventoryAmount(item)) &&
                Inventory.count(item) < getInventoryAmount(item)) {
                    if (Bank.withdraw(item, getInventoryAmount(item) - Inventory.count(item)))
                        Sleep.sleepUntil(() -> Inventory.count(item) >= getInventoryAmount(item), Utilities.getRandomSleepTime());
                } else {
                    if (!item.equals("Enchanted gem") && !item.equals("Shantay pass"))
                        ItemUtilities.buyables.add(new GeItem(item, getGeAmount(item), LivePrices.getHigh(item)));
                }
            }
        } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
            BankUtilities.openBank();
        }
    }

    public static void buyShantayPass() {
        if (Shop.isOpen()) {
            if (Shop.purchase("Shantay pass", 50))
                Sleep.sleepUntil(() -> Inventory.contains("Shanty pass"), Utilities.getRandomSleepTime());
        } else {
            NPC shanty = NPCs.closest(i -> i != null && i.getName().equals("Shantay"));
            if (shanty.interact("Trade"))
                Sleep.sleepUntil(Shop::isOpen, Utilities.getRandomSleepTime());
        }
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
        if (itemName.equals("Coins")) amount = 2000;

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

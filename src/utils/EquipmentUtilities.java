package utils;

import constants.ItemNameConstants;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipmentUtilities {
    public static List<String> requiredEquipment = new ArrayList<>();

    public static boolean hasAllEquipment() {
        List<Item> inv = Inventory.all(i -> i != null && requiredEquipment.contains(i.getName()));
        List<Item> equip = Equipment.all(i -> i != null && requiredEquipment.contains(i.getName()));

        inv.addAll(equip);
        List<String> names = inv.stream().map(i -> i.getName()).toList();
        return names.containsAll(requiredEquipment);
    }

    public static void setRequiredEquipment() {
        requiredEquipment = getCurrentEquipment();
        Logger.log(requiredEquipment.size());
    }

    private static ArrayList<String> getCurrentEquipment() {
        switch (TaskUtilities.currentTask) {
            case "Train Combat Melee":
            case "Kill Imps":
            case "Slay kalphite":
            case "Slay ogre":
            case "Slay ice warriors":
            case "Slay moss giants":
            case "Slay hobgoblins":
            case "Slay ice giants":
            case "Slay crocodiles":
            case "Slay cockatrice":
            case "Slay wall beasts":
            case "Slay cave bugs":
            case "Slay basilisks":
            case "Slay killerwatts":
            case "Slay rockslugs":
            case "Slay cave slimes":
            case "Slay ankou":
            case "Slay cave crawlers":
            case "Slay hill giants":
            case "Slay fire giants":
            case "Slay lesser demons":
                if (!Utilities.isGeFullyOpen()) {
                    return new ArrayList<>(Arrays.asList("Bronze sword", "Wooden shield"));
                }

                return new ArrayList<>(Arrays.asList(
                        getCurrentFullHelm(),
                        getCurrentPlatebody(),
                        getCurrentPlatelegs(),
                        getCurrentKiteshield(),
                        getCurrentSword(),
                        getCurrentMeleeBoots(),
                        getCurrentMeleeHandslot()
                ));
            case "Train Combat Range":
                return new ArrayList<>(Arrays.asList(
                        getCurrentRangedHelm(),
                        getCurrentRangedBody(),
                        getCurrentRangedPants(),
                        getCurrentRangedGloves(),
                        getCurrentBow(),
                        getCurrentArrow()
                ));
            case "Train Combat Magic":
                return new ArrayList<>(Arrays.asList(
                        getCurrentMagicHelm(),
                        getCurrentMagicBody(),
                        getCurrentMagicPants(),
                        getCurrentMagicNecklace(),
                        getCurrentMagicWeapon()
                ));
            case "Slay pyrefiends":
                return new ArrayList<>(Arrays.asList(
                        getCurrentFullHelm(),
                        getCurrentRangedBody(),
                        getCurrentRangedPants(),
                        getCurrentRangedOffhand(),
                        getCurrentSword(),
                        getCurrentMeleeBoots(),
                        getCurrentRangedGloves()
                ));
            default:
                return new ArrayList<>();
        }
    }

    public static String getCurrentFullHelm() {
        int level = Skills.getRealLevel(Skill.DEFENCE);

        if (TaskUtilities.currentTask.equals("Slay wall beasts") || TaskUtilities.currentTask.equals("Slay cave bugs")
            || TaskUtilities.currentTask.equals("Slay cave slimes"))
            return "Spiny helmet";

        //if (level > 59 && Utilities.isP2P)
            //return "Dragon med helm";
        if (level > 39)
            return "Rune full helm";
        else if (level > 29)
            return "Adamant full helm";
        else if (level > 9)
            return "Black full helm";
        else
            return "Iron full helm";
    }

    public static String getCurrentPlatebody() {
        int level = Skills.getRealLevel(Skill.DEFENCE);

        //if (level > 59 && Utilities.isP2P)
            //return "Dragon chainbody";
        if (level > 39)
            return "Rune chainbody";
        else if (level > 29)
            return "Adamant platebody";
        else if (level > 9)
            return "Black platebody";
        else
            return "Iron platebody";
    }

    public static String getCurrentPlatelegs() {
        int level = Skills.getRealLevel(Skill.DEFENCE);

        //if (level > 59 && Utilities.isP2P)
            //return "Dragon platelegs";
        if (level > 39)
            return "Rune platelegs";
        else if (level > 29)
            return "Adamant platelegs";
        else if (level > 9)
            return "Black platelegs";
        else
            return "Iron platelegs";
    }

    public static String getCurrentKiteshield() {
        int level = Skills.getRealLevel(Skill.DEFENCE);

        if (TaskUtilities.currentTask.equals("Slay cockatrice") || TaskUtilities.currentTask.equals("Slay basilisks"))
            return "Mirror shield";

        if (level > 39)
            return "Rune kiteshield";
        else if (level > 29)
            return "Adamant kiteshield";
        else if (level > 9)
            return "Black kiteshield";
        else
            return "Iron kiteshield";
    }

    public static String getCurrentMeleeHandslot() {
        return "Leather gloves";
    }

    public static String getCurrentSword() {
        int level = Skills.getRealLevel(Skill.ATTACK);

        //if (level > 59)
            //return "Dragon sword";
        if (level > 39)
            return "Rune scimitar";
        else if (level > 29)
            return "Adamant scimitar";
        else if (level > 9)
            return "Black scimitar";
        else
            return "Iron scimitar";
    }

    public static String getCurrentMeleeBoots() {
        int level = Skills.getRealLevel(Skill.DEFENCE);

        if (TaskUtilities.currentTask.equals("Slay killerwatts"))
            return "Insulated boots";

        //if (level > 59)
            //return "Dragon boots";
        else
            return "Leather boots";
    }

    public static String getCurrentRangedHelm() {
        int rangeLevel = Skills.getRealLevel(Skill.RANGED);
        int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);

        if (rangeLevel > 20 && defenceLevel > 20)
            return "Coif";

        return "Leather cowl";
    }

    public static String getCurrentRangedBody() {
        int rangeLevel = Skills.getRealLevel(Skill.RANGED);
        int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);

        if (rangeLevel >= 70 && defenceLevel >= 70 && Utilities.isP2P)
            return "Black d'hide body";

        if (rangeLevel >= 60 && defenceLevel >= 60 && Utilities.isP2P)
            return "Red d'hide body";

        if (rangeLevel >= 60 && defenceLevel >= 40 && Utilities.isP2P)
            return "Blue d'hide body";

        if ((rangeLevel >= 20) && (defenceLevel >= 20))
            return "Studded body";

        return "Leather body";
    }

    public static String getCurrentRangedPants() {
        int rangeLevel = Skills.getRealLevel(Skill.RANGED);
        int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);

        if (rangeLevel >= 70 && defenceLevel >= 70 && Utilities.isP2P)
            return "Black d'hide chaps";

        if (rangeLevel >= 60 && defenceLevel >= 60 && Utilities.isP2P)
            return "Red d'hide chaps";

        if (rangeLevel >= 40 && defenceLevel >= 40 && Utilities.isP2P)
            return "Green d'hide chaps";

        if (rangeLevel >= 20  && defenceLevel >= 20)
            return "Studded chaps";

        return "Leather chaps";
    }

    public static String getCurrentRangedGloves() {
        int rangeLevel = Skills.getRealLevel(Skill.RANGED);
        int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);

        if (rangeLevel >= 70 && defenceLevel >= 70 && Utilities.isP2P)
            return "Black d'hide vambraces";

        if (rangeLevel >= 60 && defenceLevel >= 60 && Utilities.isP2P)
            return "Red d'hide vambraces";

        if (rangeLevel >= 40 && defenceLevel >= 40 && Utilities.isP2P)
            return "Green d'hide vambraces";

        return "Leather vambraces";
    }

    public static String getCurrentBow() {
        int level = Skills.getRealLevel(Skill.RANGED);

        if (level > 29)
            return "Maple shortbow";
        else if (level > 19)
            return "Willow shortbow";
        else if (level > 4)
            return "Oak shortbow";
        else
            return "Shortbow";
    }

    public static String getCurrentArrow() {
        int level = Skills.getRealLevel(Skill.RANGED);

        if (level > 19)
            return "Mithril arrow";
        else
            return "Iron arrow";
    }

    public static String getCurrentRangedOffhand() {
        int rangeLevel = Skills.getRealLevel(Skill.RANGED);
        int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);

        if (rangeLevel >= 70 && defenceLevel >= 70 && Utilities.isP2P)
            return "Black d'hide shield";

        if (rangeLevel >= 60 && defenceLevel >= 60 && Utilities.isP2P)
            return "Red d'hide shield";

        if (rangeLevel >= 40 && defenceLevel >= 40 && Utilities.isP2P)
            return "Green d'hide shield";
        else
            return getCurrentKiteshield();
    }

    public static String getCurrentMagicHelm() {
        return "Blue wizard hat";
    }

    public static String getCurrentMagicBody() {
        return "Blue wizard robe";
    }

    public static String getCurrentMagicPants() {
        return "Zamorak monk bottom";
    }

    public static String getCurrentMagicNecklace() {
        return "Amulet of magic";
    }

    public static String getCurrentMagicWeapon() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        if (level > 12)
            return "Staff of fire";

        return "Staff of air";
    }

    public static String getCurrentPickaxe() {
        int level = Skills.getRealLevel(Skill.MINING);
        if (level > 40)
            return "Rune pickaxe";
        if (level > 30)
            return "Adamant pickaxe";
        if (level > 20)
            return "Mithril pickaxe";
        if (level > 5)
            return "Steel pickaxe";

        return "Bronze pickaxe";
    }
}

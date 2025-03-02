package utils;

import constants.ItemNameConstants;
import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Sleep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtilities {
    public static ArrayList<GeItem> buyables = new ArrayList<>();
    public static ArrayList<String> sellables = new ArrayList<>();
    public static Tile lootTile = null;
    public static String currentFood = ItemNameConstants.SALMON;

    public static ArrayList<String> allSellables = new ArrayList<>(Arrays.asList("Logs", "Oak logs", "Willow logs", "Yew logs", "Ball of wool",
            "Wool", "Copper ore", "Iron ore", "Coal ore", "Soft clay", "Clay", "Cowhide", "Nature rune", "Limpwurt root", "Uncut sapphire", "Uncut emerald",
            "Uncut ruby", "Uncut diamond", "Big bones", "Goblin mail", "Chef's hat", "Brass necklace", "Black bead", "Red bead", "White bead", "Yellow bead",
            "Mind talisman", "Fiendish ashes", "Tuna", "Grimy harralander", "Grimy ranarr weed", "Grimy irit leaf", "Grimy avantoe", "Grimy kwuarm", "Grimy cadantine",
            "Grimy lantadyme", "Grimy dwarf weed", "Steel longsword", "Snape grass seed", "Ranarr seed", "Toadflax seed", "Avantoe seed", "Kwuarm seed",
            "Snapdragon seed", "Cadantine seed", "Dwarf weed seed", "Torstol seed", "Rune spear", "Dragon spear", "Black sq shield", "Mithril sword", "Steel kiteshield",
            "Steel bar", "Coal", "Uncut diamond", "Bronze spear", "Iron boots", "Mystic boots (light)", "Red spiders' eggs", "Rune dagger", "Mystic hat (light)",
            "Adamantite ore", "Fire battlestaff", "Air battlestaff", "Mystic fire staff", "Cannonball", "Fire orb", "Iron bar", "Bronze bar", "Mystic gloves (light)",
            "Iron bar", "Black robe", "Mithril ore", "Mithril sq shield", "Trout", "Silver bar"));

    public static ArrayList<String> phaseOneSellables = new ArrayList<>(Arrays.asList("Logs", "Ball of wool", "Wool", "Goblin mail", "Chef's hat", "Brass necklace",
            "Black bead", "Red bead", "White bead", "Yellow bead", "Blue wizard hat", "Mind talisman", "Fiendish ashes"));

    private static final List<String> dropables = new ArrayList<>(List.of("Vial"));

    public static void dropDropables() {
        if (Inventory.contains(i -> dropables.contains(i.getName()))) {
            if (Inventory.dropAll(i -> dropables.contains(i.getName())))
                Sleep.sleepUntil(() -> !Inventory.contains(i -> dropables.contains(i.getName())), Utilities.getRandomSleepTime());
        }
    }

    public static String getCurrentBait() {
        int level = Skills.getRealLevel(Skill.FISHING);
        if (level > 19)
            return "Feather";
        else
            return "";
    }

    public static void setCurrentFood() {
        if (!Utilities.isGeFullyOpen()) {
            if (Bank.contains(ItemNameConstants.SALMON) && Bank.count(ItemNameConstants.SALMON) > 10) {
                currentFood = ItemNameConstants.SALMON;
                return;
            }

            if (Bank.contains(ItemNameConstants.TROUT) && Bank.count(ItemNameConstants.TROUT) > 10) {
                currentFood = ItemNameConstants.TROUT;
                return;
            }

            if (Bank.contains(ItemNameConstants.ANCHOVIE) && Bank.count(ItemNameConstants.ANCHOVIE) > 10) {
                currentFood = ItemNameConstants.ANCHOVIE;
                return;
            }

            if (Bank.contains(ItemNameConstants.SHRIMP) && Bank.count(ItemNameConstants.SHRIMP) > 10) {
                currentFood = ItemNameConstants.SHRIMP;
                return;
            }

            TaskUtilities.endCurrentTask();
            return;
        }

        currentFood = Utilities.isP2P ? ItemNameConstants.SWORDFISH : ItemNameConstants.SALMON;
    }
}
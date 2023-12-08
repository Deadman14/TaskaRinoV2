package utils;

import models.GeItem;
import org.dreambot.api.methods.map.Tile;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemUtilities {
    public static boolean sellablesAboveThreshold = false;

    public static String currentFood = "Pike";

    public static ArrayList<GeItem> buyables = new ArrayList<>();

    public static ArrayList<String> sellables = new ArrayList<>();

    public static Tile lootTile = null;

    public static ArrayList<String> allSellables = new ArrayList<>(Arrays.asList("Logs", "Oak logs", "Willow logs", "Yew logs", "Ball of wool",
            "Wool", "Copper ore", "Iron ore", "Coal ore", "Raw shrimps", "Raw anchovies", "Raw trout", "Raw salmon", "Soft clay", "Clay",
            "Cowhide", "Nature rune", "Limpwurt root", "Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond", "Big bones",
            "Goblin mail", "Chef's hat", "Brass necklace", "Black bead", "Red bead", "White bead", "Yellow bead", "Mind talisman",
            "Fiendish ashes", "Tuna", "Raw tuna", "Grimy harralander", "Grimy ranarr weed", "Grimy irit leaf", "Grimy avantoe",
            "Grimy kwuarm", "Grimy cadantine", "Grimy lantadyme", "Grimy dwarf weed", "Steel longsword", "Snape grass seed", "Ranarr seed",
            "Toadflax seed", "Avantoe seed", "Kwuarm seed", "Snapdragon seed", "Cadantine seed", "Dwarf weed seed", "Torstol seed",
            "Rune spear", "Dragon spear", "Black sq shield", "Mithril sword", "Steel kiteshield", "Steel bar", "Coal", "Uncut diamond",
            "Bronze spear", "Iron boots", "Mystic boots (light)", "Red spiders' eggs", "Rune dagger", "Mystic hat (light)",
            "Adamantite ore", "Fire battlestaff", "Air battlestaff", "Mystic fire staff", "Cannonball", "Fire orb", "Iron bar",
            "Bronze bar", "Mystic gloves (light)", "Iron bar", "Black robe", "Mithril ore"));

    public static ArrayList<String> phaseOneSellables = new ArrayList<>(Arrays.asList("Logs", "Ball of wool", "Wool", "Goblin mail", "Chef's hat",
            "Brass necklace", "Black bead", "Red bead", "White bead", "Yellow bead", "Blue wizard hat", "Mind talisman", "Fiendish ashes"));
}
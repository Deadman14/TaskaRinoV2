package nodes;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;
import utils.EquipmentUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootNode extends TaskNode {
    List<String> nodesTasks = new ArrayList<>(Arrays.asList("Train Combat Melee", "Train Combat Range", "Slay kalphite", "Slay ogres",
            "Slay ice warriors", "Slay moss giants", "Slay ice giants", "Slay crocodiles", "Slay hobgoblins", "Slay cockatrice"));

    public List<String> lootables = new ArrayList<>(Arrays.asList("Coins", "Iron arrow", "Mithril arrow", "Cowhide", "Law rune", "Nature rune",
            "Death rune", "Chaos rune", "Cosmic rune", "Blood rune", "Limpwurt root", "Giant key", "Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond",
            "Big bones", "Goblin mail", "Chef's hat", "Brass necklace", "Black bead", "Red bead", "White bead", "Yellow bead", "Blue wizard hat",
            "Mind talisman", "Fiendish ashes", "Grimy harralander", "Grimy ranarr weed", "Grimy irit leaf", "Grimy avantoe", "Grimy kwuarm",
            "Grimy cadantine", "Grimy lantadyme", "Grimy dwarf weed", "Steel longsword", "Snape grass seed", "Ranarr seed", "Toadflax seed",
            "Avantoe seed", "Kwuarm seed", "Snapdragon seed", "Cadantine seed", "Dwarf weed seed", "Torstol seed", "Ensouled ogre head",
            "Rune spear", "Dragon spear", "Loop half of key", "Tooth half of key", "Black sq shield", "Mithril sword", "Steel kiteshield",
            "Steel bar", "Coal", "Uncut diamond", "Bronze spear", "Iron boots", "Mystic boots (light)"));


    @Override
    public int execute() {
        Utilities.currentNode = "LootNode";
        Logger.log("Loot");

        if (ItemUtilities.lootTile == null || Utilities.isP2P) {
            GroundItem loot = GroundItems.closest(item -> (lootables.contains(item.getName()) && (!item.getName().contains("arrow")
                    || item.getName().contains("arrow") && item.getAmount() > 5)));
            if (loot != null) {
                if (loot.canReach()) {
                    if (loot.interact("Take"))
                        Sleep.sleepUntil(() -> !loot.exists(), Utilities.getRandomSleepTime());
                } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                    Walking.walk(loot.getTile());
                }
            } else {
                ItemUtilities.lootTile = null;
            }
        } else {
            if (ItemUtilities.lootTile != null) {
                Area lootArea = ItemUtilities.lootTile.getArea(2);
                GroundItem loot = GroundItems.closest(item -> (lootables.contains(item.getName()) && (!item.getName().contains("arrow")
                        || item.getName().contains("arrow") && item.getAmount() > 5)) && lootArea.contains(item));
                if (loot != null) {
                    if (loot.canReach()) {
                        if (loot.interact("Take"))
                            Sleep.sleepUntil(() -> !loot.exists(), Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Calculations.random(3, 6))) {
                        Walking.walk(loot.getTile());
                    }
                } else {
                    ItemUtilities.lootTile = null;
                }
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return (!Inventory.isFull() && Equipment.containsAll(EquipmentUtilities.requiredEquipment) &&
                !GroundItems.all(item -> ((lootables.contains(item.getName()) && (!item.getName().contains("arrow") || item.getName().contains("arrow") && item.getAmount() > 5)) && item.distance(Players.getLocal()) < 7)).isEmpty()
                && nodesTasks.contains(TaskUtilities.currentTask)) && Utilities.shouldLoot;
    }

    @Override
    public int priority() {
        return 2;
    }
}

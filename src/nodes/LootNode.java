package nodes;

import constants.NpcNameConstants;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootNode extends TaskNode {

    public List<String> lootables = new ArrayList<>(Arrays.asList("Coins", "Iron arrow", "Mithril arrow", "Cowhide", "Law rune", "Nature rune",
            "Death rune", "Chaos rune", "Cosmic rune", "Blood rune", "Limpwurt root", "Giant key", "Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond",
            "Big bones", "Goblin mail", "Chef's hat", "Brass necklace", "Black bead", "Red bead", "White bead", "Yellow bead", "Blue wizard hat",
            "Mind talisman", "Fiendish ashes", "Grimy harralander", "Grimy ranarr weed", "Grimy irit leaf", "Grimy avantoe", "Grimy kwuarm",
            "Grimy cadantine", "Grimy lantadyme", "Grimy dwarf weed", "Steel longsword", "Snape grass seed", "Ranarr seed", "Toadflax seed",
            "Avantoe seed", "Kwuarm seed", "Snapdragon seed", "Cadantine seed", "Dwarf weed seed", "Torstol seed", "Ensouled ogre head",
            "Rune spear", "Dragon spear", "Loop half of key", "Tooth half of key", "Black sq shield", "Mithril sword", "Steel kiteshield",
            "Steel bar", "Coal", "Uncut diamond", "Bronze spear", "Iron boots", "Mystic boots (light)", "Red spiders' eggs",
            "Adamant full helm", "Mithril kiteshield", "Rune dagger", "Mystic hat (light)", "Adamantite ore", "Staff of fire",
            "Staff of air", "Fire battlestaff", "Air battlestaff", "Mystic fire staff", "Cannonball", "Fire orb", "Coal", "Iron bar",
            "Bronze bar", "Mystic gloves (light)", "Iron bar", "Black robe", "Mithril ore", "Rune scimitar", "Mithril sq shield", "Rune arrow"));

    @Override
    public int execute() {
        Logger.log("- Loot -");

        GroundItem onTileItem = GroundItems.closest(i -> ItemUtilities.lootTile.getArea(0).contains(i));
        if (onTileItem == null) {
            Sleep.sleepUntil(() -> GroundItems.closest(i -> ItemUtilities.lootTile.getArea(0).contains(i)) != null, Utilities.getRandomSleepTime());
        }

        GroundItem loot = GroundItems.closest(item -> (lootables.contains(item.getName()) && (!item.getName().contains("arrow")
                || item.getName().contains("arrow") && item.getAmount() > 5)) && getLootArea().contains(item));
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

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return ItemUtilities.lootTile != null && !Players.getLocal().isInCombat() && !Inventory.isFull()
                && Equipment.containsAll(EquipmentUtilities.requiredEquipment);
    }

    @Override
    public int priority() {
        return 2;
    }

    private Area getLootArea() {
        String monster = SlayerUtilities.getCurrentCombatTrainingNpc();

        if (monster.equals(NpcNameConstants.COW))
            return ItemUtilities.lootTile.getArea(1);
        else
            return ItemUtilities.lootTile.getArea(7);
    }
}

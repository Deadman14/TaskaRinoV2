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
    List<String> nodesTasks = new ArrayList<>(Arrays.asList("Train Combat Melee", "Train Combat Range", "Train Combat Magic",
            "Slay kalphite", "Slay ogres", "Slay ice warriors", "Slay moss giants", "Slay ice giants", "Slay crocodiles",
            "Slay hobgoblins", "Slay cockatrice", "Slay wall beasts", "Slay cave bugs", "Slay basilisks", "Slay killerwatts",
            "Slay pyrefiends", "Slay rockslugs", "Slay cave slimes", "Slay ankou", "Slay cave crawlers", "Slay hill giants",
            "Slay fire giants", "Slay lesser demons", "Slay lizards", "Slay jellies", "Kill Imps"));

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
        if (onTileItem == null)
            return Utilities.getRandomExecuteTime();

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
        return (!Inventory.isFull() && Equipment.containsAll(EquipmentUtilities.requiredEquipment) &&
                !GroundItems.all(item -> ((lootables.contains(item.getName()) && (!item.getName().contains("arrow") || item.getName().contains("arrow") && item.getAmount() > 5)) && item.distance(Players.getLocal()) < 7)).isEmpty()
                && nodesTasks.contains(TaskUtilities.currentTask)) && Utilities.shouldLoot && ItemUtilities.lootTile != null && !Players.getLocal().isInCombat();
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

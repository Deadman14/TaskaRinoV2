package nodes.skilling;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import utils.*;

public class MineNode extends TaskNode {
    private String currentPickaxe = "Bronze pickaxe";
    private final static Area COPPER_AREA = new Area(3220, 3152, 3232, 3143);
    private final static Area IRON_AREA_1 = new Area(3281, 3370, 3289, 3362);
    private final static Area MINING_GUILD = new Area(3024, 9753, 3053, 9732);

    private static String currentOre = "";

    @Override
    public int execute() {
        Utilities.currentNode = "MineNode";
        Logger.log("Mine");

        if (currentOre.isEmpty())
            currentOre = getCurrentOre();

        if (Dialogues.inDialogue())
            Dialogues.continueDialogue();

        if ((Inventory.contains(currentPickaxe) || Equipment.contains(currentPickaxe)) && !Inventory.isFull()) {
            if (getCurrentArea().contains(Players.getLocal())) {
                if (!Equipment.contains(currentPickaxe) && canEquipCurrentPickaxe()) {
                    if (Inventory.interact(currentPickaxe))
                        Sleep.sleepUntil(() -> Equipment.contains(currentPickaxe), Utilities.getRandomSleepTime());
                }

                GameObject ore = GameObjects.closest(o -> o != null && o.getName().equals(currentOre) && o.exists() && getCurrentArea().contains(o));
                if (ore != null && ore.exists()) {
                    if (ore.canReach()) {
                        if (ore.interact())
                            Sleep.sleepUntil(() -> !ore.exists() || Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Walking.walk(ore.getTile());
                    }
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                Walking.walk(getCurrentArea().getRandomTile());
            }
        } else {
            if (Bank.isOpen()) {
                if (!currentPickaxe.equals(EquipmentUtilities.getCurrentPickaxe())) {
                    currentPickaxe = EquipmentUtilities.getCurrentPickaxe();
                }

                if (Inventory.isFull())
                    currentOre = getCurrentOre();

                if (Inventory.isFull() || !Inventory.isEmpty() && !Inventory.contains(currentPickaxe)) {
                    if (Bank.depositAllExcept(currentPickaxe))
                        Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
                }

                if (!Inventory.contains(currentPickaxe) && !Equipment.contains(currentPickaxe)) {
                    if (Bank.contains(EquipmentUtilities.getCurrentPickaxe())) {
                        BankUtilities.setBankMode(BankMode.ITEM);
                        if (Bank.withdraw(currentPickaxe))
                            Sleep.sleepUntil(() -> Inventory.contains(currentPickaxe), Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(EquipmentUtilities.getCurrentPickaxe(), 1, LivePrices.getHigh(currentPickaxe)));
                    }
                }
            } else {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Mining");
    }

    private boolean canEquipCurrentPickaxe() {
        int level = Skills.getRealLevel(Skill.ATTACK);

        if (currentPickaxe.equals("Rune pickaxe") && level >= 40)
            return true;
        if (currentPickaxe.equals("Adamant pickaxe") && level >= 30)
            return true;
        if (currentPickaxe.equals("Mithril pickaxe") && level >= 20)
            return true;
        if (currentPickaxe.equals("Steel pickaxe") && level >= 5)
            return true;

        return currentPickaxe.equals("Bronze pickaxe");
    }

    private Area getCurrentArea() {
        int level = Skills.getRealLevel(Skill.MINING);

        if (level > 59)
            return MINING_GUILD;

        if (level > 14)
            return IRON_AREA_1;

        return COPPER_AREA;
    }

    public static String getCurrentOre() {
        int level = Skills.getRealLevel(Skill.MINING);

        if (level > 59)
            return "Coal rocks";

        if (level > 14)
            return "Iron rocks";

        return currentOre.equals("Tin rocks") ? "Copper rocks" : "Tin rocks";
    }
}

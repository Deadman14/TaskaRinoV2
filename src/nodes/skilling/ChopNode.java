package nodes.skilling;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
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
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class ChopNode extends TaskNode {
    private String currentAxe = "Bronze axe";
    private final Area normalTreeArea = new Area(3150, 3449, 3171, 3465);
    private final Area oakTreeArea = new Area(3109, 3409, 3132, 3438);
    private final Area willowTreeArea = new Area(3080, 3239, 3093, 3226);
    private final Area yewTreeArea = new Area(3045, 3324, 2994, 3310);

    @Override
    public int execute() {
        Utilities.currentNode = "ChopNode";
        Logger.log("Chop");

        if (Dialogues.inDialogue()) {
            Dialogues.continueDialogue();
        }

        if ((Inventory.contains(currentAxe) || Equipment.contains(currentAxe)) && !Inventory.isFull()) {
            if (getCurrentArea().contains(Players.getLocal())) {
                if (!Equipment.contains(currentAxe) && canEquipCurrentAxe()) {
                    if (Inventory.interact(currentAxe))
                        Sleep.sleepUntil(() -> Equipment.contains(currentAxe), Utilities.getRandomSleepTime());
                }

                GameObject tree = GameObjects.closest(getCurrentTree());
                if (!getCurrentArea().contains(tree)) {
                    if (Walking.shouldWalk(Calculations.random(3, 6)))
                        Walking.walk(getCurrentArea().getCenter());
                    return Utilities.getRandomExecuteTime();
                }

                if (tree != null && tree.exists()) {
                    if (tree.canReach()) {
                        if (tree.interact())
                            Sleep.sleepUntil(() -> !tree.exists() || Dialogues.inDialogue(), Utilities.getRandomSleepTime() + 60000);
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        Walking.walk(tree.getTile());
                    }
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                Walking.walk(getCurrentArea().getRandomTile());
            }
        } else {
            if (Bank.isOpen()) {
                if (!currentAxe.equals(getCurrentAxe())) {
                    currentAxe = getCurrentAxe();
                }

                if (Inventory.isFull() || !Inventory.isEmpty() && !Inventory.onlyContains(currentAxe)) {
                    if (Bank.depositAllExcept(currentAxe))
                        Sleep.sleepUntil(() -> !Inventory.isFull(), Utilities.getRandomSleepTime());
                }

                if (!Inventory.contains(currentAxe) && !Equipment.contains(currentAxe)) {
                    BankUtilities.setBankMode(BankMode.ITEM);

                    if (Bank.contains(currentAxe)) {
                        if (Bank.withdraw(currentAxe))
                            Sleep.sleepUntil(() -> Inventory.contains(currentAxe), Utilities.getRandomSleepTime());
                    } else {
                        ItemUtilities.buyables.add(new GeItem(currentAxe, 1, LivePrices.getHigh(currentAxe)));
                    }
                }
            } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Woodcutting");
    }

    private String getCurrentTree() {
        int level = Skills.getRealLevel(Skill.WOODCUTTING);

        if (level > 59 )
            return "Yew tree";
        if (level > 29 && Players.getLocal().getLevel() > 14)
            return "Willow tree";
        if (level > 14)
            return "Oak tree";

        return "Tree";
    }

    private Area getCurrentArea() {
        int level = Skills.getRealLevel(Skill.WOODCUTTING);

        if (level > 59)
            return yewTreeArea;
        if (level > 29 && Players.getLocal().getLevel() > 14)
            return willowTreeArea;
        if (level > 14)
            return oakTreeArea;

        return normalTreeArea;
    }

    private String getCurrentAxe() {
        int level = Skills.getRealLevel(Skill.WOODCUTTING);

        if (level > 40 )
            return "Rune axe";
        if (level > 30)
            return "Adamant axe";
        if (level > 20)
            return "Mithril axe";
        if (level > 5)
            return "Steel axe";

        return "Bronze axe";
    }

    private boolean canEquipCurrentAxe() {
        int level = Skills.getRealLevel(Skill.ATTACK);

        if (currentAxe.equals("Rune axe") && level >= 40)
            return true;
        if (currentAxe.equals("Adamant axe") && level >= 30)
            return true;
        if (currentAxe.equals("Mithril axe") && level >= 20)
            return true;
        if (currentAxe.equals("Steel axe") && level >= 5)
            return true;

        return currentAxe.equals("Bronze axe");
    }
}

package nodes;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import utils.EquipmentUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

public class NewTaskNode extends TaskNode {

    @Override
    public int execute() {
        Utilities.currentNode = "NewTaskNode";
        Logger.log("New Task");

        Utilities.isP2P = isReadyForP2P();
        if (Utilities.isP2P)
            ItemUtilities.currentFood = "Swordfish";
        TaskUtilities.currentTask = TaskUtilities.nextTask();
        TaskUtilities.taskTimer = new Timer(Calculations.random(1800000, 3600000));
        TaskUtilities.taskTimer.start();
        EquipmentUtilities.setRequiredEquipment();

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.isEmpty() || TaskUtilities.taskTimer.remaining() <= 0;
    }

    @Override
    public int priority() {
        return 5;
    }

    private boolean isReadyForP2P() {
        return Skills.getRealLevel(Skill.STRENGTH) >= 40 && Skills.getRealLevel(Skill.ATTACK) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 40
                && Skills.getRealLevel(Skill.RANGED) >= 40 && Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.FISHING) >= 40
                && Skills.getRealLevel(Skill.MINING) >= 40 && Utilities.isGeFullyOpen();
    }
}

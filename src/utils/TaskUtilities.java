package utils;

import constants.TaskNameConstants;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TaskUtilities {

    public static String[] totalTasks = {TaskNameConstants.COOKS_ASSISTANT, TaskNameConstants.TRAIN_COMBAT_MELEE, TaskNameConstants.TRAIN_COMBAT_RANGE,
            TaskNameConstants.MINING, TaskNameConstants.WOODCUTTING, TaskNameConstants.MAKE_SOFT_CLAY, TaskNameConstants.SHEAR_SHEEP, TaskNameConstants.FISHING,
            TaskNameConstants.RESTLESS_GHOST, TaskNameConstants.KILL_IMPS, TaskNameConstants.TRAIN_COMBAT_MAGIC, TaskNameConstants.DORICS_QUEST, TaskNameConstants.IMP_CATCHER,
            TaskNameConstants.RUNE_MYSTERIES, TaskNameConstants.X_MARKS_THE_SPOT, TaskNameConstants.NATURAL_HISTORY_QUIZ, TaskNameConstants.SLAYER, TaskNameConstants.SMITH,
            TaskNameConstants.ERNEST_THE_CHICKEN, TaskNameConstants.SHEEP_SHEARER };

    public static String[] preGeTasks = { TaskNameConstants.MINING, TaskNameConstants.WOODCUTTING, TaskNameConstants.SHEAR_SHEEP, TaskNameConstants.FISHING,
            TaskNameConstants.RESTLESS_GHOST, TaskNameConstants.KILL_IMPS, TaskNameConstants.TRAIN_COMBAT_MELEE, TaskNameConstants.COOKS_ASSISTANT,
            TaskNameConstants.DORICS_QUEST, TaskNameConstants.IMP_CATCHER, TaskNameConstants.RUNE_MYSTERIES, TaskNameConstants.X_MARKS_THE_SPOT,
            TaskNameConstants.ERNEST_THE_CHICKEN, TaskNameConstants.SHEEP_SHEARER };

    public static String currentTask = "";

    public static String previousTask = "";

    public static Timer taskTimer = new Timer();

    //TODO: hint: different base weight shifts depending on how close you are to the target or if you are passed target
    //TODO: seed how long you are doing task for
    public static String nextTask() {
        ArrayList<String> tasks = new ArrayList(){};
        int highestWeight = 0;

        for (String task : Utilities.isGeFullyOpen() ? totalTasks : preGeTasks) {
            if (task.equals(previousTask) || shouldRemoveTask(task))
                continue;

            tasks.add(task);
        }

        for (String task : tasks) {
            int weight = getSeededValue(task, 500, -250, 250);
            Logger.log(task + ": " + weight);

            if (highestWeight < weight)
                highestWeight = weight;
        }

        Collections.shuffle(tasks);
        int weightCheck = new Random().nextInt(highestWeight);

        for (String task : tasks) {
            int weight = getSeededValue(task, 500, -250, 250);

            if (weightCheck <= weight) {
                previousTask = task;
                return task;
            }
        }

        return "";
    }

    public static int getSeededValue(String task, int base, int min, int max) {
        int seed = generateSeed(task);

        return base + (new Random(seed).nextInt(max + 1 - min) + min);
    }

    private static int generateSeed(String task) {
        String email = Client.getUsername();

        String seedString = email + task;
        int seed = 0;
        for (char c : seedString.toCharArray()) {
            seed += (int)c;
        }

        return seed;
    }

    //TODO:  change to can do and check if you can do task based on ge being open - only mage and range after ge open
    private static boolean shouldRemoveTask(String task) {
        switch (task) {
            case TaskNameConstants.COOKS_ASSISTANT:
                return FreeQuest.COOKS_ASSISTANT.isFinished();
            case TaskNameConstants.RESTLESS_GHOST:
                return FreeQuest.THE_RESTLESS_GHOST.isFinished() || Players.getLocal().getLevel() < 10;
            case TaskNameConstants.DORICS_QUEST:
                return FreeQuest.DORICS_QUEST.isFinished();
            case TaskNameConstants.IMP_CATCHER:
                return FreeQuest.IMP_CATCHER.isFinished();
            case TaskNameConstants.RUNE_MYSTERIES:
                return FreeQuest.RUNE_MYSTERIES.isFinished();
            case TaskNameConstants.ERNEST_THE_CHICKEN:
                return FreeQuest.ERNEST_THE_CHICKEN.isFinished();
            case TaskNameConstants.X_MARKS_THE_SPOT:
                return FreeQuest.X_MARKS_THE_SPOT.isFinished();
            case TaskNameConstants.NATURAL_HISTORY_QUIZ:
                return !Utilities.isP2P || Skills.getRealLevel(Skill.SLAYER) >= 9;
            case TaskNameConstants.SLAYER:
                return !Utilities.isP2P || Skills.getRealLevel(Skill.SLAYER) < 9;
            case TaskNameConstants.SHEAR_SHEEP:
            case TaskNameConstants.TRAIN_COMBAT_MELEE:
            case TaskNameConstants.TRAIN_COMBAT_RANGE:
            case TaskNameConstants.TRAIN_COMBAT_MAGIC:
            case TaskNameConstants.WOODCUTTING:
            case TaskNameConstants.MAKE_SOFT_CLAY:
            case TaskNameConstants.KILL_IMPS:
                return Utilities.isP2P;
            case TaskNameConstants.SMITH:
                return Skills.getRealLevel(Skill.SMITHING) < 29 || Skills.getRealLevel(Skill.SMITHING) >= 60;
            default:
                return false;
        }
    }
}

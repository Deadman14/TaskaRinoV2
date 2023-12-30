package utils;

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

    public static String[] totalTasks = {"Cooks Assistant", "Train Combat Melee", "Train Combat Range", "Mining", "Woodcutting", "Romeo And Juliet",
            "Make Soft Clay", "Shear Sheep", "Fishing", "Goblin Diplomacy", "Restless Ghost", "Kill Imps", "Train Combat Magic", "Doric's Quest",
            "Imp Catcher", "Rune Mysteries", "Ernest The Chicken", "X Mark's The Spot", "Knights Sword", "Natural History Quiz", "Slayer", "Smith"};

    public static String[] preGeTasks = { "Goblin Diplomacy", "Mining", "Woodcutting", "Romeo And Juliet", "Shear Sheep", "Fishing", "Restless Ghost",
            "Kill Imps", "Train Combat Melee", "Cooks Assistant", "Doric's Quest", "Imp Catcher", "Rune Mysteries", "Ernest The Chicken",
            "X Mark's The Spot", "Knights Sword"};

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
            case "Cooks Assistant":
                return FreeQuest.COOKS_ASSISTANT.isFinished();
            case "Romeo And Juliet":
                return FreeQuest.ROMEO_AND_JULIET.isFinished();
            case "Goblin Diplomacy":
                return FreeQuest.GOBLIN_DIPLOMACY.isFinished();
            case "Restless Ghost":
                return FreeQuest.THE_RESTLESS_GHOST.isFinished() || Players.getLocal().getLevel() < 10;
            case "Doric's Quest":
                return FreeQuest.DORICS_QUEST.isFinished();
            case "Imp Catcher":
                return FreeQuest.IMP_CATCHER.isFinished();
            case "Rune Mysteries":
                return FreeQuest.RUNE_MYSTERIES.isFinished();
            case "Ernest The Chicken":
                return FreeQuest.ERNEST_THE_CHICKEN.isFinished();
            case "Knights Sword":
                return FreeQuest.THE_KNIGHTS_SWORD.isFinished() || Players.getLocal().getLevel() < 20
                        || Skills.getRealLevel(Skill.MINING) < 10;
            case "X Mark's The Spot":
                return FreeQuest.X_MARKS_THE_SPOT.isFinished();
            case "Natural History Quiz":
                return !Utilities.isP2P || Skills.getRealLevel(Skill.SLAYER) >= 9;
            case"Slayer":
                return !Utilities.isP2P || Skills.getRealLevel(Skill.SLAYER) < 9;
            case "Shear Sheep":
            case "Train Combat Melee":
            case "Train Combat Range":
            case "Train Combat Magic":
            case "Woodcutting":
            case "Make Soft Clay":
            case "Kill Imps":
                return Utilities.isP2P;
            case "Smith":
                return Skills.getRealLevel(Skill.SMITHING) < 29 || Skills.getRealLevel(Skill.SMITHING) >= 60;
            default:
                return false;
        }
    }
}

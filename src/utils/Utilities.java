package utils;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class Utilities {
    public static String currentNode = "";
    public static boolean shouldLoot = false;
    //TODO: Change back to false
    public static boolean isP2P = false;

    public static boolean isGeFullyOpen() {
        int totalXp = 0;
        for (int xp : Skills.getExperience()) {
            totalXp += xp;
        }

        return Skills.getTotalLevel() > 99 && Quests.getQuestPoints() > 9 && totalXp > 130000;
    }

    public static int getRandomSleepTime() {
        Logger.log("Sleeping");
        return Calculations.random(7000, 10000);
    }

    public static int getRandomExecuteTime() {
        return Calculations.random(120, 300);
    }

    public static int getShouldWalkDistance() { return Calculations.random(3, 6); }

    public static boolean canBuyItem(String item, int geAmount) {
        return Bank.get("Coins").getAmount() > (LivePrices.getHigh(item) * 2.5) * geAmount;
    }

    public static void walkToArea(Area area) {
        Logger.log("-- Walking To Area --");

        if (Walking.shouldWalk(getShouldWalkDistance())) {
            if (Walking.getRunEnergy() > Calculations.random(20, 40) && !Walking.isRunEnabled())
                Walking.toggleRun();

            Walking.walk(area.getRandomTile());
        }
    }

    public static void closeGeAndBank() {
        if (GrandExchange.isOpen()) {
            if (GrandExchange.close())
                Sleep.sleepUntil(() -> !GrandExchange.isOpen(), Utilities.getRandomSleepTime());
        }

        if (Bank.isOpen()) {
            if (Bank.close())
                Sleep.sleepUntil(() -> !Bank.isOpen(), Utilities.getRandomSleepTime());
        }
    }
}

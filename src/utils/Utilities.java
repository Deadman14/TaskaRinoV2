package utils;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class Utilities {
    public static String currentNode = "";
    public static boolean isP2P = false;
    public static Integer timePlayed = -1;
    public static boolean hasDied = false;
    public static Tile playerDeathTile = null;

    public static boolean isGeFullyOpen() {
        Logger.log("-- Time Played: " + timePlayed + "Hours --");
        return Skills.getTotalLevel() > 99 && Quests.getQuestPoints() > 9 && timePlayed >= 20;
    }

    public static int getRandomSleepTime() {
        Logger.log("Sleeping");
        return Calculations.random(5000, 11000);
    }

    public static int getRandomExecuteTime() {
        return Calculations.random(100, 400);
    }

    public static int getShouldWalkDistance() { return Calculations.random(5, 11); }

    public static void walkToArea(Area area) {
        Logger.log("-- Walking To Area --");

        if (Walking.shouldWalk(getShouldWalkDistance())) {
            if (Walking.getRunEnergy() > Calculations.random(20, 40) && !Walking.isRunEnabled())
                Walking.toggleRun();

            Walking.walk(area.getRandomTile());
        }
    }

    public static void closeInterfaces() {
        if (GrandExchange.isOpen()) {
            if (GrandExchange.close())
                Sleep.sleepUntil(() -> !GrandExchange.isOpen(), Utilities.getRandomSleepTime());
        }

        if (Bank.isOpen()) {
            if (Bank.close())
                Sleep.sleepUntil(() -> !Bank.isOpen(), Utilities.getRandomSleepTime());
        }

        if (Shop.isOpen()) {
            if (Shop.close())
                Sleep.sleepUntil(() -> !Shop.isOpen(), Utilities.getRandomSleepTime());
        }
    }
}

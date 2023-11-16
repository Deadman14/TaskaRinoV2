package nodes;

import models.GeItem;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import utils.BankUtilities;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.ArrayList;

public class GeNode extends TaskNode {
    private Area geArea = new Area(3151, 3501, 3177, 3478);
    private boolean canBuyItems = false;

    private boolean checkedBank = false;

    @Override
    public int execute() {
        Utilities.currentNode = "GeNode";
        Logger.log("Ge");

        Logger.log("BUYABLES: " + ItemUtilities.buyables.size());
        Logger.log("SELLABLES: " + ItemUtilities.sellables.size());

        if (!checkedBank) {
            if (Bank.isOpen()) {
                if (Utilities.isGeFullyOpen()) {
                    ItemUtilities.sellables = new ArrayList<>(Bank.all(i -> i != null && ItemUtilities.allSellables.contains(i.getName())).stream().map(Item::getName).toList());
                } else {
                    ItemUtilities.phaseOneSellables = new ArrayList<>(Bank.all(i -> i != null && ItemUtilities.phaseOneSellables.contains(i.getName())).stream().map(Item::getName).toList());
                }

                int totalCoins = Bank.count("Coins");
                for (String i : ItemUtilities.sellables) {
                    totalCoins += (int) (LivePrices.getLow(i) * 0.8) + 1;
                }

                int totalBuyPrice = 0;
                for(GeItem i : ItemUtilities.buyables) {
                    totalBuyPrice += (LivePrices.getHigh(i.getName()) * i.getAmount()) + 1;
                }

                if (totalCoins <= totalBuyPrice * 2.5) {
                    TaskUtilities.currentTask = "";
                    ItemUtilities.buyables = new ArrayList<>();
                    checkedBank = false;
                    return Utilities.getRandomExecuteTime();
                }

                checkedBank = true;
            }   else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                BankUtilities.openBank();
            }
        }

        if (geArea.contains(Players.getLocal()) && checkedBank) {
            if (ItemUtilities.sellables.isEmpty()) {
                Logger.log(ItemUtilities.buyables.get(0));

                ItemUtilities.sellablesAboveThreshold = false;

                if (Inventory.getEmptySlots() >= ItemUtilities.buyables.size() && canBuyItems) {
                    if (GrandExchange.isOpen()) {
                        for (GeItem i : ItemUtilities.buyables) {
                            if (GrandExchange.getOpenSlots() > 0 && i != null) {
                                if (GrandExchange.buyItem(i.getName(), i.getAmount(), i.getPrice()))
                                    Sleep.sleepUntil(() -> GrandExchange.contains(i.getName()), Utilities.getRandomSleepTime());
                            }

                            if (GrandExchange.isReadyToCollect()) {
                                if (GrandExchange.collect())
                                    Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                            }
                        }

                        Sleep.sleep(Utilities.getRandomSleepTime());

                        if (GrandExchange.isReadyToCollect()) {
                            if (GrandExchange.collect())
                                Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                        }

                        ItemUtilities.buyables.removeIf(i -> Inventory.contains(i.getName())
                                && Inventory.get(i.getName()).getAmount() >= i.getAmount()
                                && !GrandExchange.contains(i.getName()));

                        for (GeItem i : ItemUtilities.buyables) {
                            if (GrandExchange.contains(i.getName())) {
                                int slot = GrandExchange.getItem(i.getName()).getSlot();
                                if (GrandExchange.cancelOffer(slot)) {
                                    if (Inventory.contains(i.getName()))
                                        i.setAmount(i.getAmount() - Inventory.get(i.getName()).getAmount());
                                    i.setPrice((int)(i.getPrice() * 1.10) + 1);
                                    Sleep.sleepUntil(GrandExchange::isReadyToCollect, Utilities.getRandomSleepTime());
                                }
                            }

                            if (GrandExchange.isReadyToCollect()) {
                                if (GrandExchange.collect())
                                    Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                            }
                        }
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        if (GrandExchange.open())
                            Sleep.sleepUntil(GrandExchange::isOpen, Utilities.getRandomSleepTime());
                    }
                } else {
                    if (Bank.isOpen()) {
                        if (!BankUtilities.canBuyItemsForTask()) {
                            TaskUtilities.currentTask = "";
                            ItemUtilities.buyables = new ArrayList<>();
                            canBuyItems = false;
                            checkedBank = false;
                            return Utilities.getRandomExecuteTime();
                        } else {
                            canBuyItems = true;
                        }

                        if (Bank.depositAllItems())
                            Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());

                        ItemUtilities.buyables.removeIf(i -> Bank.contains(i.getName()) && Bank.count(i.getName()) >= i.getAmount());
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        BankUtilities.openBank();
                    }
                }
            } else {
                if (Inventory.containsAll(ItemUtilities.sellables)) {
                    if (GrandExchange.isOpen()) {
                        ArrayList<String> remove = new ArrayList<>();
                        for (String i : ItemUtilities.sellables) {
                            if (GrandExchange.getOpenSlots() > 0) {
                                Item item = Inventory.get(it -> it != null && it.getName().equals(i));
                                Widget yesMenu = Widgets.getWidget(289);

                                if (GrandExchange.sellItem(i, item.getAmount(), 1)) {
                                    remove.add(i);
                                    Sleep.sleepUntil(() -> GrandExchange.contains(i) || yesMenu.isVisible(), Utilities.getRandomSleepTime());

                                    if (yesMenu != null && yesMenu.isVisible()) {
                                        WidgetChild yesButton = yesMenu.getChild(8);
                                        if (yesButton.interact())
                                            Sleep.sleepUntil(() -> !yesMenu.isVisible(), Utilities.getRandomSleepTime());
                                    }
                                }
                            } else {
                                break;
                            }
                        }

                        ItemUtilities.sellables.removeAll(remove);

                        Sleep.sleep(Utilities.getRandomSleepTime());

                        if (GrandExchange.isReadyToCollect()) {
                            if (GrandExchange.collect())
                                Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                        }
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        if (GrandExchange.open())
                            Sleep.sleepUntil(GrandExchange::isOpen, Utilities.getRandomSleepTime());
                    }
                } else {
                    if (Bank.isOpen()) {
                        BankUtilities.setBankMode(BankMode.NOTE);

                        if (!Inventory.isEmpty()) {
                            if (Bank.depositAllItems())
                                Sleep.sleepUntil(Bank::isEmpty, Utilities.getRandomSleepTime());
                        }

                        ItemUtilities.sellables.removeIf(i -> !Bank.contains(i) && !Inventory.contains(i));

                        for (String i : ItemUtilities.sellables) {
                            if (Bank.contains(i)) {
                                if (Bank.withdrawAll(i))
                                    Sleep.sleepUntil(() -> Inventory.contains(i), Utilities.getRandomSleepTime());
                            }
                        }
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        BankUtilities.openBank();
                    }
                }
            }
        } else {
            Utilities.walkToArea(geArea);
        }

        if (ItemUtilities.buyables.isEmpty()) {
            canBuyItems = false;
            checkedBank = false;
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return !ItemUtilities.buyables.isEmpty();
    }

    @Override
    public int priority() {
        return 4;
    }
}

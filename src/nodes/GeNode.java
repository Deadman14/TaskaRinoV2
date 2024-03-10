package nodes;

import models.GeItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.InventoryMonitor;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeNode extends TaskNode {
    private Area geArea = new Area(3151, 3501, 3177, 3478);
    private boolean checkedBank = false;
    private boolean hasAllSellables = false;

    @Override
    public int execute() {
        Logger.log("- Grand Exchange -");

        int totalCoins = Bank.count("Coins");
        if (!checkedBank) {
            if (Bank.isOpen()) {
                Logger.log("GE FULLY OPEN: " + Utilities.isGeFullyOpen());
                if (Utilities.isGeFullyOpen()) {
                    ItemUtilities.sellables = new ArrayList<>(Bank.all(i -> i != null && ItemUtilities.allSellables.contains(i.getName())).stream().map(Item::getName).toList());
                } else {
                    ItemUtilities.sellables = new ArrayList<>(Bank.all(i -> i != null && ItemUtilities.phaseOneSellables.contains(i.getName())).stream().map(Item::getName).toList());
                }

                int totalBuyPrice = ItemUtilities.buyables.stream().mapToInt(i -> i.getPrice() * i.getAmount()).sum();

                Logger.log("Coins: " + totalCoins);
                Logger.log("Buy: " + totalBuyPrice * 2.5);
                if(totalCoins > totalBuyPrice * 2.5) {
                    checkedBank = true;
                } else {
                    for (String i : ItemUtilities.sellables) {
                        Logger.log("add sell");
                        totalCoins += (LivePrices.getLow(i) * Bank.count(i)) + 1;
                    }

                    if (!Utilities.isP2P && Bank.count("Coins") < 50000 && totalCoins >= 50000) {
                        checkedBank = true;
                    } else if (totalCoins < totalBuyPrice * 2.5) {
                        TaskUtilities.currentTask = "";
                        ItemUtilities.buyables = new ArrayList<>();
                        checkedBank = false;
                    } else
                        checkedBank = true;
                }
            }   else {
                BankUtilities.openBank();
            }

            return Utilities.getRandomExecuteTime();
        }

        if (geArea.contains(Players.getLocal()) && checkedBank) {
            if (ItemUtilities.sellables.isEmpty()) {
                hasAllSellables = false;

                if (Inventory.getEmptySlots() >= ItemUtilities.buyables.size()) {
                    if (GrandExchange.isOpen()) {
                        List<GeItem> items = ItemUtilities.buyables.stream()
                                .filter(i -> (!Inventory.contains(i.getName()) || Inventory.count(i.getName()) < i.getAmount()) && !GrandExchange.contains(i.getName()))
                                .toList();
                        if (GrandExchange.getOpenSlots() > 0 && !items.isEmpty()) {
                            GeItem item = items.get(0);
                            if (totalCoins > item.getPrice() * item.getAmount()) {
                            if (GrandExchange.buyItem(item.getName(), item.getAmount(), item.getPrice()))
                                Sleep.sleepUntil(() -> GrandExchange.contains(item.getName()), Utilities.getRandomSleepTime());
                            } else {
                                TaskUtilities.currentTask = "";
                                ItemUtilities.buyables = new ArrayList<>();
                                checkedBank = false;
                            }
                        } else {
                            Sleep.sleepUntil(GrandExchange::isReadyToCollect, Utilities.getRandomSleepTime());
                            if (GrandExchange.isReadyToCollect()) {
                                if (GrandExchange.collect())
                                    Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                            } else {
                                if (!ItemUtilities.buyables.isEmpty()) {
                                    GeItem cancelItem = ItemUtilities.buyables.stream().filter(i -> GrandExchange.contains(i.getName())).toList().get(0);
                                    int slot = GrandExchange.getItem(cancelItem.getName()).getSlot();
                                    if (GrandExchange.cancelOffer(slot)) {
                                        Sleep.sleepUntil(GrandExchange::isReadyToCollect, Utilities.getRandomSleepTime());
                                        if (GrandExchange.collect()) {
                                            Sleep.sleepUntil(() -> Inventory.contains(cancelItem.getName()) || !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                                            cancelItem.setAmount(cancelItem.getAmount() - Inventory.count(cancelItem.getName()));
                                            cancelItem.setPrice((int) (cancelItem.getPrice() * 1.10) + 1);
                                        }
                                    }
                                } else {
                                    if (GrandExchange.cancelAll()) {
                                        Sleep.sleepUntil(GrandExchange::isReadyToCollect, Utilities.getRandomSleepTime());
                                        if (GrandExchange.collect())
                                            Sleep.sleepUntil(() -> GrandExchange.getOpenSlots() > 0, Utilities.getRandomSleepTime());
                                    }
                                }
                            }
                        }

                        ItemUtilities.buyables.removeIf(i -> Inventory.contains(i.getName()) && Inventory.count(i.getName()) >= i.getAmount());
                        if (ItemUtilities.buyables.isEmpty())
                            checkedBank = false;
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        if (GrandExchange.open())
                            Sleep.sleepUntil(GrandExchange::isOpen, Utilities.getRandomSleepTime());
                    }
                } else {
                    if (Bank.isOpen()) {
                        if (!Inventory.isEmpty()) {
                            if (Bank.depositAllItems())
                                Sleep.sleepUntil(Inventory::isEmpty, Utilities.getRandomSleepTime());
                        }

                        ItemUtilities.buyables.removeIf(i -> Bank.contains(i.getName()) && Bank.count(i.getName()) >= i.getAmount());
                    } else {
                        BankUtilities.openBank();
                    }
                }
            } else {
                if (hasAllSellables) {
                    if (GrandExchange.isOpen()) {
                        Logger.log("SIZE: " + ItemUtilities.sellables.size());
                        String item = ItemUtilities.sellables.get(0);

                        if (GrandExchange.getOpenSlots() > 0 && Inventory.contains(item)) {
                            if (GrandExchange.sellItem(item, Inventory.count(item), (int)(LivePrices.getLow(item) * 0.9) + 1))
                                Sleep.sleepUntil(() -> GrandExchange.contains(item) || Widgets.getWidget(289).isVisible(), Utilities.getRandomSleepTime());
                        } else {
                            Sleep.sleepUntil(GrandExchange::isReadyToCollect, Utilities.getRandomSleepTime());
                            if (GrandExchange.isReadyToCollect()) {
                                if (GrandExchange.collect())
                                    Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                            } else if (GrandExchange.contains(item)) {
                                GrandExchangeItem geItem = GrandExchange.getItem(item);
                                if (geItem != null) {
                                    if (GrandExchange.cancelOffer(geItem.getSlot()))
                                        Sleep.sleepUntil(GrandExchange::isReadyToCollect, Utilities.getRandomSleepTime());
                                    if (GrandExchange.isReadyToCollect()) {
                                        if (GrandExchange.collect())
                                            Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                                    }
                                }
                            } else if (GrandExchange.getOpenSlots() < 1) {
                                if (GrandExchange.cancelAll()) {
                                    Sleep.sleepUntil(GrandExchange::isReadyToCollect, Utilities.getRandomSleepTime());
                                    if (GrandExchange.isReadyToCollect()) {
                                        if (GrandExchange.collect())
                                            Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), Utilities.getRandomSleepTime());
                                    }
                                }
                            }
                        }

                        ItemUtilities.sellables.removeIf(i -> !Inventory.contains(i));
                    } else if (Walking.shouldWalk(Utilities.getShouldWalkDistance())) {
                        if (GrandExchange.open())
                            Sleep.sleepUntil(GrandExchange::isOpen, Utilities.getRandomSleepTime());
                    }
                } else {
                    if (Bank.isOpen()) {
                        BankUtilities.setBankMode(BankMode.NOTE);

                        if (Inventory.contains(i -> !ItemUtilities.sellables.contains(i.getName()))) {
                            Logger.log("GE Selling Deposit Items");
                            if (Bank.depositAllExcept(i -> ItemUtilities.sellables.contains(i.getName())))
                                Sleep.sleepUntil(() -> Inventory.isEmpty() || Inventory.onlyContains(i -> ItemUtilities.sellables.contains(i)), Utilities.getRandomSleepTime());
                        }

                        ItemUtilities.sellables.removeIf(i -> !Bank.contains(i) && !Inventory.contains(i));

                        Item dbItem = Bank.get(i -> ItemUtilities.sellables.contains(i.getName()));
                        if (dbItem != null) {
                            String item = dbItem.getName();
                            if (Bank.withdrawAll(item))
                                Sleep.sleepUntil(() -> !Bank.contains(item), Utilities.getRandomSleepTime());
                        } else
                            hasAllSellables = true;
                    } else {
                        BankUtilities.openBank();
                    }
                }
            }
        } else {
            Utilities.walkToArea(geArea);
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
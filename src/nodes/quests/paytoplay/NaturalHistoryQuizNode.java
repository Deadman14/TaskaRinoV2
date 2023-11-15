package nodes.quests.paytoplay;

import models.NaturalQuizDisplay;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import utils.ItemUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.util.*;
import java.util.stream.Collectors;

public class NaturalHistoryQuizNode extends TaskNode {
    private final Area quizArea = new Area(1724, 4990, 1792, 4932);
    private final Area orlandoArea = new Area(1754, 4966, 1763, 4954);
    private final Area lizardArea =  new Area(1738, 4986, 1745, 4975);
    private final Area battleTortoiseArea = new Area(1748, 4986, 1755, 4975);
    private final Area dragonArea = new Area(1763, 4986, 1770, 4975);
    private final Area wyvernArea = new Area(1773, 4986, 1779, 4975);
    private final Area snailArea = new Area(1772, 4970, 1777, 4960);
    private final Area snakeArea = new Area(1779, 4970, 1784, 4960);
    private final Area seaSlugArea = new Area(1785, 4949, 1779, 4960);
    private final Area monkeyArea = new Area(1777, 4949, 1772, 4959);
    private final Area kalphiteQueenArea = new Area(1770, 4943, 1758, 4935);
    private final Area terrorBirdArea = new Area(1747, 4943, 1758, 4934);
    private final Area penguinArea = new Area(1745, 4949, 1740, 4960);
    private final Area moleArea = new Area(1738, 4949, 1733, 4960);
    private final Area camelArea = new Area(1733, 4971, 1738, 4960);
    private final Area leechArea = new Area(1740, 4971, 1746, 4960);

    private List<NaturalQuizDisplay> quizSteps = new ArrayList<>(Arrays.asList(
            new NaturalQuizDisplay("Lizard", lizardArea, new ArrayList<>(Arrays.asList("Sunlight.",
                    "The Slayer Masters.", "Three.", "Squamata.", "It becomes sleepy.", "Hair."))),
            new NaturalQuizDisplay("Battle Tortoise", battleTortoiseArea, new ArrayList<>(Arrays.asList("Mibbiwocket.",
                "Vegetables.", "Admiral Bake.", "Hard shell.", "Twenty years.", "Gnomes."))),
            new NaturalQuizDisplay("Dragon", dragonArea, new ArrayList<>(Arrays.asList("Runite.",
                "Anti-dragon-breath shield.", "Unknown.", "Elemental.", "Old battle sites.", "Twelve."))),
            new NaturalQuizDisplay("Wyvern", wyvernArea, new ArrayList<>(Arrays.asList("Climate change.",
                "Two.", "Asgarnia.", "Reptiles.", "Dragons.", "Below room temperature."))),
            new NaturalQuizDisplay("Snail", snailArea, new ArrayList<>(Arrays.asList("It is resistant to acid.",
                "Spitting acid.", "Fireproof oil.", "Acid-spitting snail.", "Contracting and stretching.", "An operculum."))),
            new NaturalQuizDisplay("Snake", snakeArea, new ArrayList<>(Arrays.asList("Stomach acid.",
                "Tongue.", "Seeing how you smell.", "Constriction.", "Squamata.", "Anywhere."))),
            new NaturalQuizDisplay("Sea slug", seaSlugArea, new ArrayList<>(Arrays.asList("Nematocysts.",
                "The researchers keep vanishing.", "Seaweed.", "Defense or display.", "Ardougne.", "They have a hard shell."))),
            new NaturalQuizDisplay("Monkey", monkeyArea, new ArrayList<>(Arrays.asList("Simian.",
                "Harmless.", "Bitternuts.", "Harmless.", "Red.", "Seaweed."))),
            new NaturalQuizDisplay("Kalphite Queen", kalphiteQueenArea, new ArrayList<>(Arrays.asList("Pasha.",
                "Worker.", "Lamellae.", "Carnivores.", "Scarab beetles.", "Scabaras."))),
            new NaturalQuizDisplay("Terrorbird", terrorBirdArea, new ArrayList<>(Arrays.asList("Anything.",
                "Gnomes.", "Eating plants.", "Four.", "Stones.", "0."))),
            new NaturalQuizDisplay("Penguin", penguinArea, new ArrayList<>(Arrays.asList("Sight.",
                "Planning.", "A layer of fat.", "Cold.", "Social.", "During breeding."))),
            new NaturalQuizDisplay("Mole", moleArea, new ArrayList<>(Arrays.asList("Subterranean.",
                "They dig holes.", "Wyson the Gardener.", "A labour.", "Insects and other invertebrates.", "The Talpidae family."))),
            new NaturalQuizDisplay("Camel", camelArea, new ArrayList<>(Arrays.asList("Toxic dung.",
                "Two.", "Omnivore.", "Annoyed.", "Al Kharid.", "Milk."))),
            new NaturalQuizDisplay("Leech", leechArea, new ArrayList<>(Arrays.asList("Water.",
                "'Y'-shaped.", "Apples.", "Environment.", "They attack by jumping.", "It doubles in size.")))
    ));
    private boolean isStarted = false;
    private boolean isFinished = false;
    private boolean handIn = false;

    @Override
    public int execute() {
        Utilities.currentNode = "NaturalHistoryQuizNode";
        Logger.log("Natural History Quiz");

        if (isFinished) {
            TaskUtilities.currentTask = "";
            ItemUtilities.buyables = new ArrayList<>();
            return Utilities.getRandomExecuteTime();
        }

        if (quizArea.contains(Players.getLocal())) {
            if (isStarted && !handIn) {
                if (quizSteps.stream().anyMatch(i -> !i.isDone())) {
                    NaturalQuizDisplay step = quizSteps.stream().filter(i -> !i.isDone()).findFirst().get();
                    if (Widgets.isVisible(533) || Dialogues.inDialogue()) {
                        if (Dialogues.inDialogue()) {
                            //use game message in Start class to check this instead
                            if (Dialogues.getNPCDialogue().equals("Bonza, mate! I think that's all of them."))
                                step.setDone(true);

                            Dialogues.continueDialogue();
                        }

                        if (Widgets.isVisible(533)) {
                            Logger.log("Widget");
                            for (WidgetChild child : Widgets.getWidget(533).getChildren()) {
                                if (!child.getText().isEmpty() && step.getOptions().contains(child.getText())) {
                                    if (child.interact())
                                        Sleep.sleepUntil(() -> !Widgets.isVisible(533), Utilities.getRandomSleepTime());
                                }
                            }
                        }
                    } else {
                        Logger.log("Handle Plaque for: " + step.getName());
                        GameObject plaque = GameObjects.closest(i -> i != null && i.getName().equals("Plaque")
                                && step.getArea().contains(i));
                        if (plaque.interact())
                            Sleep.sleepUntil(() -> Widgets.isVisible(533), Utilities.getRandomSleepTime());

                        if (!Widgets.isVisible(533) && plaque.distance(Players.getLocal()) <= 1) step.setDone(true);
                    }
                } else if (quizSteps.stream().filter(i -> i.isDone()).toList().size() == 14) {
                    handIn = true;
                }
            } else if (!isStarted && !handIn){
                if (Dialogues.inDialogue()) {
                    if (Dialogues.areOptionsAvailable()) {
                        Dialogues.chooseOption(1);
                        isStarted = true;
                    } else {
                        if (Dialogues.getNPCDialogue().equals("G'day there, mate. How're the display cases coming<br>along?"));
                        isStarted = true;
                        Dialogues.continueDialogue();
                    }
                } else {
                    if (orlandoArea.contains(Players.getLocal())) {
                        NPC orlando = NPCs.closest(i -> i != null && i.getName().equals("Orlando Smith"));
                        if (orlando.interact())
                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                    } else {
                        Utilities.walkToArea(orlandoArea);
                    }
                }
            } else if (handIn) {
                if (Dialogues.inDialogue()) {
                    Dialogues.continueDialogue();
                } else {
                    if (orlandoArea.contains(Players.getLocal())) {
                        NPC orlando = NPCs.closest(i -> i != null && i.getName().equals("Orlando Smith"));
                        if (orlando.interact())
                            Sleep.sleepUntil(Dialogues::inDialogue, Utilities.getRandomSleepTime());
                    } else {
                        Utilities.walkToArea(orlandoArea);
                    }
                }
            }
        } else {
            Utilities.walkToArea(quizArea);
        }

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return TaskUtilities.currentTask.equals("Natural History Quiz");
    }
}

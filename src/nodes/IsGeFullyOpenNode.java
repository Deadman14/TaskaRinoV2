package nodes;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import utils.Utilities;

public class IsGeFullyOpenNode extends TaskNode {

    @Override
    public int execute() {
        Logger.log("- Is Ge Fully Open Node -");

        Utilities.closeGeAndBank();

        if (Tabs.isOpen(Tab.QUEST)) {
            if (Dialogues.inDialogue())
                Dialogues.chooseOption(2);

            if (Widgets.getWidget(712).getChild(2).getChild(100).isVisible()) {
                if (!Widgets.getWidget(712).getChild(2).getChild(100).getText().contains("Click to reveal")) {
                    String timePlayedText = Widgets.getWidget(712).getChild(2).getChild(100).getText();
                    if (timePlayedText.contains("day"))
                        Utilities.timePlayed = 20;
                    else if (timePlayedText.contains("hour"))
                        Utilities.timePlayed = Integer.valueOf(timePlayedText.substring(timePlayedText.indexOf(">") + 1, timePlayedText.indexOf("h") - 1));
                    else
                        Utilities.timePlayed = 0;
                } else if (Widgets.getWidget(712).getChild(2).getChild(100).interact()) {
                    Sleep.sleepUntil(() -> !Widgets.getWidget(712).getChild(2).getChild(100).getText().contains("Click to reveal") || Dialogues.inDialogue(), Utilities.getRandomSleepTime());
                }
            } else if (Widgets.getWidget(629).getChild(3).interact()) {
                Sleep.sleep(Calculations.random(500, 2000));
            }
        } else if (Tabs.open(Tab.QUEST))
            Sleep.sleepUntil(() -> Tabs.isOpen(Tab.QUEST), Utilities.getRandomSleepTime());

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return Utilities.timePlayed == null;
    }

    @Override
    public int priority() {
        return 6;
    }
}

import nodes.*;
import nodes.combat.EatNode;
import nodes.combat.TrainMagic;
import nodes.combat.TrainMeleeAndRangeNode;
import nodes.combat.slayertasks.*;
import nodes.moneymaking.MakeSoftclayNode;
import nodes.moneymaking.ShearSheepNode;
import nodes.quests.freetoplay.*;
import nodes.quests.paytoplay.NaturalHistoryQuizNode;
import nodes.skilling.*;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;
import utils.SlayerUtilities;
import utils.TaskUtilities;
import utils.Utilities;

import java.awt.*;

@ScriptManifest(name = "Task-A-Rino-V2", description = "Runs multiple scripts inside one script as tasks.",
        author = "Deadman", version = 1.0, category = Category.MISC)
public class Start extends TaskScript implements ChatListener {
    private Timer timer = new Timer();

    @Override
    public void onStart() {

        timer.start();

        //TaskUtilities.currentTask = "Doric's Quest";
        //TaskUtilities.taskTimer = new Timer(2600654);
        //TaskUtilities.taskTimer.start();

        addNodes(new BankNode(), new ChopNode(), new MineNode(), new EquipmentNode(), new NewTaskNode(),
                new CooksAssistantNode(), new LootNode(), new ShearSheepNode(), new EatNode(), new FishNode(), new GeNode(),
                new MakeSoftclayNode(), new RestlessGhostNode(),  new DoricsNode(), new ImpCatcherNode(), new RuneMysteriesNode(),
                new ErnestTheChickenNode(), new XMarksTheSpotNode(), new KnightsSwordNode(), new NaturalHistoryQuizNode(),
                new GetSlayerTaskNode(), new SlayKalphitesNode(), new SlayOgresNode(), new SlayIceWarriorsNode(),
                new SlayMossGiantsNode(), new SlayIceGiantsNode(), new SlayCrocodilesNode(), new SlayHobgoblinsNode(),
                new SlayCockatriceNode(), new SlayWallBeastsNode(), new SlayCaveBugsNode(), new SlayBasilisksNode(),
                new SlayKillerwattsNode(), new SlayPyrefiendsNode(), new SlayRockslugsNode(), new SlayCaveSlimesNode(),
                new SlayAnkousNode(), new SlayCaveCrawlersNode(), new SlayHillGiantsNode(), new SlayFireGiantsNode(),
                new SlayLesserDemonsNode(), new SlayLizardsNode(), new SlayJelliesNode(), new IsGeFullyOpenNode(), new SmithingNode(),
                new DeathNode(), new SheepShearerNode(), new TrainMeleeAndRangeNode(), new TrainMagic());
    }

    @Override
    public void onPaint(Graphics2D graphics) {
        graphics.drawString("Current Task: " + TaskUtilities.currentTask, 10, 30);

        graphics.drawString("Time Left on Task: " + TaskUtilities.taskTimer.remaining() / 60000, 10, 45);

        graphics.drawString("Time running: " + timer.formatTime(), 10, 60);

        if (!SlayerUtilities.currentSlayerTask.isEmpty())
            graphics.drawString("Current Slayer Task: " + SlayerUtilities.currentSlayerTask, 10, 75);

        super.onPaint(graphics);
    }

    @Override
    public void onGameMessage(Message message) {
        if (message != null) {
            String m = message.getMessage();

            if (m.contains("You're assigned to kill")) {
                String henry = m.split("</col>")[1];
                SlayerUtilities.currentSlayerTask = henry.split("<col=")[0];
            }

            if (m.contains("return to a Slayer master")) {
                SlayerUtilities.currentSlayerTask = "";
                TaskUtilities.currentTask = "Slayer";
                SlayerUtilities.getNewSlayerTaskAfterTask = true;
            }

            if (m.contains("you are dead")) {
                Utilities.hasDied = true;
                Utilities.playerDeathTile = Players.getLocal().getTile();
            }
        }
    }
}

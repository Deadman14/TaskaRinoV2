package nodes;

import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import utils.Utilities;

public class SetSettingsNode extends TaskNode {

    @Override
    public int execute() {
        Logger.log("- Set Settings -");

        return Utilities.getRandomExecuteTime();
    }

    @Override
    public boolean accept() {
        return PlayerSettings.getBitValue(14700) != 1 || PlayerSettings.getBitValue(14701) != 1;
    }
}

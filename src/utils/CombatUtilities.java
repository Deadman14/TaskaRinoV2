package utils;

import constants.ItemNameConstants;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombatUtilities {
    public static boolean needRunes = false;
    private static final Area goblinArea = new Area(3240, 3250, 3264, 3225);
    private static final Area cowArea = new Area(3242, 3296, 3264, 3256);
    private static final Area hillGiantArea = new Area(3096, 9850, 3126, 9823);
    private static final Area impArea = new Area(2953, 3330, 3059, 3293);

    public static Area getCurrentCombatArea() {
        if (TaskUtilities.currentTask.contains("Melee")) {
            int att = Skills.getRealLevel(Skill.ATTACK);
            int str = Skills.getRealLevel(Skill.STRENGTH);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (att > 39 && str > 39 && def > 39)
                return hillGiantArea;
            if (att > 19 && str > 19 && def > 19)
                return cowArea;
            else
                return goblinArea;
        } else if (TaskUtilities.currentTask.contains("Range")) {
            int rang = Skills.getRealLevel(Skill.RANGED);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (rang > 39 && def > 39)
                return hillGiantArea;
            if (rang > 19 && def > 19)
                return cowArea;
            else
                return goblinArea;
        } else if (TaskUtilities.currentTask.contains("Magic")) {
            int mage = Skills.getRealLevel(Skill.MAGIC);
            int def = Skills.getRealLevel(Skill.DEFENCE);

            if (mage > 39 && def > 39)
                return hillGiantArea;
            if (mage > 19 && def > 19)
                return cowArea;
            else
                return goblinArea;
        } else if (TaskUtilities.currentTask.equals("Kill Imps")) {
            return impArea;
        }

        return goblinArea;
    }

    public static Normal getCurrentSpell() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        if (level > 58)
            return Normal.FIRE_BLAST;
        if (level > 34)
            return Normal.FIRE_BOLT;
        if (level > 12)
            return Normal.FIRE_STRIKE;

        return Normal.WIND_STRIKE;
    }

    public static List<String> getCurrentRunes() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        if (level > 58)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.DEATH_RUNE, ItemNameConstants.AIR_RUNE));
        if (level > 34)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.CHAOS_RUNE, ItemNameConstants.AIR_RUNE));
        if (level > 12)
            return new ArrayList<>(Arrays.asList(ItemNameConstants.MIND_RUNE, ItemNameConstants.AIR_RUNE));

        return new ArrayList<>(List.of(ItemNameConstants.MIND_RUNE));
    }
}
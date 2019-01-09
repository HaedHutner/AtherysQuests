package com.atherys.quests.script.lib.quest;

import com.atherys.quests.api.objective.Objective;
import com.atherys.quests.model.quest.SimpleQuest;

import java.util.function.BiFunction;

/**
 * @jsfunc
 */ 
public class AddQuestObjectives implements BiFunction<SimpleQuest, Objective[], Boolean> {
    @Override
    public Boolean apply(SimpleQuest simpleQuest, Objective[] objectives) {
        for (Objective objective : objectives) {
            simpleQuest.getObjectives().add(objective);
        }

        return true;
    }
}

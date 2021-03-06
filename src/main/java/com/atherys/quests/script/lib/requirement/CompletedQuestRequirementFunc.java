package com.atherys.quests.script.lib.requirement;

import com.atherys.quests.api.requirement.Requirement;
import com.atherys.quests.quest.requirement.Requirements;

import java.util.function.Function;

/**
 * @jsfunc
 */
public class CompletedQuestRequirementFunc implements Function<String, Requirement> {
    /**
     * A requirement for completing a quest.
     *
     * @param questId The quest's ID.
     * @jsname completedQuestRequirement
     */
    @Override
    public Requirement apply(String questId) {
        return Requirements.completedQuest(questId);
    }
}

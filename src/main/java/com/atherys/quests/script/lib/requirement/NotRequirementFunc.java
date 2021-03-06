package com.atherys.quests.script.lib.requirement;

import com.atherys.quests.api.requirement.Requirement;
import com.atherys.quests.quest.requirement.Requirements;

import java.util.function.Function;

/**
 * @jsfunc
 */
public class NotRequirementFunc implements Function<Requirement, Requirement> {
    /**
     * A requirement for the player _not_ to have a requirement.
     *
     * @param requirement The requirement for them not to have.
     * @jsname notRequirement
     */
    @Override
    public Requirement apply(Requirement requirement) {
        return Requirements.not(requirement);
    }
}

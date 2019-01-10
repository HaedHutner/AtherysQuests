package com.atherys.quests.model.quest.requirement;

import com.atherys.quests.api.quester.Quester;
import com.atherys.quests.api.requirement.Requirement;
import com.atherys.quests.model.SimpleQuester;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.text.Text;

/**
 * A requirement which evaluates 2 other requirements and && their values together.
 */
public class AndRequirement implements Requirement {

    @Expose
    private Requirement requirement1;
    @Expose
    private Requirement requirement2;

    AndRequirement(Requirement requirement1, Requirement requirement2) {
        this.requirement1 = requirement1;
        this.requirement2 = requirement2;
    }

    @Override
    public boolean check(Quester quester) {
        return requirement1.check(quester) && requirement2.check(quester);
    }

    @Override
    public Requirement copy() {
        return new OrRequirement(requirement1.copy(), requirement2.copy());
    }

    @Override
    public Text toText() {
        return Text.of(requirement1, " AND ", requirement2);
    }
}
package com.atherys.quests.quester;

import com.atherys.core.database.api.DBObject;
import com.atherys.core.utils.UserUtils;
import com.atherys.core.views.Viewable;
import com.atherys.quests.api.quest.Quest;
import com.atherys.quests.events.QuestCompletedEvent;
import com.atherys.quests.events.QuestStartedEvent;
import com.atherys.quests.events.QuestTurnedInEvent;
import com.atherys.quests.util.QuestMsg;
import com.atherys.quests.views.QuestLog;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Quester implements DBObject, Viewable<QuestLog> {

    private UUID player; // Retrieve player from this. 100% Reliable.

    private Player cachedPlayer; // Used for performance optimizations. When quick access to the player object is crucial.

    private Map<String, Quest> quests = new HashMap<>();
    private Map<String, Long> completedQuests = new HashMap<>();

    public Quester(UUID uuid) {
        this.player = uuid;
    }

    public Quester(Player player) {
        this.player = player.getUniqueId();
        this.cachedPlayer = player;
    }

    @Override
    public UUID getUUID() {
        return player;
    }

    public void notify(Event event, Player player) {
        if (!this.player.equals(player.getUniqueId())) return;

        this.cachedPlayer = player;

        for (Quest quest : quests.values()) {
            if (!quest.isComplete()) {
                quest.notify(event, this);
                if (quest.isComplete()) {
                    QuestMsg.info(this, "You have completed the quest \"", quest.getName(), "\". You may now turn it in.");

                    QuestCompletedEvent qsEvent = new QuestCompletedEvent(quest, this);
                    Sponge.getEventManager().post(qsEvent);
                }
            }
        }
    }

    public void pickupQuest(Quest quest) {
        if (!quest.meetsRequirements(this)) {
            Text.Builder reqText = Text.builder();
            reqText.append(Text.of(QuestMsg.MSG_PREFIX, " You do not meet the requirements for this quest."));
            reqText.append(quest.createView().getFormattedRequirements());
            QuestMsg.noformat(this, reqText.build());

            return;
        }

        if (!completedQuests.containsKey(quest.getId()) && !quests.containsKey(quest.getId())) {
            quest.pickUp(this);
            quests.put(quest.getId(), (Quest) quest.copy());
            QuestMsg.info(this, "You have started the quest \"", quest.getName(), "\"");

            QuestStartedEvent qsEvent = new QuestStartedEvent(quest, this);
            Sponge.getEventManager().post(qsEvent);
        } else {
            QuestMsg.error(this, "You are either already doing this quest, or have done it before in the past.");
        }
    }

    public void removeQuest(Quest quest) {
        quests.remove(quest.getId());
    }

    public void turnInQuest(Quest quest) {
        removeQuest(quest);
        completedQuests.put(quest.getId(), System.currentTimeMillis());

        quest.award(this);

        QuestMsg.info(this, "You have turned in the quest \"", quest.getName(), "\"");

        quest.turnIn(this);
        QuestTurnedInEvent qsEvent = new QuestTurnedInEvent(quest, this);
        Sponge.getEventManager().post(qsEvent);
    }

    public Optional<? extends User> getUser() {
        return UserUtils.getUser(this.player);
    }

    @Nullable
    public Player getCachedPlayer() {
        return cachedPlayer;
    }

    public boolean hasQuestWithId(String id) {
        return quests.containsKey(id);
    }

    public boolean hasQuest(Quest quest) {
        return quests.containsKey(quest.getId());
    }

    public Map<String, Long> getCompletedQuests() {
        return completedQuests;
    }

    public Map<String, Quest> getQuests() {
        return quests;
    }

    public boolean hasCompleted(String questId) {
        return completedQuests.containsKey(questId);
    }

    @Override
    public QuestLog createView() {
        return new QuestLog(this);
    }

    public QuestLog getLog() {
        return new QuestLog(this);
    }
}

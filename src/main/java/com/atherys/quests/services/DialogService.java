package com.atherys.quests.services;

import com.atherys.quests.AtherysQuests;
import com.atherys.quests.data.DialogData;
import com.atherys.quests.dialog.Dialog;
import com.atherys.quests.dialog.tree.DialogTree;
import com.atherys.quests.events.DialogRegistrationEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A class responsible for managing all dialogs
 */
public final class DialogService {

    private static DialogService instance = new DialogService();

    private Map<UUID, Dialog> ongoingDialogs = new HashMap<>();
    private Map<String, DialogTree> trees = new HashMap<>();

    private DialogService() {
        DialogRegistrationEvent dialogRegistrationEvent = new DialogRegistrationEvent(this);
        Sponge.getEventManager().post(dialogRegistrationEvent);
    }

    public static DialogService getInstance() {
        return instance;
    }

    public void registerDialog(DialogTree tree) {
        this.trees.put(tree.getId(), tree);
    }

    public Optional<DialogTree> getDialogFromId(String id) {
        return Optional.ofNullable(trees.get(id));
    }

    /**
     * Checks to see if an {@link Entity} contains a {@link DialogTree}.
     *
     * @param entity The entity to be checked
     * @return Whether or not this entity contains a DialogTree
     */
    public boolean hasDialog(Entity entity) {
        return entity.get(DialogData.class).isPresent();
    }

    /**
     * Used to get the {@link DialogTree} an entity is associated with.
     *
     * @param entity The entity whose dialog is to be retrieved
     * @return An optional containing the dialog this entity is associated with. The optional is empty if the entity does not contain a dialog.
     */
    public Optional<DialogTree> getDialog(Entity entity) {
        Optional<DialogData> dialogData = entity.get(DialogData.class);
        if (dialogData.isPresent()) {
            AtherysQuests.getInstance().getLogger().error("Dialog Data Detected: " + dialogData.get().getDialogId());
            return Optional.ofNullable(trees.get(dialogData.get().getDialogId()));
        } else return Optional.empty();
    }

    /**
     * Associate an entity with a {@link DialogTree}. When a player attempts to interact with this entity, they will begin the dialog.
     *
     * @param entity The entity to be assigned a dialog
     * @param tree   The dialog tree itself
     * @return Whether or not setting the dialog was successful.
     */
    public boolean setDialog(Entity entity, DialogTree tree) {
        return entity.offer(new DialogData(tree.getId())).isSuccessful();
    }

    /**
     * Used to check if a player is currently in a dialog with an NPC or not.
     *
     * @param player The player to check
     * @return Whether or not the player is participating in a dialog
     */
    public boolean hasPlayerDialog(Player player) {
        return ongoingDialogs.containsKey(player.getUniqueId());
    }

    /**
     * Used to get the dialog the player is currently participating in.
     *
     * @param player The player whose dialog is to be gotten
     * @return An optional containing the dialog. Is empty if no dialog is found.
     */
    public Optional<Dialog> getPlayerDialog(Player player) {
        return Optional.ofNullable(ongoingDialogs.get(player.getUniqueId()));
    }

    public void removePlayerDialog(Player player) {
        ongoingDialogs.remove(player.getUniqueId());
    }

    /**
     * Begin a dialog between a player and an entity.
     * This will check if the player is currently in another dialog. If so, the dialog will not be started.
     * This will also check if the entity has a dialog. If not, a dialog will not be started.
     *
     * @param player The player participant
     * @param entity The entity participant
     * @return An optional containing the dialog. Empty if failure.
     */
    public Optional<Dialog> startDialog(Player player, Entity entity) {
        Optional<DialogTree> tree = getDialog(entity);

        if (!tree.isPresent() || hasPlayerDialog(player)) return Optional.empty();

        Optional<Dialog> dialog = Dialog.between(player, entity, tree.get());

        if (!dialog.isPresent()) return Optional.empty();

        this.ongoingDialogs.put(player.getUniqueId(), dialog.get());
        return dialog;
    }

}
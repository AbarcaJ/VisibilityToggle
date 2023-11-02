package dev.cleusgamer201.visibilitytoggle.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerVisibilityUpdatedEvent extends Event {

    private static final HandlerList handler = new HandlerList();

    private final Player player;
    private final Visibility visibility;

    public PlayerVisibilityUpdatedEvent(Player player, Visibility visibility) {
        this.player = player;
        this.visibility = visibility;
    }

    public Player getPlayer() {
        return player;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }
}

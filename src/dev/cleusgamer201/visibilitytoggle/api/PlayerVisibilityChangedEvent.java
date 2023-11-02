package dev.cleusgamer201.visibilitytoggle.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerVisibilityChangedEvent extends Event {

    private static final HandlerList handler = new HandlerList();

    private final Player player;
    private final Visibility oldVisibility, newVisibility;

    public PlayerVisibilityChangedEvent(Player player, Visibility oldVisibility, Visibility newVisibility) {
        this.player = player;
        this.oldVisibility = oldVisibility;
        this.newVisibility = newVisibility;
    }

    public Player getPlayer() {
        return player;
    }

    public Visibility getOldVisibility() {
        return oldVisibility;
    }

    public Visibility getNewVisibility() {
        return newVisibility;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }
}

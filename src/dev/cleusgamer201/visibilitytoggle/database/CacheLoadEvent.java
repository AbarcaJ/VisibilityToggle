package dev.cleusgamer201.visibilitytoggle.database;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CacheLoadEvent extends Event {

	private final Cache cache;
	private final Player player;
	
	public CacheLoadEvent(final Cache cache, final Player player) {
		this.cache = cache;
		this.player = player;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Cache getCache() {
		return this.cache;
	}
	
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}

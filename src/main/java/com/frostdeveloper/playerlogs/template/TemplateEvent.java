package com.frostdeveloper.playerlogs.template;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class TemplateEvent extends PlayerEvent
{
	private HandlerList nullHandler;
	
	public TemplateEvent(@NotNull Player who) { super(who); }
	
	@Override
	public @NotNull HandlerList getHandlers() { return nullHandler; }
}

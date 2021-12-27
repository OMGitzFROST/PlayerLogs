package com.frostdeveloper.playerlogs.definition;

public enum Permission
{
	ALL("playerlog.*"),
	UPDATE_NOTIFY("playerlog.update.notify"),
	CMD_UPDATE("playerlog.command.update"),
	CMD_RELOAD("playerlog.command.reload");
	
	private final String perm;
	
	Permission(String perm) { this.perm = perm; }
	
	public String getPerm() { return perm; }
}

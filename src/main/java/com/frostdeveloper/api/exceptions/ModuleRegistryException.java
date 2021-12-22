package com.frostdeveloper.api.exceptions;

import java.text.MessageFormat;

public class ModuleRegistryException extends RuntimeException
{
	public ModuleRegistryException(Throwable thrown) { super(thrown); }
	
	public ModuleRegistryException(String message, Object... param) { super(MessageFormat.format(message, param)); }
	
	public ModuleRegistryException(String message, Throwable thrown, Object... param) {
		super(MessageFormat.format(message, param), thrown);
	}
}

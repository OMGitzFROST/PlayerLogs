package com.frostdeveloper.api.exceptions;

import java.text.MessageFormat;

public class ResourceNotFoundException extends RuntimeException
{
	public ResourceNotFoundException(Throwable thrown) { super(thrown); }
	
	public ResourceNotFoundException(String message, Object... param) { super(MessageFormat.format(message, param)); }
	
	public ResourceNotFoundException(String message, Throwable thrown, Object... param) {
		super(MessageFormat.format(message, param), thrown);
	}
}

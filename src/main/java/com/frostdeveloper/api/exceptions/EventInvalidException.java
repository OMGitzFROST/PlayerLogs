package com.frostdeveloper.api.exceptions;

import java.text.MessageFormat;

public class EventInvalidException extends RuntimeException
{
	public EventInvalidException(Throwable thrown) { super(thrown); }
	
	public EventInvalidException(String message, Object... param) { super(MessageFormat.format(message, param)); }
	
	public EventInvalidException(String message, Throwable thrown, Object... param) {
		super(MessageFormat.format(message, param), thrown);
	}
}

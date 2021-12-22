package com.frostdeveloper.api.exceptions;

import java.text.MessageFormat;

public class InvalidTypeException extends RuntimeException
{
	public InvalidTypeException(Throwable thrown) { super(thrown); }
	
	public InvalidTypeException(String message, Object... param) { super(MessageFormat.format(message, param)); }
	
	public InvalidTypeException(String message, Throwable thrown, Object... param) {
		super(MessageFormat.format(message, param), thrown);
	}
	
}

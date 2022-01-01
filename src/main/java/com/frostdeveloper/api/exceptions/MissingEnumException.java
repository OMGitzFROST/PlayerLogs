package com.frostdeveloper.api.exceptions;

import java.text.MessageFormat;

public class MissingEnumException extends NullPointerException
{
	public MissingEnumException(String message, Object... param)
	{
		super(MessageFormat.format(message, param));
	}
}

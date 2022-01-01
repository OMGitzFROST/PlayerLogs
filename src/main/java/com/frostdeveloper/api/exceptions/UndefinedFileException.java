package com.frostdeveloper.api.exceptions;

public class UndefinedFileException extends IllegalArgumentException
{
	public UndefinedFileException()              { super();        }
	
	public UndefinedFileException(String message){ super(message); }
}

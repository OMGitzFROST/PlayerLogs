package com.frostdeveloper.api.handler;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.exceptions.UndefinedFileException;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * A class used to handle our exception sand create a report when the exception is caught.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class Report
{
	// CLASS INSTANCES
	private static final FrostAPI api = FrostAPI.getInstance();
	
	// CLASS SPECIFIC OBJECTS
	private final File targetFile;
	private final String version;
	
	/**
	 * A class constructor used to that only takes a file parameter to define
	 * the output directory, and a string parameter to define the current version
	 * in which the exception was caught.
	 *
	 * @param output Output directory.
	 * @param version Project version
	 * @since 1.2
	 */
	public Report(File output, String version)
	{
		this.targetFile = output;
		this.version    = version;
	}
	
	/**
	 * A class constructor used to that only takes a file parameter to define
	 * the output directory.
	 *
	 * @param output Output directory.
	 * @since 1.2
	 */
	public Report(File output)
	{
		this.targetFile = output;
		this.version    = null;
	}
	
	/**
	 * This method is used to create our report in the defined output location, if an output
	 * is not defined, this method will throw a {@link UndefinedFileException}, an output
	 * file has to be defined before this method inorder for this method to work.
	 *
	 * @param clazz The class that caused the exception
	 * @param thrown The exception thrown
	 * @param silent Determine whether a stacktrace should be printed to the console.
	 * @since 1.0
	 */
	public void create(@NotNull Class<?> clazz, @NotNull Throwable thrown, boolean silent)
	{
		Validate.notNull(targetFile, "Please define an output location");
		api.createParent(targetFile);
		api.renameFile(targetFile, new File(targetFile.getParentFile(), getCreated(targetFile)));
		
		printToWriter(targetFile, "Exception Date: " + api.getTimeNow());
		printToWriter(thrown.getMessage() != null, targetFile, "Error Message: " + thrown.getMessage());
		printToWriter(version != null, targetFile, "Version: " + version);
		printToWriter(targetFile, "Fault: " + clazz.getSimpleName());
		printToWriter(targetFile, "At Line: " + thrown.getStackTrace()[0].getLineNumber());
		printToWriter(targetFile, "");
		printToWriter(targetFile, thrown.getStackTrace());
		
		if (!silent) {
			thrown.printStackTrace();
		}
	}
	
	/**
	 * A method used to print a message to a file.
	 *
	 * @param targetFile Output file
	 * @param message Target message
	 * @param param Optional parameters
	 * @since 1.2
	 */
	public void printToWriter(File targetFile, Object message, Object... param)
	{
		try {
			FileWriter writer   = new FileWriter(targetFile, true);
			PrintWriter printer = new PrintWriter(writer);
			printer.println(api.format(String.valueOf(message), param));
			printer.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to print a message to a file if a condition is met.
	 *
	 * @param condition Required condition
	 * @param targetFile Output file
	 * @param message Target message
	 * @param param Optional parameters
	 * @since 1.2
	 */
	public void printToWriter(boolean condition, File targetFile, String message, Object... param)
	{
		if (condition) {
			try {
				PrintWriter printer = new PrintWriter(targetFile);
				printer.println(api.format(message, param));
				printer.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * A method used to return the file name including the file creation date, this
	 * method is used for old reports that need to be adjusted.
	 *
	 * @since 1.2
	 */
	public String getCreated(@NotNull File targetFile)
	{
		BasicFileAttributes attrs;
		try {
			if (targetFile.exists()) {
				attrs = Files.readAttributes(targetFile.toPath(), BasicFileAttributes.class);
				FileTime time = attrs.creationTime();
				
				String pattern = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				LocalDate date = LocalDate.parse(simpleDateFormat.format(new Date( time.toMillis())));
				return date + api.getExtension(targetFile.getName());
			}
			return null;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

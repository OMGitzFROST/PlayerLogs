package com.frostdeveloper.api.handler;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.exceptions.UndefinedFileException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A class used to handle our exception sand create a report when the exception is caught.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class Report
{
	// CLASS INSTANCES
	private final FrostAPI api = FrostAPI.getInstance();
	
	// CLASS SPECIFIC OBJECTS
	private static File targetFile;
	private static String version;
	
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
		try {
			if (targetFile == null) {
				throw new UndefinedFileException("Please define an output location");
			}
			
			String className = clazz.getSimpleName();
			
			PrintWriter printer = new PrintWriter(targetFile);
			printer.println("Exception Date: " + api.getTimeNow());
			
			if (thrown.getMessage() != null) printer.println("Error Message: " + thrown.getMessage());
			if (version != null) printer.println("Version: " + version);
			
			printer.println("Fault: " + className);
			printer.println("At Line: " + thrown.getStackTrace()[0].getLineNumber());
			printer.println("");
			thrown.printStackTrace(printer);
			
			api.createParent(targetFile);
			printer.close();
			
			if (!silent) {
				thrown.printStackTrace();
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method is used to define the log files output directory, in which directory should the file
	 * be created?
	 *
	 * @implNote This method needs to be executed before creating our report.
	 *
	 * @param targetFile Target output
	 * @since 1.0
	 */
	public void setOutputLocation(File targetFile) { Report.targetFile = targetFile; }
	
	/**
	 * A method used to define the projects current version once an error is caught, this
	 * method can be of utility when multi versions are available, or when an update occurs,
	 * you can see which version caused the error.
	 *
	 * @param version Current project version
	 * @since 1.0
	 */
	public void setProjectVersion(String version)  { Report.version = version;       }
}

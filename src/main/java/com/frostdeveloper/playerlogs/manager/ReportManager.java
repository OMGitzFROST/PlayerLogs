package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.util.Util;
import org.jetbrains.annotations.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;

/**
 * A class used to handle our exception handling, it handles our report making
 * tasks and configures the plugin to enable reports.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class ReportManager
{
	// CLASS INSTANCES
	private static final PlayerLogs plugin = PlayerLogs.getInstance();
	private static final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private static final File reportDir = Util.toFile("crash-reports");
	private static final File report = Util.toFile("crash-reports/report.log");
	
	/**
	 * The method used publicly to create our report, it will rename older files to
	 * (date-created-report.txt) and create new report.
	 *
	 * @param thrown Exception thrown
	 * @since 1.1
	 */
	public static void createReport(Class<?> cl, Exception thrown, boolean print)
	{
		try {
			File previousReport = new File(reportDir, api.format("crash-{0}.txt", getDateCreated(report)));
			
			if (reportDir.exists() || reportDir.mkdirs()) {
				cleanDirectory();
				
				if (report.exists() && report.renameTo(previousReport)) {
					writeToFile(cl, thrown);
				}
				
				if (!report.exists() && report.createNewFile()) {
					writeToFile(cl, thrown);
				}
				
				plugin.log(cl, Level.SEVERE, "report.print.success", report.getPath());
				
				if (print) {
					thrown.printStackTrace();
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to write our report to the report file.
	 *
	 * @param thrown The exception thrown
	 * @since 1.1
	 */
	private static void writeToFile(Class<?> cl, @NotNull Exception thrown)
	{
		try {
			PrintWriter writer = new PrintWriter(report);
			writer.println("Exception Date: " + getToday());
			if (thrown.getMessage() != null) {
				writer.println("Error Message: " + thrown.getMessage());
			}
			writer.println("Version: " + plugin.getDescription().getVersion());
			writer.println("Fault: " + cl.getSimpleName());
			writer.println("At Line: " + thrown.getStackTrace()[0].getLineNumber());
			writer.println("");
			thrown.printStackTrace(writer);
			writer.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to delete old reports from our report directory, currently files will delete
	 * if older than 2 days.
	 *
	 * @since 1.1
	 */
	public static void cleanDirectory()
	{
		if (reportDir.exists()) {
			if (reportDir.length() > 1) {
				for (File currentFile : Objects.requireNonNull(reportDir.listFiles())) {
					if (Objects.requireNonNull(getDateCreated(currentFile)).isBefore(getToday().minusDays(2))) {
						if (currentFile.delete()) {
							plugin.debug("index.delete.complete", currentFile.getName());
						}
					}
				}
			}
			if (reportDir.length() == 0) {
				if (reportDir.delete()) {
					plugin.debug("index.delete.complete", reportDir.getName());
				}
			}
			plugin.debug("index.clean.complete", reportDir.getName());
		}
	}
	
	/**
	 * A method used to return when a file was created.
	 *
	 * @param file Target File
	 * @return File creation date.
	 * @since 1.1
	 */
	private static @Nullable LocalDate getDateCreated(@NotNull File file)
	{
		BasicFileAttributes attrs;
		try {
			if (file.exists()) {
				attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				FileTime time = attrs.creationTime();
				
				String pattern = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				return LocalDate.parse(simpleDateFormat.format(new Date( time.toMillis())));
			}
			return null;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * A method used to get today's date.
	 *
	 * @return Today's date
	 * @since 1.1
	 */
	@Contract (" -> new")
	private static @NotNull LocalDate getToday() { return LocalDate.now(); }
}
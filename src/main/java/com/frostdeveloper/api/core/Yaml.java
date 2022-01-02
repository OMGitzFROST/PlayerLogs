package com.frostdeveloper.api.core;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.exceptions.NullPathException;
import com.frostdeveloper.api.exceptions.UndefinedFileException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class Yaml
{
	private final FrostAPI api = FrostAPI.getInstance();
	
	private final String name;
	private final File targetFile;
	private final InputStream inputStream;
	private final FileConfiguration config;
	
	public Yaml(@NotNull File targetFile)
	{
		this.targetFile = targetFile;
		this.name       = targetFile.getName();
		inputStream     = FrostAPI.getInstance().getResource(name);
		config          = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
		verifyParameters();
	}
	
	private void verifyParameters()
	{
		if (targetFile == null) {
			throw new UndefinedFileException("Please define a file using a file object inside the constructor");
		}
		if (FrostAPI.getInstance().isDirectory(targetFile)) {
			throw new IllegalArgumentException("The defined file cannot be a directory!");
		}
		if (inputStream == null) {
			throw new NullPointerException("Resource not found inside our jar file, Does it exist?");
		}
	}
	
	public FileConfiguration getConfig()
	{
		if (targetFile.exists()) {
			return YamlConfiguration.loadConfiguration(targetFile);
		}
		return config;
	}
	
	public File getFile()   { return targetFile; }
	
	public String getName() { return name;       }
	
	public String getString(String path)
	{
		if (getConfig().getString(path) == null) {
			throw new NullPathException("This path does not exist in our " + getName() + ": " + path);
		}
		return getConfig().getString(path);
	}
	
	public boolean getBoolean(String path)
	{
		if (getConfig().getString(path) == null) {
			throw new NullPathException("This path does not exist in our " + getName() + ": " + path);
		}
		return api.toBoolean(Objects.requireNonNull(getConfig().getString(path)));
	}
	
	public double getDouble(String path)
	{
		if (getConfig().getString(path) == null) {
			throw new NullPathException("This path does not exist in our " + getName() + ": " + path);
		}
		return api.toDouble(Objects.requireNonNull(getConfig().getString(path)));
	}
}

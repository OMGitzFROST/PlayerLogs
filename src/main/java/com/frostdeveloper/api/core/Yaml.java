package com.frostdeveloper.api.core;

import com.frostdeveloper.api.FrostAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * This class is designed to make creating new yaml files quick and easy, it features
 * methods to create a config file and load it from either a file object or from an
 * input stream, additionally this class features various getters for different object getters
 * such as strings, boolean lists, etc.
 *
 * @author OMGitzFROST
 * @since 1.0
 * @deprecated Yaml does not fit our project well, but will remain till a better file type is found.
 * (MARKED FOR REMOVAL)
 */
public class Yaml
{
	// API INSTANCE
	private final FrostAPI api = FrostAPI.getInstance();
	
	// CLASS SPECIFIC OBJECTS
	private final File targetFile;
	private final String name;
	private boolean blank;
	private FileConfiguration config;
	
	/**
	 * A class constructor used to initialize required variables and define
	 * the yaml's location.
	 *
	 * @param targetFile Target Location.
	 * @since 1.0
	 */
	public Yaml(@NotNull File targetFile)
	{
		this.targetFile = targetFile;
		name            = targetFile.getName();
	}
	
	/**
	 * A class constructor used to initialize required variables and define
	 * the yaml's location and whether an input stream search should
	 * be ignored.
	 *
	 * @param targetFile Target Location.
	 * @param blank Whether we should search our resource directory.
	 * @since 1.0
	 */
	public Yaml(@NotNull File targetFile, boolean blank)
	{
		this.targetFile = targetFile;
		this.blank      = blank;
		name            = targetFile.getName();
	}
	
	/**
	 * A method used to create our message file using the input stream assigned, if the constructor
	 * parameter 'blank' is set to true, this method will avoid the input stream and create an
	 * empty file and assign the desired name. If set to false, this method will look for the
	 * file inside the jar file and save it to the desired location.
	 * <br><br/>
	 * If replace is set to true, this method will always replace the existing file with
	 * a new copy of the file, this could be used if the files contents, are not meant
	 * to be stored and are replaced.
	 *
	 * @since 1.0
	 */
	@SuppressWarnings ("ResultOfMethodCallIgnored")
	public void createFile(boolean replace)
	{
		try {
			if (blank) {
				api.createParent(targetFile);
				targetFile.createNewFile();
			}
			else {
				InputStream input = FrostAPI.getInstance().getResource(name);
				Validate.notNull(input);
				api.createParent(targetFile);
				
				if (replace) {
					Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				else {
					if (!targetFile.exists()) {
						Files.copy(input, targetFile.toPath());
					}
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to create our message file using the input stream assigned, if the constructor
	 * parameter 'blank' is set to true, this method will avoid the input stream and create an
	 * empty file and assign the desired name. If set to false, this method will look for the
	 * file inside the jar file and save it to the desired location.
	 * <br><br/>
	 * By default, this method will not replace the existing file.
	 *
	 * @since 1.0
	 */
	public void createFile()                           { createFile(false);                                        }
	
	/**
	 * A method used to set a path with and assign a value to that path. If the boolean is
	 * set to true, this method will save your changes a soon as they are changed. Otherwise,
	 * no changes will be saved
	 * 
	 * @see #save(File) 
	 *
	 * @param path Target path
	 * @param value Desired value
	 * @since 1.0
	 */
	public void setDefault(String path, Object value, boolean save)
	{
		try {
			// VALIDATE THAT PARAMS ARE NOT NULL
			Validate.notNull(path); Validate.notNull(value);
			
			api.createParent(targetFile);
			config.set(path, value);
			
			if (save) {
				config.save(targetFile);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to set a path with and assign a value to that path. Note this method will
	 * not save the changes to your yaml file by default please add a boolean parameter
	 * to modify this setting.
	 *
	 * @see #save(File)
	 * 
	 * @param path Target path
	 * @param value Desired value
	 * @since 1.0
	 */
	public void setDefault(String path, Object value)  { setDefault(path, value, false);                           }
	
	//
	public void addDefault(String path, Object value)  { config.addDefault(path, value);                           }
	
	/**
	 * A method used to save our yaml file, If no changes were made, nothing will change.
	 *
	 * @param targetFile Save location.
	 * @since 1.0
	 */
	public void save(File targetFile)
	{
		try {
			config.save(targetFile);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to copy our defaults to the specified file.
	 *
	 * @param targetFile Target file location
	 * @since 1.0
	 */
	public void saveDefaults(File targetFile)
	{
		try {
			config.options().copyDefaults();
			config.save(targetFile);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to remove a path from our yaml configuration.
	 *
	 * @param path Target path.
	 * @since 1.0
	 */
	public void removeDefault(String path)             { config.set(path, null);                                   }
	
	/**
	 * A method used to return our yaml map.
	 *
	 * @return Yaml Map
	 */
	public FileConfiguration getConfig()
	{
		if (config == null) {
			reload();
		}
		return config;
	}
	
	/**
	 * A method used to reload our yaml file.
	 *
	 * @since 1.0
	 */
	public void reload()                               { config = YamlConfiguration.loadConfiguration(targetFile); }
	
	/**
	 * This method is used to return the yaml's file name
	 *
	 * @return File name
	 * @since 1.0
	 */
	public String getName()                            { return name;                                              }
	
	/**
	 * A method used to return the yaml file object. The file object does not
	 * have to exist, as it's only an object.
	 *
	 * @return Yaml File Object
	 * @since 1.0
	 */
	public File getFile()                              { return targetFile;                                        }
	
	/**
	 * A method used to return whether the yaml file exists.
	 *
	 * @return Whether file exists.
	 * @since 1.0
	 */
	public boolean exists()                            { return targetFile.exists();                               }
	
	/**
	 * A method used to return an object from our yaml file
	 *
	 * @param path Target path.
	 * @return String object
	 * @since 1.0
	 */
	public Object get(String path)                      { return getConfig().get(path);                            }
	
	/**
	 * A method used to return a string from our yaml file
	 *
	 * @param path Target path.
	 * @param def Default path
	 * @return String object
	 * @since 1.0
	 */
	public String getString(String path, Object def)   { return getConfig().getString(path, api.toString(def));    }
	
	/**
	 * A method used to return a string from our yaml file
	 *
	 * @param path Target path.
	 * @return String object
	 * @since 1.0
	 */
	public String getString(String path)               { return getConfig().getString(path);                       }
	
	/**
	 * A method used to return a boolean from our yaml file
	 *
	 * @param path Target path.
	 * @param def Default path
	 * @return Boolean object
	 * @since 1.0
	 */
	public boolean getBoolean(String path, Object def) { return getConfig().getBoolean(path, api.toBoolean(def));  }
	
	/**
	 * A method used to return a boolean from our yaml file
	 *
	 * @param path Target path.
	 * @return Boolean object
	 * @since 1.0
	 */
	public boolean getBoolean(String path)             { return getConfig().getBoolean(path);                      }
	
	/**
	 * A method used to return a double from our yaml file
	 *
	 * @param path Target path.
	 * @param def Default path
	 * @return Double object
	 * @since 1.0
	 */
	public double getDouble(String path, Object def)   { return getConfig().getDouble(path, api.toDouble(def));    }
	
	/**
	 * A method used to return a double from our yaml file
	 *
	 * @param path Target path.
	 * @return Double object
	 * @since 1.0
	 */
	public double getDouble(String path)               { return getConfig().getDouble(path);                       }
	
	/**
	 * A method used to return a list from our yaml file
	 *
	 * @param path Target path.
	 * @return List object
	 * @since 1.0
	 */
	public List<?> getList(String path)                { return getConfig().getList(path);                         }
	
	/**
	 * A method used to return a string list from our yaml file
	 *
	 * @param path Target path.
	 * @return String list
	 * @since 1.0
	 */
	public List<String> getStringList(String path)     { return getConfig().getStringList(path);                   }
	
	/**
	 * A method used to return a integer list from our yaml file
	 *
	 * @param path Target path.
	 * @return Integer list
	 * @since 1.0
	 */
	public List<Integer> getIntegerList(String path)   { return getConfig().getIntegerList(path);                  }
	
	/**
	 * A method used to return a boolean list from our yaml file
	 *
	 * @param path Target path.
	 * @return Boolean list
	 * @since 1.0
	 */
	public List<Boolean> getBooleanList(String path)   { return getConfig().getBooleanList(path);                  }
	
	/**
	 * A method used to return a float list from our yaml file
	 *
	 * @param path Target path.
	 * @return Float list
	 * @since 1.0
	 */
	public List<Float> getFloatList(String path)       { return getConfig().getFloatList(path);                    }
	
	/**
	 * A method used to return if a config path is a list
	 *
	 * @param path Target path
	 * @return Whether it's a list or not
	 * @since 1.0
	 */
	public boolean isList(String path)                 { return getConfig().isList(path);                          }
	
	/**
	 * A method used to return if a config path is a boolean
	 *
	 * @param path Target path
	 * @return Whether it's a boolean or not
	 * @since 1.0
	 */
	public boolean isBoolean(String path)              { return getConfig().isBoolean(path);                       }
	
	/**
	 * A method used to return if a config path is a string
	 *
	 * @param path Target path
	 * @return Whether it's a string or not
	 * @since 1.0
	 */
	public boolean isString(String path)               { return getConfig().isString(path);                        }
}

package com.frostdeveloper.api.core;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * The Properties class is designed to add new methods not seen in the {@link java.util.Properties}
 * class, makes using properties files much easier and extends Javas properties capabilities.
 * <p/>
 *
 * @apiNote By default, this class does not order any properties added to the Hashtable.<br><br/>
 * To order, remember to add a boolean value to the class constructor.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class Properties
{
	// CLASS OBJECTS
	private final java.util.Properties prop;
	private boolean ordered;
	
	/**
	 * Creates a new property list with no default values.
	 *
	 * @implNote Properties may be added in a random order as this constructor
	 * does not maintain its order inherently.
	 *
	 * @since 1.0
	 */
	public Properties()                      { prop = new java.util.Properties();  }
	
	/**
	 * Creates a new property list with no default values.
	 *
	 * @implNote If true, Properties are added in alphabetical order.
	 *
	 * @param ordered Determines whether the property list should be ordered alphabetically.
	 * @since 1.0
	 */
	public Properties(boolean ordered)
	{
		this.ordered = ordered;
		
		if (ordered) {
			prop = new java.util.Properties() {
				@Override public synchronized Set<Map.Entry<Object, Object>> entrySet() {
					return Collections.synchronizedSet(
							super.entrySet()
									.stream()
									.sorted(Comparator.comparing(e -> e.getKey().toString()))
									.collect(Collectors.toCollection(LinkedHashSet::new)));
				}
			};
		}
		else {
			prop = new java.util.Properties();
		}
	}
	
	/**
	 * A method used to write our properties list (keys and element pairs), in a format
	 * suitable for property loading. This method will include your comment at the top of the
	 * file when printing.
	 *
	 * @see #store(File)
	 *
	 * @throws ClassCastException if this {@code Properties} object contains any keys or values that are not {@code Strings}.
	 * @throws NullPointerException if {@code out} is null.
	 *
	 * @param targetFile Storing location.
	 * @param comment A description of the property list.
	 * @since 1.0
	 */
	public void store(@NotNull File targetFile, String comment)
	{
		try(FileOutputStream outputStream = new FileOutputStream(targetFile)) {
			if (targetFile.getParentFile().exists() || targetFile.getParentFile().mkdirs()) {
				prop.store(outputStream, comment);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to write our properties list (keys and element pairs), in a format
	 * suitable for property loading. This method will use Java's default comments
	 * when storing to file.
	 *
	 * @see #store(File, String)
	 *
	 * @throws ClassCastException if this {@code Properties} object contains any keys or values that are not {@code Strings}.
	 * @throws NullPointerException if {@code out} is null.
	 *
	 * @param targetFile Storing location.
	 * @since 1.0
	 */
	public void store(@NotNull File targetFile)
	{
		try(FileOutputStream outputStream = new FileOutputStream(targetFile)) {
			if (targetFile.getParentFile().exists() || targetFile.getParentFile().mkdirs()) {
				prop.store(outputStream, null);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to read a property list (keys and element pairs). The input stream is
	 * closed by this method after it returns.
	 *
	 * @throws IllegalArgumentException if the input stream contains a malformed Unicode escape sequence.
	 * @throws NullPointerException if {@code inStream} is null.
	 *
	 * @param targetFile File being read.
	 * @since 1.0
	 */
	public synchronized void load(@NotNull File targetFile)
	{
		try {
			FileInputStream inputStream = new FileInputStream(targetFile);
			prop.load(inputStream);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to read a property list (keys and element pairs). The input stream is
	 * closed by this method after it returns.
	 *
	 * @throws IllegalArgumentException if the input stream contains a malformed Unicode escape sequence.
	 * @throws NullPointerException if {@code inStream} is null.
	 *
	 * @param inputStream The input stream
	 * @since 1.0
	 */
	public synchronized void load(InputStream inputStream)
	{
		try {
			prop.load(inputStream);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to set a value to a property inside our property list if it
	 * does not already exist.
	 * 
	 * @apiNote By default, this method will not overwrite the key's current value.
	 * To overwrite, use {@link #setProperty(String, Object, boolean)}
	 * 
	 * @see #setProperty(String, Object, boolean)
	 * 
	 * @param key The target key.
	 * @param value The desired value.
	 * @since 1.0
	 */
	public void setProperty(String key, Object value)
	{
		if (getProperty(key) == null && !getProperty(key).equals(value)) {
			prop.setProperty(key, String.valueOf(value));
		}
	}
	
	/**
	 * A method used to set a value to a property inside our property list if it
	 * does not already exist. If, the key already exists, you need to specify
	 * whether this method should override its current value.
	 *
	 * @apiNote If you choose to not overwrite its value, nothing will happen to the
	 * key's current value.
	 * 
	 * @see #setProperty(String, Object) 
	 *
	 * @param key The target key.
	 * @param value The desired value.
	 * @param replace Enable overwriting.
	 * @since 1.0
	 */
	public void setProperty(String key, Object value, boolean replace)
	{
		if (getProperty(key) == null || replace && !getProperty(key).equals(String.valueOf(value))) {
			prop.setProperty(key, String.valueOf(value));
		}
	}
	
	/**
	 * A method used to remove a property from a properties map. Keep in mind this property does not
	 * save changes by default.
	 *
	 * @param key Target key
	 * @since 1.0
	 */
	public void removeProperty(String key)
	{
		if (getProperty(key) != null) {
			prop.remove(key);
		}
	}
	
	/**
	 * A method used to search for a specific property key inside our property list, If the
	 * key is not found, this method will return null
	 *
	 * @apiNote To avoid return the default value, remember to load a property file using {@link #load(File)}
	 * or load from an input stream using {@link #load(InputStream)}.
	 *
	 * @see #setProperty(String, Object)
	 * @see #setProperty(String, Object, boolean)
	 *
	 * @param key The target key.
	 * @return The value in the property list.
	 * @since 1.0
	 */
	public String getProperty(String key)    { return prop.getProperty(key); }
	
	/**
	 * A method used to search for a specific property key inside our property list, If the
	 * key is not found, this method will return the specified default property value.
	 *
	 * @apiNote To avoid return the default value, remember to load a property file using {@link #load(File)} 
	 * or load from an input stream using {@link #load(InputStream)}.
	 * 
	 * @see #setProperty(String, Object)
	 * @see #setProperty(String, Object, boolean)
	 *
	 * @param key The target key.
	 * @param defaultValue A default value.
	 * @return The value in the property list.
	 * @since 1.0
	 */
	public String getProperty(String key, Object defaultValue)
	{
		return prop.getProperty(key, String.valueOf(defaultValue));
	}
	
	/**
	 * Returns {@code true} if this map contains no key-value mappings.
	 *
	 * @return {@code true} if this map contains no key-value mappings
	 * @since 1.0
	 */
	public boolean isEmpty()                 { return prop.isEmpty();              }
	
	/**
	 * A method used to return whether a properties are to be in alphabetical order.
	 *
	 * @return Whether in alphabetical order.
	 * @since 1.0
	 */
	public boolean isOrdered()               { return ordered;                     }
	
	/**
	 * Tests if the specified object is a key in this table.
	 *
	 * @param  key possible key
	 * @return {@code true} if and only if the specified object
	 *         is a key in this table, as determined by the
	 *         {@code equals} method; {@code false} otherwise
	 * @throws NullPointerException if the specified key is null
	 * @since 1.0
	 */
	public boolean containsKey(Object key)   { return prop.containsKey(key);       }
	
	/**
	 * A method used to return a set of string property names for a property map.
	 *
	 * @return Set of string property names
	 * @since 1.0
	 */
	public Set<String> stringPropertyNames() { return prop.stringPropertyNames();  }
	
	/**
	 * Removes all the mappings from this map.
	 *
	 * @since 1.0
	 */
	public void clear()                      { prop.clear();                       }
}

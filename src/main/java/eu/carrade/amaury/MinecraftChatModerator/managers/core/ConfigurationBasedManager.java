/*
 * Copyright or © or Copr. Amaury Carrade (2015)
 *
 * http://amaury.carrade.eu
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package eu.carrade.amaury.MinecraftChatModerator.managers.core;

import eu.carrade.amaury.MinecraftChatModerator.MinecraftChatModerator;
import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents a class managing a list of objects instances
 * enabled or not following a configuration file.
 *
 * Thread-safe.
 *
 * @param <MANAGED> The object being managed by this manager.
 *
 * @author Amaury Carrade
 */
public abstract class ConfigurationBasedManager<MANAGED>
{
	/**
	 * The registered and loaded instances being managed.
	 */
	protected final Set<MANAGED> managed = new CopyOnWriteArraySet<>();

	/**
	 * The object loaded following the configuration file, when the method
	 * to load them is called.
	 */
	private final Set<Class<? extends MANAGED>> toBeLoadedFromConfig = new CopyOnWriteArraySet<>();


	/**
	 * Registers on-the-fly an instance into the manager.
	 *
	 * @param registerMe The instance.
	 * @return {@code true} if effectively added (not already registered).
	 */
	public boolean register(MANAGED registerMe)
	{
		final boolean added = managed.add(registerMe);

		if(added)
		{
			MinecraftChatModerator.get().getLogger().info("Registered " + registerMe.getClass().getSimpleName() + " successfully.");
		}

		return added;
	}

	/**
	 * Register a class to be loaded when [METHOD] is called, if enabled in the configuration file
	 * associated.
	 * <p></p>
	 * <strong>Important</strong> — such a class NEEDS TO have a constructor accepting a
	 * {@link org.bukkit.configuration.ConfigurationSection} as an argument, eventually {@code null},
	 * if they need to have options in the configuration file.<br />
	 * If such a constructor is not present, a constructor without arguments will be used.
	 *
	 * @param clazz The class to register.
	 */
	protected void loadAfterFollowingConfig(Class<? extends MANAGED> clazz)
	{
		toBeLoadedFromConfig.add(clazz);
	}

	/**
	 * Loads the classes registered by {@link #loadAfterFollowingConfig(Class)}, if enabled in the
	 * configuration file.
	 *
	 * <p>
	 *     The configuration file format is the following.
	 * </p>
	 * <p>
	 *     Each root-key of this configuration section must be the name of a managed class
	 *     pre-registered using {@link #loadAfterFollowingConfig(Class)}, or this name without the
	 *     trailing {@code suffix} (if it exists).<br />
	 *     As example, with “Bar” as the suffix, for the class “FooBar”, the following keys will be
	 *     accepted:
	 *     <ul>
	 *          <li>{@code FooBar} ; </li>
	 *          <li>{@code Foo}.</li>
	 *     </ul>
	 * </p>
	 * <p>
	 *     The configuration “sub-section” of each of these root keys can be of two different types.
	 *     <ul>
	 *         <li>
	 *             <strong>No configuration section: a simple boolean.</strong><br />
	 *             In this case, this boolean will represent the “enabled” state of this filter.<br />
	 *             No config will be transmitted to the subsequent managed object.<br />
	 *             Example:
	 *             <blockquote>
	 *                 <pre>
	 * FooBar: true
	 *                 </pre>
	 *             </blockquote>
	 *         </li>
	 *         <li>
	 *             <strong>With a configuration section.</strong><br />
	 *             The configuration section have to follow this format:
	 *             <blockquote>
	 *                 <pre>
	 * FooBar:
	 *     enabled: true  # or false
	 *     options:
	 *         # anything.
	 *                 </pre>
	 *             </blockquote>
	 *             The {@code enabled} tag controls weither or not this is enabled.<br />
	 *             The {@code options} configuration section represents the options passed to the
	 *             constructor of the subsequent managed object (if such a constructor is present).
	 *         </li>
	 *     </ul>
	 * </p>
	 *
	 * @param config The configuration section containing the whole config for this kind of managed
	 *               things.
	 * @param suffix The classes usual suffix removable from the class name to find the configuration
	 *               key (see above).
	 */
	protected void load(ConfigurationSection config, String suffix)
	{
		final Logger logger = MinecraftChatModerator.get().getLogger();

		logger.info("Loading " + config.getName() + "...");

		for(Class<? extends MANAGED> type : toBeLoadedFromConfig)
		{
			final String managedName = type.getSimpleName();
			String configurationKey = managedName;

			if(!config.contains(configurationKey) && configurationKey.endsWith(suffix))
			{
				configurationKey = configurationKey.substring(0, configurationKey.length() - suffix.length());
				if(!config.contains(configurationKey))
				{
					logger.info(managedName + " not found in config - skipping.");
					continue;
				}
			}

			final Boolean enabled;
			final ConfigurationSection options;

			if(!config.isConfigurationSection(configurationKey)) // Simple case: “managedName: true/false”.
			{
				enabled = config.getBoolean(configurationKey, false);
				options = null;
			}
			else // Complex case: configuration section with "enabled" and "options".
			{
				ConfigurationSection managedConfig = config.getConfigurationSection(configurationKey);

				enabled = managedConfig.getBoolean("enabled", false);
				options = managedConfig.isConfigurationSection("options") ? managedConfig.getConfigurationSection("options") : null;
			}

			if(!enabled) continue;


			MANAGED managedInstance;

			try
			{
				try
				{
					Constructor<? extends MANAGED> optionsConstructor = type.getConstructor(ConfigurationSection.class);
					managedInstance = optionsConstructor.newInstance(options);
				}
				catch (NoSuchMethodException ignored)
				{
					try
					{
						Constructor<? extends MANAGED> emptyConstructor = type.getConstructor();
						managedInstance = emptyConstructor.newInstance();
					}
					catch (NoSuchMethodException e)
					{
						logger.log(Level.SEVERE, "Invalid constructor (neither with ConfigurationSection nor with nothing) in the " + managedName + " class (" + type.getName() + "), skipping.");
						continue;
					}
				}
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				logger.log(Level.SEVERE, "Unable to load the " + managedName + " class (" + type.getName() + "), skipping.", e);
				continue;
			}
			catch (InvocationTargetException e)
			{
				logger.log(Level.SEVERE, "An exception occurred while loading " + managedName + ", skipping.", e.getCause());
				continue;
			}

			register(managedInstance);
		}

		logger.info("Done.");
	}

	/**
	 * Loads the classes registered by {@link #loadAfterFollowingConfig(Class)}, if enabled in the
	 * configuration file.
	 *
	 * <p>
	 *     Here, the suffix (see {@link #load(ConfigurationSection, String)}) is the capitalized name of
	 *     the configuration section without the last character (usually the “s” of the plural form,
	 *     that's why).
	 * </p>
	 *
	 * @param config The configuration section containing the whole config for this kind of managed
	 *               things.
	 *
	 * @see {@link #load(ConfigurationSection, String)} for detailed informations about the configuration
	 *      format.
	 */
	protected void load(ConfigurationSection config)
	{
		final String suffix = WordUtils.capitalize(config.getName());
		load(config, suffix.substring(0, suffix.length() - 1));
	}
}

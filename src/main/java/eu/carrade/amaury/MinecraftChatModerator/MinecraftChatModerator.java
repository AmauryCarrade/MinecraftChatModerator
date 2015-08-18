/*
 * Copyright (C) 2015 Amaury Carrade
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.carrade.amaury.MinecraftChatModerator;

import eu.carrade.amaury.MinecraftChatModerator.listeners.*;
import eu.carrade.amaury.MinecraftChatModerator.managers.*;
import org.bukkit.plugin.java.*;


public class MinecraftChatModerator extends JavaPlugin
{
	private static MinecraftChatModerator instance;

	private PlayersMessagesManager messagesManager;
	private FiltersManager filtersManager;
	private AnalyzersManager analyzersManager;


	@Override
	public void onEnable()
	{
		instance = this;

		messagesManager  = new PlayersMessagesManager();
		filtersManager   = new FiltersManager();
		analyzersManager = new AnalyzersManager();

		getServer().getPluginManager().registerEvents(new ChatListener(), this);
	}


	public PlayersMessagesManager getMessagesManager()
	{
		return messagesManager;
	}

	public AnalyzersManager getAnalyzersManager()
	{
		return analyzersManager;
	}

	public FiltersManager getFiltersManager()
	{
		return filtersManager;
	}


	public static MinecraftChatModerator get()
	{
		return instance;
	}
}

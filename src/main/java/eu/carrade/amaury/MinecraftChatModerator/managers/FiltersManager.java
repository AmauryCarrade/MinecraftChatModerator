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

package eu.carrade.amaury.MinecraftChatModerator.managers;

import eu.carrade.amaury.MinecraftChatModerator.filters.*;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.*;
import eu.carrade.amaury.MinecraftChatModerator.utils.*;
import org.bukkit.*;

import java.util.*;
import java.util.concurrent.*;


public class FiltersManager
{
	private final Set<ChatFilter> filters = new CopyOnWriteArraySet<>();

	public FiltersManager()
	{
		// TODO Register filters
	}


	/**
	 * Registers a new filter in the system.
	 *
	 * @param filter The filter.
	 */
	public void registerFilter(ChatFilter filter)
	{
		filters.add(filter);
	}

	/**
	 * Filters the given message.
	 *
	 * @param message The message.
	 * @throws MessageRequiresCensorshipException If the message needs to be censored.
	 */
	public void filterMessage(ChatMessage message) throws MessageRequiresCensorshipException
	{
		for(ChatFilter filter : filters)
		{
			try
			{
				filter.filter(message);
			}
			catch (MessageRequiresCensorshipException e)
			{
				String why = Objects.toString(e.getWhy(), "aucun");

				AsyncMessageSender.sendErrorMessage(
						message.getSender(),
						ChatColor.RED + "" + ChatColor.BOLD + "Votre message a été censuré.",
						ChatColor.GRAY + "Motif laissé : " + why + "."
				);

				message.setCensored(true);

				// We throws the exception for the event class to catch it.
				throw e;
			}
		}
	}
}

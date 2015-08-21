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

package eu.carrade.amaury.MinecraftChatModerator.managers;

import eu.carrade.amaury.MinecraftChatModerator.filters.CensorshipFilter;
import eu.carrade.amaury.MinecraftChatModerator.filters.ChatFilter;
import eu.carrade.amaury.MinecraftChatModerator.filters.MessageRequiresCensorshipException;
import eu.carrade.amaury.MinecraftChatModerator.filters.PingFilter;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.ChatMessage;
import eu.carrade.amaury.MinecraftChatModerator.utils.AsyncMessageSender;
import org.bukkit.ChatColor;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class FiltersManager
{
	private final Set<ChatFilter> filters = new CopyOnWriteArraySet<>();

	public FiltersManager()
	{
		registerFilter(new CensorshipFilter());
		registerFilter(new PingFilter());
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
						ChatColor.GRAY + "Motif : " + why + "."
				);

				message.setCensored(true);

				// We throws the exception for the event class to catch it.
				throw e;
			}
		}
	}
}

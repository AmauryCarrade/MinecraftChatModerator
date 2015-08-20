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

package eu.carrade.amaury.MinecraftChatModerator.listeners;

import eu.carrade.amaury.MinecraftChatModerator.*;
import eu.carrade.amaury.MinecraftChatModerator.filters.*;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.*;
import eu.carrade.amaury.MinecraftChatModerator.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.*;


public class ChatListener implements Listener
{

	private final MinecraftChatModerator p = MinecraftChatModerator.get();

	@EventHandler(priority = EventPriority.HIGHEST) // MONITOR unavailable because we may have to cancel the event.
	public void onAsyncPlayerChat(AsyncPlayerChatEvent ev)
	{
		ChatMessage message       = p.getMessagesManager().savePlayerMessage(ev.getPlayer().getUniqueId(), ev.getMessage());
		PlayerChatHistory history = p.getMessagesManager().getChatHistory(ev.getPlayer().getUniqueId());

		history.cleanup();


		/* **  Filters  ** */

		try
		{
			p.getFiltersManager().filterMessage(message);
			ev.setMessage(message.getMessage());

			Map<UUID, String> specificMessages = message.getSpecificMessages();
			if(!specificMessages.isEmpty())
			{
				for (Map.Entry<UUID, String> specificMessage : specificMessages.entrySet())
				{
					AsyncMessageSender.sendMessage(
							specificMessage.getKey(),
							String.format(ev.getFormat(), ev.getPlayer().getDisplayName(), specificMessage.getValue())
					);
				}

				final Set<UUID> excludedRecipientsUUID = specificMessages.keySet();
				ev.getRecipients().removeIf(player -> excludedRecipientsUUID.contains(player.getUniqueId()));
			}
		}
		catch (MessageRequiresCensorshipException e)
		{
			ev.setCancelled(true);
			ev.setMessage("");
		}


		/* **  Analyzers ** */

		p.getAnalyzersManager().runAnalyzes(history);
	}
}

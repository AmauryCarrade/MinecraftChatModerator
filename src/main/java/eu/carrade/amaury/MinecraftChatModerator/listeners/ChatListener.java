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

package eu.carrade.amaury.MinecraftChatModerator.listeners;

import eu.carrade.amaury.MinecraftChatModerator.MinecraftChatModerator;
import eu.carrade.amaury.MinecraftChatModerator.filters.MessageRequiresCensorshipException;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.ChatMessage;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.PlayerChatHistory;
import eu.carrade.amaury.MinecraftChatModerator.utils.AsyncMessageSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class ChatListener implements Listener
{
	private final MinecraftChatModerator p;
	
	public ChatListener(MinecraftChatModerator p)
	{
		this.p = p;
	}

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

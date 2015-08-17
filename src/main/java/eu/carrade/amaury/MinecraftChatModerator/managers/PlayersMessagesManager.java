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

import eu.carrade.amaury.MinecraftChatModerator.rawtypes.*;

import java.util.*;
import java.util.concurrent.*;


public class PlayersMessagesManager
{
	private Map<UUID, PlayerChatHistory> playersHistory = new ConcurrentHashMap<>();


	/**
	 * Returns the chat history of the given player.
	 *
	 * @param id The player's UUID.
	 * @return The recent chat history.
	 * An history is created on the fly if needed. This will never return {@code null}.
	 */
	public PlayerChatHistory getChatHistory(UUID id)
	{
		PlayerChatHistory history = playersHistory.get(id);

		if(history != null)
		{
			return history;
		}
		else
		{
			history = new PlayerChatHistory(id);
			playersHistory.put(id, history);

			return history;
		}
	}

	/**
	 * Saves a message sent by a player.
	 *
	 * @param id The player's UUID.
	 * @param message The message's body.
	 * @param time The time this message was sent.
	 */
	public void savePlayerMessage(UUID id, String message, Long time)
	{
		getChatHistory(id).addMessage(new ChatMessage(id, message, time));
	}

	/**
	 * Saves a message sent by a player just now.
	 *
	 * @param id The player's UUID.
	 * @param message The message's body.
	 */
	public void savePlayerMessage(UUID id, String message)
	{
		savePlayerMessage(id, message, System.currentTimeMillis());
	}
}

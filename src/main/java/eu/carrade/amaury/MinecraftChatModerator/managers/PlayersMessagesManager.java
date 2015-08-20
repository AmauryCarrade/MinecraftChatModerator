/*
 * Copyright or © or Copr. AmauryCarrade (2015)
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

import eu.carrade.amaury.MinecraftChatModerator.rawtypes.*;

import java.util.*;
import java.util.concurrent.*;


public class PlayersMessagesManager
{
	private final Map<UUID, PlayerChatHistory> playersHistory = new ConcurrentHashMap<>();

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
	 *
	 * @return The {@link ChatMessage} object inserted in the chat history.
	 */
	public ChatMessage savePlayerMessage(UUID id, String message, Long time)
	{
		final ChatMessage chatMessage = new ChatMessage(id, message, time);

		getChatHistory(id).addMessage(chatMessage);
		return chatMessage;
	}

	/**
	 * Saves a message sent by a player just now.
	 *
	 * @param id The player's UUID.
	 * @param message The message's body.
	 *
	 * @return The {@link ChatMessage} object inserted in the chat history.
	 */
	public ChatMessage savePlayerMessage(UUID id, String message)
	{
		return savePlayerMessage(id, message, System.currentTimeMillis());
	}
}

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

package eu.carrade.amaury.MinecraftChatModerator.rawtypes;

import java.util.*;
import java.util.concurrent.*;


public class PlayerChatHistory
{
	private static final Long MAXIMUM_MESSAGE_AGE = 120000l; // ms (120'000 = 2 minutes)

	private UUID playerID;
	private Set<ChatMessage> messages = new CopyOnWriteArraySet<>();


	/**
	 * Constructs a new chat history belonging to the given player.
	 *
	 * @param playerID The history's owner.
	 *
	 * @throws NullPointerException If the {@code playerID} is {@code null}.
	 */
	public PlayerChatHistory(UUID playerID)
	{
		this.playerID = Objects.requireNonNull(playerID, "The player UUID cannot be null!");
	}


	/**
	 * Adds a message to this chat history.
	 *
	 * @param message The message to add.
	 *
	 * @throws IllegalArgumentException If the message does not belong to the owner of this history.
	 */
	public void addMessage(ChatMessage message)
	{
		if(!message.getSender().equals(playerID))
			throw new IllegalArgumentException("Cannot add a message to this player history, UUIDs mismatch! (" + message.getSender() + " (message) versus " + playerID + "(history).)");

		messages.add(message);
	}


	/**
	 * Forgets all messages older than {@link #MAXIMUM_MESSAGE_AGE} ms.
	 */
	public void cleanup()
	{
		Long now = System.currentTimeMillis();

		new HashSet<>(messages).stream()
				.filter(message -> message.getTime() < now - MAXIMUM_MESSAGE_AGE)
				.forEach(messages::remove);
	}


	/**
	 * Returns the history's owner's UUID.
	 *
	 * @return The UUID.
	 */
	public UUID getPlayerID()
	{
		return playerID;
	}

	/**
	 * Returns the stored messages.
	 *
	 * @return The messages.
	 */
	public Set<ChatMessage> getMessages()
	{
		return messages;
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof PlayerChatHistory)) return false;

		PlayerChatHistory that = (PlayerChatHistory) o;

		return playerID.equals(that.playerID) && messages.equals(that.messages);

	}

	@Override
	public int hashCode()
	{
		return Objects.hash(playerID, messages);
	}
}

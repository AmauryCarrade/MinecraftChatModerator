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

package eu.carrade.amaury.MinecraftChatModerator.rawtypes;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;


public class PlayerChatHistory
{
	private static final Long MAXIMUM_MESSAGE_AGE = 120000l; // ms (120'000 = 2 minutes)

	private final UUID playerID;
	private final Set<ChatMessage> messages = new CopyOnWriteArraySet<>();


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
		Long oldestAllowedTime = System.currentTimeMillis() - MAXIMUM_MESSAGE_AGE;

		new HashSet<>(messages).stream()
				.filter(message -> message.getTime() < oldestAllowedTime)
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

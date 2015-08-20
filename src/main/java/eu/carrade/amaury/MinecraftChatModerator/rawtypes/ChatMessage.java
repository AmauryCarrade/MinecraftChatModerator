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

package eu.carrade.amaury.MinecraftChatModerator.rawtypes;

import java.util.*;
import java.util.concurrent.*;


public class ChatMessage
{
	private final UUID sender;
	private String message;
	private final Long time;

	private final Map<UUID, String> messageForSpecificPlayer = new ConcurrentHashMap<>();

	private Boolean censored = false;


	/**
	 * Constructs a new chat message.
	 *
	 * @param sender The message's sender.
	 * @param message The message's body.
	 * @param time The milli-timestamp when the message was sent.
	 *
	 * @throws NullPointerException If any of the arguments is null.
	 */
	public ChatMessage(UUID sender, String message, Long time)
	{
		this.sender = Objects.requireNonNull(sender, "The sender cannot be null!");
		this.message = Objects.requireNonNull(message, "The message cannot be null!");
		this.time = Objects.requireNonNull(time, "The time cannot be null!");
	}

	/**
	 * Constructs a new chat message sent just now (when the method is called).
	 *
	 * @param sender The message's sender.
	 * @param message The message's body.
	 *
	 * @throws NullPointerException If any of the arguments is null.
	 */
	public ChatMessage(UUID sender, String message)
	{
		this(sender, message, System.currentTimeMillis());
	}


	/**
	 * Returns the message's sender.
	 *
	 * @return The sender.
	 */
	public UUID getSender()
	{
		return sender;
	}

	/**
	 * Returns the message's body.
	 *
	 * @return The message.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Returns the message's body for specific receivers.
	 *
	 * @return A map UUID -> message (String) (where the UUID is the specific receiver of this message).
	 */
	public Map<UUID, String> getSpecificMessages()
	{
		return messageForSpecificPlayer;
	}

	/**
	 * Updates the message's body.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Updates the message's body for a specific receiver.
	 */
	public void setMessage(UUID receiver, String message)
	{
		messageForSpecificPlayer.put(receiver, message);
	}

	/**
	 * Returns the message's sending time.
	 *
	 * @return The milli-timestamp when the message was sent.
	 */
	public Long getTime()
	{
		return time;
	}


	/**
	 * Returns true if this message was censored by a filter.
	 *
	 * @return The censorship status.
	 */
	public Boolean getCensored()
	{
		return censored;
	}

	/**
	 * Updates the censorship status.
	 *
	 * @param censored {@code true} if censored.
	 */
	public void setCensored(Boolean censored)
	{
		this.censored = censored;
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof ChatMessage)) return false;

		ChatMessage that = (ChatMessage) o;

		return sender.equals(that.sender) && message.equals(that.message) && time.equals(that.time);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(sender, message, time);
	}

	@Override
	public String toString()
	{
		return "ChatMessage{" +
				"sender=" + sender +
				", message='" + message + '\'' +
				", time=" + time + '}';
	}
}

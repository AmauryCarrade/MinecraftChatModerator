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


public class ChatMessage
{
	private final UUID sender;
	private String message;
	private final Long time;

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
	 * Updates the message's body.
	 */
	public void setMessage(String message)
	{
		this.message = message;
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
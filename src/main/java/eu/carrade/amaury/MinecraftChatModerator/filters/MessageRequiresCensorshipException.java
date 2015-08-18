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

package eu.carrade.amaury.MinecraftChatModerator.filters;

/**
 * Thrown by a chat filter when a message needs to be censored.
 */
public class MessageRequiresCensorshipException extends Exception
{
	String why;

	/**
	 * Construct a censorship exception without explanation.
	 */
	public MessageRequiresCensorshipException()
	{
		this(null);
	}

	/**
	 * @param why Why this message was censored (in short).
	 */
	public MessageRequiresCensorshipException(String why)
	{
		this.why = why;
	}

	public String getWhy()
	{
		return why;
	}
}

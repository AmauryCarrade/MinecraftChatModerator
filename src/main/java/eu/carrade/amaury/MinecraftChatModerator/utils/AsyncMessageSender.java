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

package eu.carrade.amaury.MinecraftChatModerator.utils;

import eu.carrade.amaury.MinecraftChatModerator.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;


public final class AsyncMessageSender
{
	private AsyncMessageSender() {}

	public static void sendMessage(UUID receiver, String... messages)
	{
		Bukkit.getScheduler().runTask(MinecraftChatModerator.get(), () -> {
			Player player = Bukkit.getPlayer(receiver);

			if(player != null && player.isOnline())
			{
				for(String message : messages)
				{
					player.sendMessage(message);
				}
			}
		});
	}

	public static void sendErrorMessage(UUID receiver, String... messages)
	{
		String[] errorMessages = new String[messages.length + 2];

		errorMessages[0] = "";
		errorMessages[errorMessages.length - 1] = "";

		for (int i = 0; i < messages.length; i++)
		{
			errorMessages[i + 1] = ChatColor.DARK_GRAY + "» " + ChatColor.RESET + messages[i];
		}

		sendMessage(receiver, errorMessages);
	}
}

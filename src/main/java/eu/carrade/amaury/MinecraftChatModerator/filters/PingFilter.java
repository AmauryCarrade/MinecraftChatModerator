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

import eu.carrade.amaury.MinecraftChatModerator.rawtypes.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;


public class PingFilter implements ChatFilter
{
	private static final ChatColor PING_COLOR = ChatColor.GOLD;

	private static final Random random = new Random();

	private static final String PREVIOUS_FORMATTING_MARK = "×××××FORMATTING_" + Math.abs(random.nextInt()) + "×××××";


	@Override
	public void filter(ChatMessage message) throws MessageRequiresCensorshipException
	{
		String body    = message.getMessage();
		String bodyLow = body.toLowerCase();

		for (Player player : Bukkit.getOnlinePlayers())
		{
			UUID    playerID       = player.getUniqueId();
			String  playerName     = player.getName();
			String  playerNameLow  = playerName.toLowerCase();
			Integer playerNameSize = playerName.length();

			if (bodyLow.contains(playerNameLow))
			{
				String pingMessage = body.replaceAll("(?i)(" + playerName + ")", PREVIOUS_FORMATTING_MARK + "$1");

				String[] pingMessageSplit = pingMessage.split(PREVIOUS_FORMATTING_MARK);
				String formattedPingMessage = "";

				for (int i = 0; i < pingMessageSplit.length; i++)
				{
					formattedPingMessage += pingMessageSplit[i];

					if(i != pingMessageSplit.length - 1)
					{
						String previousColors = ChatColor.getLastColors(formattedPingMessage);
						String previousFormatting = ""; // Only format like bold..., no colors.

						// The ChatColor.getLastColors method always returns either
						// only non-colors code, either a color code at the beginning and then
						// only non-color codes.
						if(previousColors.length() >= 2)
						{
							ChatColor firstCode = ChatColor.getByChar(previousColors.charAt(1));
							if (!firstCode.isColor())
							{
								previousFormatting = previousColors;
							}
							else if (previousColors.length() > 2)
							{
								previousFormatting = previousColors.substring(2);
							}
						}

						if (previousColors.isEmpty()) previousColors = ChatColor.RESET.toString();

						formattedPingMessage += PING_COLOR + previousFormatting;

						// Insertion of the previous colors after the pseudonym in the next string.
						pingMessageSplit[i + 1] = new StringBuilder(pingMessageSplit[i + 1]).insert(playerNameSize, previousColors).toString();
					}
				}

				message.setMessage(playerID, formattedPingMessage);
			}
		}
	}
}

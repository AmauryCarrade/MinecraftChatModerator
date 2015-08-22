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

package eu.carrade.amaury.MinecraftChatModerator.filters;

import eu.carrade.amaury.MinecraftChatModerator.filters.core.ChatFilter;
import eu.carrade.amaury.MinecraftChatModerator.filters.core.MessageRequiresCensorshipException;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;


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

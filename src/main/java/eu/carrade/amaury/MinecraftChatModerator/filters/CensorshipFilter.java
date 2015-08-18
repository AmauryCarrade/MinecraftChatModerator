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

import eu.carrade.amaury.MinecraftChatModerator.*;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.*;
import org.bukkit.*;

import java.util.*;


public class CensorshipFilter implements ChatFilter
{

	private Set<String> censoredWords = new HashSet<>();


	public CensorshipFilter()
	{
		MinecraftChatModerator.get().getConfig().getStringList("censoredWords").stream()
				.map(String::trim)
				.forEach(censoredWords::add);
	}


	@Override
	public void filter(ChatMessage message) throws MessageRequiresCensorshipException
	{
		String[] words = ChatColor.stripColor(message.getMessage())
				.toLowerCase()
				.split("[-—–,;\\(\\)\\[\\]\\{\\}\\.'=+*×:?!⋅^¨$£øµ&\t ]");

		for(String word : words)
		{
			if(censoredWords.contains(word))
				throw new MessageRequiresCensorshipException("mot interdit (« " + word + " »)");
		}
	}
}
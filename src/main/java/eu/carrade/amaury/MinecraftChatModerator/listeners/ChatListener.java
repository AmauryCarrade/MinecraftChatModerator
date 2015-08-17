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

package eu.carrade.amaury.MinecraftChatModerator.listeners;

import eu.carrade.amaury.MinecraftChatModerator.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;


public class ChatListener implements Listener
{

	@EventHandler(priority = EventPriority.HIGHEST) // MONITOR unavailable because we may have to cancel the event.
	public void onAsyncPlayerChat(AsyncPlayerChatEvent ev)
	{
		MinecraftChatModerator.get().getMessagesManager().savePlayerMessage(ev.getPlayer().getUniqueId(), ev.getMessage());

		// TODO Filters and analyses

		MinecraftChatModerator.get().getMessagesManager().getChatHistory(ev.getPlayer().getUniqueId()).cleanup();
	}

}

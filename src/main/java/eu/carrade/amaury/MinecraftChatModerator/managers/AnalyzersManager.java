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

package eu.carrade.amaury.MinecraftChatModerator.managers;

import eu.carrade.amaury.MinecraftChatModerator.analyzers.*;
import eu.carrade.amaury.MinecraftChatModerator.rawtypes.*;

import java.util.*;
import java.util.concurrent.*;


public class AnalyzersManager
{
	private final Set<ChatAnalyzer> analyzers = new CopyOnWriteArraySet<>();

	public AnalyzersManager()
	{
		// TODO register analyzers
	}


	/**
	 * Registers a new analyzer in the system.
	 *
	 * @param analyzer The analyzer.
	 */
	public void registerAnalyzer(ChatAnalyzer analyzer)
	{
		analyzers.add(analyzer);
	}


	/**
	 * Runs all the registered analyzers on the given player history.
	 *
	 * @param history The player's chat history.
	 */
	public void runAnalyzes(PlayerChatHistory history)
	{
		analyzers.stream().forEach(analyzer -> analyzer.analyze(history));
	}
}

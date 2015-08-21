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

package eu.carrade.amaury.MinecraftChatModerator.utils;

import eu.carrade.amaury.MinecraftChatModerator.MinecraftChatModerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;


public final class AsyncMessageSender
{
	private static boolean enabled = true;

	private static String nmsVersion;

	private static Class<?> craftPlayerClass;
	private static Class<?> packetPlayOutChatClass;
	private static Class<?> packetClass;
	private static Class<?> chatSerializerClass;
	private static Class<?> iChatBaseComponentClass;
	private static Class<?> chatComponentTextClass;


	private AsyncMessageSender() {}


	static
	{
		nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
		nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

		try
		{
			iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
			packetPlayOutChatClass  = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
			craftPlayerClass        = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
			packetClass             = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");

			if (nmsVersion.equalsIgnoreCase("v1_8_R1") || !nmsVersion.startsWith("v1_8_"))
			{
				chatSerializerClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatSerializer");
			}
			else
			{
				chatComponentTextClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatComponentText");
			}
		}
		catch (Exception e)
		{
			enabled = false;
		}
	}

	/**
	 * Sends the messages to the given recipient.
	 *
	 * @param receiver    The receiver of the messages.
	 * @param chatMessage {@code true} if this messages should be sent as chat messages.
	 *                    These messages are hidden when the player disables the chat in
	 *                    the chat settings (« Commands only » mode), unlike other ones.
	 * @param messages    The messages to send to the receiver.
	 */
	public static void sendMessage(UUID receiver, Boolean chatMessage, String... messages)
	{
		Bukkit.getScheduler().runTask(MinecraftChatModerator.get(), () -> {
			Player player = Bukkit.getPlayer(receiver);

			if (player != null && player.isOnline())
			{
				for (String message : messages)
				{
					sendMessage(player, chatMessage, message);
				}
			}
		});
	}

	/**
	 * Sends a message to the given recipient.
	 * <p/>
	 * This method needs to be called in the main Bukkit thread!
	 *
	 * @param receiver    The receiver of the message.
	 * @param chatMessage {@code true} if this messages should be sent as chat messages.
	 *                    These messages are hidden when the player disables the chat in
	 *                    the chat settings (« Commands only » mode), unlike other ones.
	 * @param message     The message to send to the receiver.
	 */
	private static void sendMessageSync(UUID receiver, Boolean chatMessage, String message)
	{
		sendMessage(Bukkit.getPlayer(receiver), chatMessage, message);
	}

	/**
	 * Sends a message to the given recipient.
	 *
	 * @param receiver    The receiver of the message.
	 * @param chatMessage {@code true} if this messages should be sent as chat messages.
	 *                    These messages are hidden when the player disables the chat in
	 *                    the chat settings (« Commands only » mode), unlike other ones.
	 * @param message     The message to send to the receiver.
	 */
	public static void sendMessage(Player receiver, Boolean chatMessage, String message)
	{
		if (receiver == null || message == null) return;

		// Fallback to Player.sendMessage() if unavailable.
		if (!chatMessage || !enabled)
		{
			receiver.sendMessage(message);
			return;
		}

		byte chatChannel = (byte) 0; // 0: chat; 1: system message; 2: action bar.
		int maxMessageLength = 32767;

		try
		{
			Object craftPlayer = craftPlayerClass.cast(receiver);
			Object chatPacket;

			if (nmsVersion.equalsIgnoreCase("v1_8_R1") || !nmsVersion.startsWith("v1_8_"))
			{
				if(message.length() > maxMessageLength - 12)
				{
					message = message.substring(0, maxMessageLength - 12);
				}

				Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
				Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
				chatPacket = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, chatChannel);
			}
			else
			{
				if(message.length() > maxMessageLength)
				{
					message = message.substring(0, maxMessageLength);
				}

				Object o = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
				chatPacket = packetPlayOutChatClass.getConstructor(new Class<?>[]{chatComponentTextClass, byte.class}).newInstance(o, chatChannel);
			}

			Object handle = craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayer);
			Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);

			playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass).invoke(playerConnection, chatPacket);
		}
		catch (Exception e)
		{
			MinecraftChatModerator.get().getLogger().log(Level.SEVERE, "Cannot send chat message '" + message + "' to " + receiver + ".", e);
		}
	}

	/**
	 * Sends chat messages to the given recipient.
	 * <p/>
	 * Messages of this kind are hidden when the player disables the chat in the chat
	 * settings (« Commands only » mode), unlike other ones.
	 *
	 * @param receiver The receiver of the message.
	 * @param messages The messages to send to the receiver.
	 */
	public static void sendChatMessage(UUID receiver, String... messages)
	{
		sendMessage(receiver, true, messages);
	}

	/**
	 * Sends an error message to the given player.
	 * <p/>
	 * This method is a shortcut to send messages with:
	 * <ul>
	 * <li>a blank line before;</li>
	 * <li>each message of the list prefixed by a dark-gray french opening quotation mark (“» ”);</li>
	 * <li>a blank line after.</li>
	 * </ul>
	 *
	 * @param receiver The message's receiver.
	 * @param messages The messages to send.
	 */
	public static void sendErrorMessage(UUID receiver, String... messages)
	{
		String[] errorMessages = new String[messages.length + 2];

		errorMessages[0] = "";
		errorMessages[errorMessages.length - 1] = "";

		for (int i = 0; i < messages.length; i++)
		{
			errorMessages[i + 1] = ChatColor.DARK_GRAY + "» " + ChatColor.RESET + messages[i];
		}

		sendMessage(receiver, false, errorMessages);
	}
}

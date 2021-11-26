package net.irewiewer.lastlife;

import java.awt.Color;


import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Test implements CommandExecutor
{
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			DiscordWebhook webhook = new DiscordWebhook("token");

		    webhook.addEmbed(new DiscordWebhook.EmbedObject()
		            .addField("Last Life Standings", "iRewiewer - 6 Lives\\nFirealex2 - 6 Lives\\nLydutech - 6 Lives", true)
		            .setColor(Color.RED));

		    try
		    {
				webhook.execute();
			}
		    catch (IOException e)
		    {
				e.printStackTrace();
			}
		}		

		return true;
	}
}
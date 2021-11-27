package net.irewiewer.lastlife;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class StartLives implements CommandExecutor
{
	private final Main plugin;
	
	public StartLives(final Main plugin) { this.plugin = plugin; }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		// If the command hasn't been executed before
		if(plugin.getISL() == false)
		{
			Participants players = new Participants();
			
			// For all the players online
			// Create a 'Participant' object for each one of them
			// with the respective attributes
			// and add that participant to the 'Participants' list

			for(Player player : Bukkit.getOnlinePlayers())
			{
				Participant part = new Participant();

				part.setName(player.getDisplayName());
				part.setLives((int)((Math.random() * (2 - 7)) + 7));
				part.setBoogie(false);
				
				players.addPart(part);
			}

			// Write changes to file
			try { Main.writeFile(players); } catch (IOException error) { error.printStackTrace(); }
			
			// Update config with changes
			plugin.updateConfig(plugin.getETC(), true, plugin.getTI());
			
			// Inform either the player or the console that the command has been issued successfully
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				player.sendMessage(ChatColor.GRAY + "The command has been issued successfully.");
			}
			else if (sender instanceof ConsoleCommandSender)
			{
				System.out.println("The command has been issued successfully.");
			}
		}
		else
		{
			// Inform either the player or the console that the command has been issued already
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				player.sendMessage(ChatColor.GRAY + "This command has been issued already.");
			}
			else if (sender instanceof ConsoleCommandSender)
			{
				System.out.println("The command has been issued already.");
			}
		}
		
		// Refresh the 'lives' variable with the updated content of the file
		// as it is used throughout the plugin's instance
		try { plugin.lives = Main.readFile(); } catch (FileNotFoundException error) { error.printStackTrace(); }
		
		// Update the config
		plugin.updateConfig(plugin.getETC(), plugin.getISL(), true);
		
		// Update the colors of every online player
		plugin.updateColoredNames();
		
		return true;
	}
}
package net.irewiewer.lastlife;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileWriter;

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
		if(plugin.funcISL() == false)
		{
			Participants players = new Participants();
			
			for(Player player : Bukkit.getOnlinePlayers())
			{
				Participant part = new Participant();

				part.setName(player.getDisplayName());
				part.setLives((int)((Math.random() * (2 - 7)) + 7));
				part.setBoogie(false);
				
				players.addPart(part);
			}

			// Initialise file with n lives / player
			try(FileWriter fileWriter = new FileWriter(System.getProperty("user.dir") + "\\LastLife\\lives.json"))
			{
				Gson gson = new GsonBuilder().setPrettyPrinting().create();

				fileWriter.write(gson.toJson(players));
				fileWriter.close();
			}
			catch (Exception error)	{ System.out.println(error); }


			plugin.updateConfig(plugin.funcETC(), true);
			
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
		
		try { plugin.lives = Main.readFile(); } catch (FileNotFoundException error) { error.printStackTrace(); }
		
		return true;
	}
}
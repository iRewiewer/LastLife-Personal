package net.irewiewer.lastlife;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Setlives implements CommandExecutor
{
	private final Main plugin;
	
	public Setlives(final Main plugin) { this.plugin = plugin; }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		List<Participant> updatedParticipants = new ArrayList<>();

		if(args.length > 1)
		{
			for(Participant participant : plugin.lives.getParts())
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					if(args[0].equals(player.getDisplayName()) && player.getDisplayName().equals(participant.getName()))
					{
						try{ participant.setLives(Integer.parseInt(args[1])); } catch (Exception error) { System.out.println(error); }

						if(sender instanceof Player) ((Player)sender).sendMessage(ChatColor.GRAY + "Set lives of player " + ChatColor.LIGHT_PURPLE + participant.getName() + ChatColor.GRAY + " to " + ChatColor.LIGHT_PURPLE + Integer.parseInt(args[1]) + ChatColor.GRAY + ".");
			            else if (sender instanceof ConsoleCommandSender) System.out.println("Set lives of player " + participant.getName() + " to " + Integer.parseInt(args[1]) + ".");
					}
				}
				
				updatedParticipants.add(participant);
			}
		}
		else
		{
			if(sender instanceof Player) ((Player)sender).sendMessage(ChatColor.GRAY + "You must specify a player and a count.");
            else if (sender instanceof ConsoleCommandSender) System.out.println("You must specify a player and a count.");

            updatedParticipants = plugin.lives.getParts();
		}
		
		plugin.lives.setParts(updatedParticipants);
		
		try { Main.writeFile(plugin.lives); } catch (IOException error) { error.printStackTrace(); }

		// Update everybody's colored names
		plugin.updateColoredNames();
		
		return true;
	}
}

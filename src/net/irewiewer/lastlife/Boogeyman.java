package net.irewiewer.lastlife;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Boogeyman implements CommandExecutor
{
	private final Main plugin;
	
	public Boogeyman(final Main plugin) { this.plugin = plugin; }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		List<Participant> updatedParticipants = new ArrayList<>();
		int boogiesNumber = (int)(Math.random() * 2) + 1;

		for(Participant participant : plugin.lives.getParts())
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				if(player.getDisplayName().equals(participant.getName()))
				{
					if(participant.getBoogie() == false && boogiesNumber > 0 && participant.getLives() > 1)
					{
						player.sendMessage(ChatColor.RED + "You're the boogeyman!");
						participant.setBoogie(true);
						boogiesNumber -= 1;
					}
					else
					{
						player.sendMessage(ChatColor.GREEN + "You're not the boogeyman!");
					}
				}
			}

			updatedParticipants.add(participant);
		}
		
		plugin.lives.setParts(updatedParticipants);
		
		try { Main.writeFile(plugin.lives); } catch (IOException error) { error.printStackTrace(); }
        
		plugin.updateColoredNames();

		return true;
	}
}
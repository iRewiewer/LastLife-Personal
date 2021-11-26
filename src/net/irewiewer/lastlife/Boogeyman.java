package net.irewiewer.lastlife;

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
		Participants p = plugin.lives;
		Participant b = null;
		int boogyeNo = 0;

		for(Participant participant : p.getParts())
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{	
				if((int)(Math.random() * 2) == 1 && participant.getBoogie() == false && boogyeNo <= 2)
				{
					if(player.getDisplayName().equals(participant.getName()))
					{
						player.sendMessage(ChatColor.RED + "You're the boogeyman!");
						participant.setBoogie(true);
						boogyeNo += 1;
					}
				}
				else player.sendMessage(ChatColor.GREEN + "You're not the boogeyman!");
			}
		}
		
		plugin.boogie = b;
		
		return true;
	}
}
package net.irewiewer.lastlife;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GetToken implements CommandExecutor
{
	private final Main plugin;
	
	public GetToken(final Main plugin) { this.plugin = plugin; }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(args.length > 0)
		{
			plugin.setToken(args[0]);
			
			if(sender instanceof Player) ((Player)sender).sendMessage(ChatColor.GREEN + "Token has been set successfully!");
            else if (sender instanceof ConsoleCommandSender) System.out.println(ChatColor.GREEN + "Token has been set successfully!");
		}
		else
		{
			if(sender instanceof Player) ((Player)sender).sendMessage(ChatColor.GRAY + "You need to provide a token.");
            else if (sender instanceof ConsoleCommandSender) System.out.println(ChatColor.LIGHT_PURPLE + "You need to provide a token.");
		}
		
		return true;
	}
}

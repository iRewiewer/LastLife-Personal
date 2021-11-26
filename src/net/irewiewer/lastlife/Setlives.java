package net.irewiewer.lastlife;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("unused")
public class Setlives implements CommandExecutor
{
	private final Main plugin;
	public Setlives(final Main plugin) { this.plugin = plugin; }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		
		
		return true;
	}
}

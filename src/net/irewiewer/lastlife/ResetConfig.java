package net.irewiewer.lastlife;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ResetConfig implements CommandExecutor
{
	private final Main plugin;
	
	public ResetConfig(final Main plugin) { this.plugin = plugin; }

	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		// Command for resetting the config from in game
		// using the already existing function in Main
		plugin.resetConfig();
		
		if(sender instanceof Player) { sender.sendMessage(ChatColor.GRAY + "You've successfully reset the config."); }
		else if (sender instanceof ConsoleCommandSender) { System.out.println("You've successfully reset the config."); }	
		
		return true;
	}
}

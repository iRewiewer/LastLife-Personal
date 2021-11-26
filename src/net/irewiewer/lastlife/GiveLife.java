package net.irewiewer.lastlife;

import org.json.simple.JSONObject;

import net.md_5.bungee.api.chat.hover.content.Item;
import net.minecraft.commands.arguments.ArgumentInventorySlot;

import java.io.Console;
import java.io.FileNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_17_R1.command.ConsoleCommandCompleter;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class GiveLife implements CommandExecutor
{
	private final Main plugin;
	
	public GiveLife(final Main pl) { this.plugin= pl; }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Participants p = plugin.lives;
		boolean displayedMessage = false;
	
		Player player = null, targetPlayer = null;
		
		if(sender instanceof Player)
		{
			player = (Player)sender;

			if(args.length > 0)
			{
				if(Bukkit.getServer().getPlayer(args[0]) != null)
				{
					targetPlayer = Bukkit.getPlayer(args[0]);
				}
			}
			else { displayedMessage = true; player.sendMessage(ChatColor.GRAY + "You must specify a player."); }
		}
		else if (sender instanceof ConsoleCommandSender)
		{
			if(args.length > 0)
			{
				if(Bukkit.getServer().getPlayer(args[0]) != null)
				{
					targetPlayer = Bukkit.getPlayer(args[0]);
				}
			}
			else { displayedMessage = true; System.out.println("You must specify a player."); }
		}

		boolean gaveLife = false;
		
		if(targetPlayer != null)
		{
			if(targetPlayer == player) player.sendMessage(ChatColor.GRAY + "You gave one of your lives... to yourself! Amazing...");
			else
			{
				for(Participant participant : p.getParts())
				{
					if(sender instanceof ConsoleCommandSender)
						if(participant.getName().equals(targetPlayer.getDisplayName())) participant.setLives(participant.getLives() + 1);
					else if(sender instanceof Player)
					{
						if(participant.getName().equals(player.getDisplayName()) && participant.getLives() > 1)
						{
							participant.setLives(participant.getLives() - 1);
							gaveLife = true;
						}
						
						if(participant.getName().equals(targetPlayer.getDisplayName()))
						{
							participant.setLives(participant.getLives() + 1);
						}
					}
				}
				
				for(Participant participant : p.getParts())
				{
					if(participant.getName().equals(targetPlayer.getDisplayName()) && gaveLife == false)
					{
						participant.setLives(participant.getLives() - 1);
					}
				}
				
				if(gaveLife == true)
				{
					player.sendMessage(ChatColor.GREEN + "You gave one of your lives to " + ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GREEN + "!");
					targetPlayer.sendMessage(ChatColor.GREEN + "You've gained a life from " + ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GREEN + "!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You don't have enough lives to do that!");
					player.sendMessage(ChatColor.GRAY + "Unless... you want to get eliminated early?");
				}
			}
		}

		return true;
	}
}
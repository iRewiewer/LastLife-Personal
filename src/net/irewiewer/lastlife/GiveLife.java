package net.irewiewer.lastlife;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.org.apache.http.impl.execchain.MainClientExec;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class GiveLife implements CommandExecutor
{
	private final Main plugin;
	
	public GiveLife(final Main pl) { this.plugin = pl; }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		boolean displayedMessage = false;
	
		Player player, targetPlayer = null;

		List<Participant> updatedParticipants = new ArrayList<>(), tmp = new ArrayList<>();

		if(sender instanceof Player) player = (Player) sender;
		else player = null;
		
        if(args.length > 0)
        {
            if(Bukkit.getServer().getPlayer(args[0]) != null)
            {
                targetPlayer = Bukkit.getPlayer(args[0]);
                
                if(player == null)
                {
                	for(Participant participant : plugin.lives.getParts())
                    {
                        if(participant.getName().equals(targetPlayer.getDisplayName()))
                        	participant.setLives(participant.getLives() + 1);

                        targetPlayer.sendMessage(ChatColor.GREEN + "You've gained a life from " + ChatColor.LIGHT_PURPLE + "[Server]" + ChatColor.GREEN + "!");

                        updatedParticipants.add(participant);
                    }
                }
                else if(targetPlayer == player)
	                {
	                	player.sendMessage(ChatColor.GRAY + "You gave one of your lives... to yourself! Amazing...");
	                	updatedParticipants = plugin.lives.getParts();
	                }
	                else
	                {
	                	boolean gaveLife = false;

	                	for(Participant participant : plugin.lives.getParts())
	                    {
		                    if(participant.getName().equals(player.getDisplayName()))
		                    {
		                    	if(participant.getLives() > 1)
		                    	{	
			                    	participant.setLives(participant.getLives() - 1);
			                    	player.sendMessage(ChatColor.GREEN + "You gave one of your lives to " + ChatColor.LIGHT_PURPLE + targetPlayer.getDisplayName() + ChatColor.GREEN + "!");
			                    	gaveLife = true;
		                    	}
		                    	else
		                    	{
		                    		if(participant.getLives() == 1)
		                    		{
			                    		player.sendMessage(ChatColor.RED + "You don't have enough lives to do that!");
			                            player.sendMessage(ChatColor.GRAY + "Unless... you want to get eliminated early?");
		                    		}
		                    		else
		                    		{
		                    			player.sendMessage(ChatColor.GRAY + "You've already been eliminated, you can't give lives anymore.");
									}
								}
		                    	
		                    	tmp.add(participant);
		                    }
		                    else
		                    	if(participant.getName().equals(targetPlayer.getDisplayName())) tmp.add(participant);
			                    else updatedParticipants.add(participant);
	                    }

	                	for(Participant participant : tmp)
	                    {
		                    if(participant.getName().equals(targetPlayer.getDisplayName()) && gaveLife == true)
		                    {
		                    	participant.setLives(participant.getLives() + 1);
		                    	targetPlayer.sendMessage(ChatColor.GREEN + "You've gained a life from " + ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GREEN + "!");
		                    }
		                    
		                	updatedParticipants.add(participant);
	                    }
	                }
            }
            else
            {
                if(sender instanceof Player) ((Player)sender).sendMessage(ChatColor.GRAY + "That player name is not valid. Perhaps they are offline?");
                else if (sender instanceof ConsoleCommandSender) System.out.println("That player name is not valid. Perhaps they are offline?");

                updatedParticipants = plugin.lives.getParts();
            }
        }
        else
        {
            if(sender instanceof Player) ((Player)sender).sendMessage(ChatColor.GRAY + "You must specify a player.");
            else if (sender instanceof ConsoleCommandSender) System.out.println("You must specify a player.");

            updatedParticipants = plugin.lives.getParts();
        }

        plugin.lives.setParts(updatedParticipants);
        
        try { Main.writeFile(plugin.lives); } catch (IOException error) { error.printStackTrace(); }
        
		plugin.updateColoredNames();
        
		return true;
	}
}
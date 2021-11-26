package net.irewiewer.lastlife;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener
{
	private final Main plugin;
	
	public DeathListener(final Main pl) { this.plugin = pl; }

	@EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
		Participants p = plugin.lives;
		
		if(event.getEntity() instanceof Player)
		{
			for(Participant participant : p.getParts())
			{
				if(participant.getLives() >= 1 && participant.getName().equals(((Player) event.getEntity()).getDisplayName()))
				{
					participant.setLives(participant.getLives() - 1);
					
					if(participant.getLives() == 0)
					{
						event.getEntity().setGameMode(GameMode.SPECTATOR);
						
						Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "Player " +
															ChatColor.LIGHT_PURPLE + participant.getName() +
															ChatColor.YELLOW + " has been " +
															ChatColor.RED + ChatColor.BOLD +  " eliminated" +
															ChatColor.RESET + ChatColor.YELLOW + "!");
					}
				}
			}
		}
		
		try { Main.writeFile(p); } catch (IOException error) { error.printStackTrace(); }
    }
}
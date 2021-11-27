package net.irewiewer.lastlife;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.awt.Color;


public class DeathListener implements Listener
{
	private final Main plugin;
	
	public DeathListener(final Main pl) { this.plugin = pl; }

	@EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
		List<Participant> updatedParticipants = new ArrayList<>();
		
		if(event.getEntity() instanceof Player)
		{
			for(Participant participant : plugin.lives.getParts())
			{
				if(participant.getName().equals(((Player) event.getEntity()).getDisplayName()))
				{
					participant.setLives(participant.getLives() - 1);
						
					if(participant.getLives() <= 0)
					{
						event.getEntity().setGameMode(GameMode.SPECTATOR);
						Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "Player " +
															ChatColor.LIGHT_PURPLE + participant.getName() +
															ChatColor.YELLOW + " has been" +
															ChatColor.RED + ChatColor.BOLD +  " eliminated" +
															ChatColor.RESET + ChatColor.YELLOW + "!");
						
						DiscordWebhook webhook = new DiscordWebhook(plugin.getToken());

						webhook.addEmbed(new DiscordWebhook.EmbedObject()
							    .setDescription("Player **" + participant.getName() + "** has been **eliminated**!")
					            .setColor(Color.RED));

					    try { webhook.execute(); } catch (IOException error) { error.printStackTrace(); }
					}
					else
					{
						DiscordWebhook webhook = new DiscordWebhook(plugin.getToken());

						webhook.addEmbed(new DiscordWebhook.EmbedObject()
							    .setDescription(event.getDeathMessage())
					            .setColor(Color.RED));

					    try { webhook.execute(); } catch (IOException error) { error.printStackTrace(); }
					}
				}

				updatedParticipants.add(participant);
			}
		}
		
		plugin.lives.setParts(updatedParticipants);
		
		try { Main.writeFile(plugin.lives); } catch (IOException error) { error.printStackTrace(); }
		
		plugin.updateColoredNames();
    }
}
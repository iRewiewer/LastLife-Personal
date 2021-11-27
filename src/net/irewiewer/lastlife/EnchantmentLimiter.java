package net.irewiewer.lastlife;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentLimiter implements Listener
{
	private final Main plugin;
	
	public EnchantmentLimiter(final Main pl) { this.plugin= pl; }

	// Event fires when the player tries to craft an item
	@EventHandler
    public void onPlayerCraft(CraftItemEvent event)
    {
		// If the player's crafting an enchanting table 
		if(event.getCurrentItem().getType() == Material.ENCHANTING_TABLE)
		{
			// But it has been crafted before
			if (plugin.getETC() == true)
	        {
				// Cancel the crafting event and inform the player
	            event.setCancelled(true);
	            event.getWhoClicked().sendMessage(ChatColor.RED + "An enchantment table has already been crafted!");
	        }
	    	else
	    	{
	    		// Cancelling event so that the player can't
	    		// somehow glitch in multiple enchanting tables
    			event.setCancelled(true);
    			
    			ItemStack enchTable = prettyEnchantingTable();

				HumanEntity human = event.getWhoClicked();
				
				// Add the pretty enchanting table to the player's inventory and update it
				// Close the inventory so the materials in the crafting grid go back to the inventory
				// Remove the materials cost for the enchanting table
				if(human instanceof Player)
				{
					((Player) human).getInventory().addItem(enchTable);
					((Player) human).updateInventory();
					((Player) human).closeInventory();
					((Player) human).getInventory().removeItem(new ItemStack(Material.BOOK, 1));
					((Player) human).getInventory().removeItem(new ItemStack(Material.DIAMOND, 2));
					((Player) human).getInventory().removeItem(new ItemStack(Material.OBSIDIAN, 4));
				}
				
				// Inform all players online
    			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "The " + ChatColor.LIGHT_PURPLE + "enchantment table" + ChatColor.GOLD + " has been crafted in " + ChatColor.YELLOW + "a faraway land" + ChatColor.GOLD + "!");

    			// Update the config
    			plugin.updateConfig(true, plugin.getISL(), plugin.getTI());
	    	}
		}
    }
	
	// Event fires when the player *tries* to pickup an item
	@EventHandler
	public void onPickup(EntityPickupItemEvent event)
	{
		// If the item is an enchanting table and the entity trying to pick it up is a player
		// Cancel the event so the item doesn't get duped
		// Add a pretty enchanting table to the player's inventory
		if(event.getEntity() instanceof Player && event.getItem().getItemStack().getType() == Material.ENCHANTING_TABLE)
		{
			ItemStack enchTable = prettyEnchantingTable();
			
			event.setCancelled(true);
			event.getItem().remove();
			((Player) event.getEntity()).getInventory().addItem(enchTable);
			((Player) event.getEntity()).updateInventory();
		}
	}
	
	public ItemStack prettyEnchantingTable()
	{
		// Define 1 enchanting table with unbreaking 10
		ItemStack enchTable = new ItemStack(Material.ENCHANTING_TABLE);
		enchTable.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		enchTable.setAmount(1);
		
		// The lore "One of a kind" and a pink name
		ItemMeta meta = enchTable.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_PURPLE + "One of a kind");
		meta.setLore(lore);
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Enchantment Table");
		enchTable.setItemMeta(meta);
		
		// Return the defined item
		return enchTable;
	}
	
}
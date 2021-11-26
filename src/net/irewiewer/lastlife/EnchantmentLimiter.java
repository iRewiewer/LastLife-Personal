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

	@EventHandler
    public void onPlayerCraft(CraftItemEvent event)
    {
		if (event.getCurrentItem().getType() == Material.ENCHANTING_TABLE && plugin.funcETC() == true)
        {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.RED + "An enchantment table has already been crafted!");
        }
    	else
    	{
    		if(event.getCurrentItem().getType() == Material.ENCHANTING_TABLE && plugin.funcETC() == false)
    		{
    			event.setCancelled(true);

    			
    			ItemStack enchTable = prettyEnchantmentTable();

				HumanEntity human = event.getWhoClicked();
				
				if(human instanceof Player)
				{
					((Player) human).getInventory().addItem(enchTable);
					((Player) human).updateInventory();
					((Player) human).closeInventory();
					((Player) human).getInventory().removeItem(new ItemStack(Material.BOOK, 1));
					((Player) human).getInventory().removeItem(new ItemStack(Material.DIAMOND, 2));
					((Player) human).getInventory().removeItem(new ItemStack(Material.OBSIDIAN, 4));
				}
				
    			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "The " + ChatColor.LIGHT_PURPLE + "enchantment table" + ChatColor.GOLD + " has been crafted in " + ChatColor.YELLOW + "a faraway land" + ChatColor.GOLD + "!");

    			plugin.updateConfig(true, plugin.funcISL());
    		}
    	}
    }
	
	@EventHandler
	public void onPickup(EntityPickupItemEvent event)
	{
		if(event.getEntity() instanceof Player && event.getItem().getItemStack().getType() == Material.ENCHANTING_TABLE)
		{
			ItemStack enchTable = prettyEnchantmentTable();
			
			event.setCancelled(true);
			event.getItem().remove();
			((Player) event.getEntity()).getInventory().addItem(enchTable);
			((Player) event.getEntity()).updateInventory();
		}
	}
	
	public ItemStack prettyEnchantmentTable()
	{
		ItemStack enchTable = new ItemStack(Material.ENCHANTING_TABLE);
		enchTable.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		enchTable.setAmount(1);
		
		ItemMeta meta = enchTable.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_PURPLE + "One of a kind");
		meta.setLore(lore);
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Enchantment Table");
		enchTable.setItemMeta(meta);
		
		return enchTable;
	}
	
}
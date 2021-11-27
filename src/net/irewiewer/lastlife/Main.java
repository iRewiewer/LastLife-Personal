package net.irewiewer.lastlife;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

public class Main extends JavaPlugin implements Listener
{
	private static Main instance;
	
	public static Path path = Paths.get(System.getProperty("user.dir") + "\\LastLife");
	public String discordToken = null;

	// Config variables
	private boolean enchantingTableCrafted;
	private boolean issuedStartLives;
	private boolean teamsInitialized;

	public FileConfiguration config;

	// Persistent session variables - read onEnable(), written onDisable()
	public Participants lives;
	
	public static Main getInstance()
	{
		return instance;
	}

	// To-do:
	// [x] Implement boogey chooser (/boogey)
	// [x] Implement give life (/givelife)
	// [x] Implement death reduces life event
	// [x] Implement start lives (/startlives)
	// [x] Implement enchantment table limiter
	// [x] Implement resetconfig command in case of unforseen events (/resetconfig)
	// [x] Implement config file
	// [ ] (OPTIONAL) Do boogey chooser animation
	// [ ] (OPTIONAL) Do give life animation
	// [ ] (OPTIONAL) Do start lives animation
	// [x] (OPTIONAL) Implement discord announcer of players' deaths (automatic event)
	// [ ] (OPTIONAL) Implement permissions system so only OPs can /resetconfig, /boogey, /startlives
	//
	// For animations, see reference at https://www.youtube.com/watch?v=WYXVypbVVOs

	@Override
	public void onEnable()
	{
		instance = this;

		setupStartLives();
		
		getConfigVars();
		saveDefaultConfig();
	
		config = getConfig();

		// Boogey command selects 1-2 random people as boogeymen
		getCommand("boogey").setExecutor(new Boogeyman(this));

		// Give one of your lives to another player
		getCommand("givelife").setExecutor(new GiveLife(this));

		// Initiate the lives of every online player
		getCommand("startlives").setExecutor(new StartLives(this));
		
		// Reset command - Resets config vars
		getCommand("resetconfig").setExecutor(new ResetConfig(this));
		
		// Setlive command - Set lives of 'player' to 'amount'
		getCommand("setlives").setExecutor(new Setlives(this));
		
		// Token command - Set the token of the discord webhook
		getCommand("token").setExecutor(new GetToken(this));

		// Event Listener - Used for enchantment table crafting limitation
		getServer().getPluginManager().registerEvents(new EnchantmentLimiter(this), this);

		// Event Listener - Used for listening to Player Death event
		getServer().getPluginManager().registerEvents(new DeathListener(this), this);

		// If startlives has been issued, read lives file
		if(getISL() == true) { try { lives = readFile(); } catch (IOException error) { error.printStackTrace(); } }
		
		// If plugin has been ran before, read lives file
		if(getTI() == false) { initializeTeams(); }
		
		getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Don't forget to provide the discord webhook token using '/token'.");
		getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Don't forget to provide the discord webhook token using '/token'.");
		getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Don't forget to provide the discord webhook token using '/token'.");
		
		Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "Don't forget to provide the discord webhook token using '/token'.");
		Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "Don't forget to provide the discord webhook token using '/token'.");
		Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "Don't forget to provide the discord webhook token using '/token'.");
	}
	
	@Override
	public void onDisable()
	{
		saveConfig();

		// If startlives has been issued, read lives file
		if(getISL() == true) { try { writeFile(lives); } catch (IOException error) { error.printStackTrace(); } }
		
		instance = null;
	}

//////////////////////////////////////////////////////////////
//
//  Scoreboard Related
//
//  Colorcode players with 'x' amount of lives
//  
//  Dark Green - 4+
//  Green - 3
//  Yellow - 2
//  Red - 1
//  
	public void initializeTeams()
	{
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		
		try
		{
			// Initialize teams
			scoreboard.registerNewTeam("dark_green").setColor(ChatColor.DARK_GREEN);
			scoreboard.registerNewTeam("green").setColor(ChatColor.GREEN);
			scoreboard.registerNewTeam("yellow").setColor(ChatColor.YELLOW);
			scoreboard.registerNewTeam("red").setColor(ChatColor.RED);
			scoreboard.registerNewTeam("dead").setColor(ChatColor.DARK_GRAY);
		}
		catch (Exception error) { unregisterTeams(scoreboard); }

		// Update the config
		updateConfig(getETC(), getISL(), true);
	}
	
	public void unregisterTeams(Scoreboard scoreboard)
	{
		// Unregistering seems totally easier than kicking all players from every team
		scoreboard.getTeam("dark_green").unregister();
		scoreboard.getTeam("green").unregister();
		scoreboard.getTeam("yellow").unregister();
		scoreboard.getTeam("red").unregister();
		scoreboard.getTeam("dead").unregister();
	}
	
	public void updateColoredNames()
	{
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	
		try { unregisterTeams(scoreboard); } catch (Exception error) { }
		finally
		{
			scoreboard.registerNewTeam("dark_green").setColor(ChatColor.DARK_GREEN);
			scoreboard.registerNewTeam("green").setColor(ChatColor.GREEN);
			scoreboard.registerNewTeam("yellow").setColor(ChatColor.YELLOW);
			scoreboard.registerNewTeam("red").setColor(ChatColor.RED);
			scoreboard.registerNewTeam("dead").setColor(ChatColor.DARK_GRAY);
			
			// Add players to their corresponding team based on the number of lives they have
			for(Player player : Bukkit.getOnlinePlayers())
			{
				for(Participant participant : lives.getParts())
				{
					if(player.getDisplayName().equals(participant.getName()))
					{
						if(participant.getLives() <= 0) scoreboard.getTeam("dead").addEntry(player.getDisplayName());
						else if(participant.getLives() == 1) scoreboard.getTeam("red").addEntry(player.getDisplayName());
						else if(participant.getLives() == 2) scoreboard.getTeam("yellow").addEntry(player.getDisplayName());
						else if(participant.getLives() == 3) scoreboard.getTeam("green").addEntry(player.getDisplayName());
						else if(participant.getLives() >= 4) scoreboard.getTeam("dark_green").addEntry(player.getDisplayName());
					}
				}
			}
		}
	}

//////////////////////////////////////////////////////////////
//
//  Lives File Related
//
//  Setup for first run of plugin - create LastLife folder & lives.json file
//  Read ; Write methods for interacting with the file during runtime
//
	
	public void setupStartLives()
	{
		//Make LastLives folder if it doesn't exist
		try { Files.createDirectories(path); }
		catch (Exception error)	{ System.out.println(error); }

		// Make lives.json file if it doesn't exist
		try
		{
			// lives.json file
			File file = new File(path + "\\lives.json");
			
			if (file.createNewFile())
			{
				try(FileWriter fileWriter = new FileWriter(file)) { fileWriter.close(); }
				catch (Exception error)	{ System.out.println(error); }
			}
		}
		catch (Exception error)	{ System.out.println(error); }
	}
	
	// Read a Participants object from the lives.json file
	public static Participants readFile() throws FileNotFoundException
	{
		GsonBuilder builder = new GsonBuilder(); 
		Gson gson = builder.create(); 
		BufferedReader bufferedReader = new BufferedReader(new FileReader(path + "\\lives.json"));
		Participants playersJson = gson.fromJson(bufferedReader, Participants.class);
		
		return playersJson;
	}
	
	// Write a Participants object to the lives.json file
	public static void writeFile(Participants participants) throws IOException
	{
		// Pretty printing is an invaluable asset
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		FileWriter writer = new FileWriter(path + "\\lives.json");
		writer.write(gson.toJson(participants));
		writer.close();
	}

//////////////////////////////////////////////////////////////
//
//  Config Related
//
//  Get methods for getting the status of the currently saved config variables
//  Reset function which resets the config to its defaults
//  Update function which updates the config to its provided parameters
//  Also token get ; set functions which are not tied to the config itself
//
	
	// Get method for currently saved config variables
	public void getConfigVars()
	{
		enchantingTableCrafted = getConfig().getBoolean("enchantingTableCrafted");
		issuedStartLives = getConfig().getBoolean("issuedStartLives");
		teamsInitialized = getConfig().getBoolean("teamsInitialized");
	}
	
	// EnchantingTableCrafted - if an enchanting table has already been crafted
	public boolean getETC() { return enchantingTableCrafted; }
	
	// IssuedStartLives - if /startlives has been issued already
	public boolean getISL() { return issuedStartLives; }
	
	// teamsInitialized - if plugin has ran before
	public boolean getTI() { return teamsInitialized; }

	// Getter ; Setter for the discord webhook token
	public String getToken() { return discordToken; }
	public void setToken(String token) { discordToken = token; }
	
	public void resetConfig()
	{
		FileConfiguration config = getConfig();
		config.set("enchantingTableCrafted", false);
		config.set("issuedStartLives", false);
		config.set("teamsInitialized", false);
		
		try { unregisterTeams(Bukkit.getScoreboardManager().getMainScoreboard()); } catch (Exception error) { }
		try { updateColoredNames(); } catch (Exception error) { System.out.println(error); }
		
		getConfigVars();
		saveConfig();
		saveDefaultConfig();
		getConfigVars();
	}

	public void updateConfig(boolean eTC, boolean iSL, boolean tI)
	{
		config.set("enchantingTableCrafted", eTC);
		config.set("issuedStartLives", iSL);
		config.set("teamsInitialized", tI);
		saveConfig();
		saveDefaultConfig();
		getConfigVars();
	}
}

//////////////////////////////////////////////////////////////
//                
//  Helper Classes
//
//  Participant - Player class that contains the name, lives, and if the player was a boogie
//  Participants - Glorified list of 'Participant' objects - basically a container of players
//

class Participants
{
	private List<Participant> parts = new ArrayList<>();
	
	public Participants() {}

	// Get ; Set ; Add
	public List<Participant> getParts() { return parts; }
	public void setParts(List<Participant> parts) { this.parts = parts; }
	public void addPart(Participant part) { this.parts.add(part); }
}

class Participant
{
	private String name;
	private int lives;
	private boolean wasBoogie;
	
	public Participant() {}

	// Get ; Set
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	// Get ; Set
	public int getLives() { return lives; }
	public void setLives(int lives) { this.lives = lives; }

	// Get ; Set
	public boolean getBoogie() { return wasBoogie; }
	public void setBoogie(boolean wasBoogie) { this.wasBoogie = wasBoogie; }
}
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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener
{
	private static Main instance;

	private boolean enchantingTableCrafted;
	private boolean issuedStartLives;
	public FileConfiguration config;
	public Participants lives;
	public Participant boogie;

	public static Main getInstance()
	{
		return instance;
	}

	// To-do:
	// [x] Implement boogey chooser (/boogey)
	// [ ] (OPTIONAL) Do boogey chooser animation
	// [x] Implement give life (/givelife)
	// [ ] (OPTIONAL) Do give life animation
	// [x] Implement death reduces life event
	// [x] Implement start lives (/startlives)
	// [ ] (OPTIONAL) Do start lives animation
	// [x] Implement enchantment table limiter
	// [x] Implement resetconfig command in case of unforseen events (/resetconfig)
	// [x] Implement config file
	// [ ] (OPTIONAL) Implement discord announcer of players' deaths (automatic event)
	// [ ] (OPTIONAL) Implement discord standings on each session start (/session start & /session stop)
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

		// Test command - TO BE REMOVED
		getCommand("test").setExecutor(new Test());
		
		// Reset command - Resets config vars
		getCommand("resetconfig").setExecutor(new ResetConfig(this));
		
		// Setlive command - Set lives of 'player' to 'amount'
		getCommand("setlives").setExecutor(new ResetConfig(this));

		// Event Listener - Used for enchantment table crafting limitation
		getServer().getPluginManager().registerEvents(new EnchantmentLimiter(this), this);

		if(funcISL() == true) { try { lives = readFile(); } catch (IOException error) { error.printStackTrace(); } }
	}
	
	@Override
	public void onDisable()
	{
		saveConfig();

		if(funcISL() == true) { try { writeFile(lives); } catch (IOException error) { error.printStackTrace(); } }
		
		instance = null;
	}
	
	/// LIVES FILE RELATED ///
	
	public void setupStartLives()
	{
		//Make LastLives folder if it doesn't exist
		try
		{
			String PATH = System.getProperty("user.dir");
			Path path = Paths.get(PATH + "\\LastLife\\");

		    Files.createDirectories(path);
		}
		catch (Exception error)	{ System.out.println(error); }

		// Make lives.json file if it doesn't exist
		try
		{
			String PATH = System.getProperty("user.dir");
			File path = new File(PATH + "\\LastLife\\lives.json");

			if (path.createNewFile())
			{
				// Initialise file with n lives / player
				try(FileWriter fileWriter = new FileWriter(PATH + "\\LastLife\\lives.json")) { fileWriter.close(); }
				catch (Exception error)	{ System.out.println(error); }
			}
		}
		catch (Exception error)	{ System.out.println(error); }
	}
	
	public static Participants readFile() throws FileNotFoundException
	{
		GsonBuilder builder = new GsonBuilder(); 
		Gson gson = builder.create(); 
		BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\LastLife\\lives.json"));
		Participants playersJson = gson.fromJson(bufferedReader, Participants.class);
		
		return playersJson;
	}
	
	public static void writeFile(Participants participants) throws IOException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		FileWriter writer = new FileWriter(System.getProperty("user.dir") + "\\LastLife\\lives.json");
		writer.write(gson.toJson(participants));
		writer.close();
	}

	
	
	/// CONFIG RELATED ///
	
	public void getConfigVars()
	{
		enchantingTableCrafted = getConfig().getBoolean("enchantingTableCrafted");
		issuedStartLives = getConfig().getBoolean("issuedStartLives");
	}
	
	public boolean funcETC() // EnchantingTableCrafted
	{
		return enchantingTableCrafted;
	}
	
	public boolean funcISL() // IssuedStartLives
	{
		return issuedStartLives;
	}

	public void resetConfig()
	{
		FileConfiguration config = getConfig();
		config.set("enchantingTableCrafted", false);
		config.set("issuedStartLives", false);
		getConfigVars();
		saveConfig();
		saveDefaultConfig();
		getConfigVars();
	}

	public void updateConfig(boolean eTC, boolean iSL)
	{
		config.set("enchantingTableCrafted", eTC);
		config.set("issuedStartLives", iSL);
		saveConfig();
		saveDefaultConfig();
		getConfigVars();
	}
}

class Participants
{
	private List<Participant> parts = new ArrayList<>();
	
	public Participants() {}

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

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public int getLives() { return lives; }
	public void setLives(int lives) { this.lives = lives; }

	public boolean getBoogie() { return wasBoogie; }
	public void setBoogie(boolean wasBoogie) { this.wasBoogie = wasBoogie; }
}
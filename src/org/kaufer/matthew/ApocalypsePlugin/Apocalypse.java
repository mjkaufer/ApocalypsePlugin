package org.kaufer.matthew.ApocalypsePlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Apocalypse extends JavaPlugin {

	public final Logger logger = Logger.getLogger("Minecraft");
	
	private int magnitude = 10;//how far away zombie will spawn
	private Server s = this.getServer();
	
	private Material[] items = new Material[]{Material.CARROT, Material.MELON, Material.APPLE, Material.COOKED_FISH, Material.COOKED_CHICKEN, Material.TORCH, Material.STICK, Material.COBBLESTONE, Material.FLINT, Material.COOKED_BEEF, Material.FEATHER, Material.ARROW, Material.IRON_INGOT, Material.COOKIE, Material.BREAD, Material.ROTTEN_FLESH, Material.MUSHROOM_SOUP, Material.GOLDEN_APPLE};
	private Material[] tools = new Material[]{Material.STONE_PICKAXE, Material.BOW, Material.STONE_SWORD, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Material.IRON_SWORD, Material.LEATHER_LEGGINGS, Material.IRON_BOOTS, Material.CHAINMAIL_HELMET, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.DIAMOND, Material.DIAMOND_SWORD, Material.DIAMOND_HELMET};
	private List<String> firstTimePlayers = new ArrayList<String>();
	private ApocalypseListener listener = null;
	
    private HashMap<String,Material> corresponding = null;//corresponding materials for beast, scout, archer, jackofalltrades

    
	public static final String INVNAME = "Apocalypse Classes";
	
	
	public void am(String s){//prints to console with [Apocalypse] in front
		System.out.println("[Apocalypse] " + s);
	}
	
	public void addToFirstTimePlayers(String s){
		firstTimePlayers.add(s);
	}
	
	
    public boolean firstTime(String player){//return true if it's the player's first time joining
        
        File saveTo = new File(getDataFolder() + "\\data\\"+player+".dat");
        if (saveTo.exists())//if they already were on this server...
            return false;//we only want do do stuff for first time
        
        if(!saveTo.exists() && firstTimePlayers.indexOf(player) == -1)//they're not in first time players, so we'll add them
        {
        	addToFirstTimePlayers(player);
        }
        return true;
    }
    
    public boolean createFile(String player){
        File saveTo = new File(getDataFolder() + "\\data\\"+player+".dat");

        try {
            saveTo.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Apocalypse]Serious error - Could not create player data file for player " + player + ". Contact mjkaufer");
            return false;
        }
    }
	
	@Override
    public void onDisable()
    {
    		PluginDescriptionFile p = this.getDescription();
    		this.logger.info(p.getName() + " V" + p.getVersion() + " has been disabled.");   
    		getServer().clearRecipes();
    }
   
    @Override
    public void onEnable()
    {
    		PluginDescriptionFile p = this.getDescription();
    		this.logger.info(p.getName() + " V" + p.getVersion() + " has been enabled.");
            PluginManager pm = this.getServer().getPluginManager();
            listener = new ApocalypseListener(this);
            pm.registerEvents(listener , this);    
            
            getConfig().options().copyDefaults(true);
            saveConfig();
            
            corresponding = new HashMap<String,Material>();
            corresponding.put("beast",Material.DIAMOND_SWORD);
            corresponding.put("scout",Material.SADDLE);
            corresponding.put("archer",Material.BOW);
            corresponding.put("jackofalltrades",Material.GOLD_HELMET);
            

            
//            am("DATA FOLDER: " + getDataFolder());
            
//            firstTimePlayers.add("mjkaufer");//for debugging
            
            //            
            File dataFolder = getDataFolder();
            if(!dataFolder.exists())
                dataFolder.mkdir();
            
            File df = new File(getDataFolder()+ "\\data");
            if(!df.exists()){
            	df.mkdir();//make the directory
//            	am("Made data directory!");
            }
            
            
//            else
//            	System.out.println("Made Data Folder");
            
//            
//            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
//                @Override
//            	public void run() {
//                    addZombies();
//                    System.out.println("PRINT");
//                    try{
//                    	s.getWorld("world").getPlayers().get(0).sendMessage("AP");
//                    } catch(Exception e){
//                    	
//                    }
//                }
//            }, 10000L, 1000L);
            
            
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(this, new Runnable() {//scheduler to add zombies
                @Override
                public void run() {
                    addZombies();
                }
            }, 0L, 20L * 45L);//20 ticks is a second
            
            
            scheduler.scheduleSyncRepeatingTask(this, new Runnable() {//scheduler to reset chests
                @Override
                public void run() {
                    updateChests();
                }
            }, 0L, 20L * 30L * 60L);//every 30 minutes
            
    }
   

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
            Player player = (Player)sender;
            if(cmd.getName().equalsIgnoreCase("apocalypse"))
            {
            	if(args.length > 0){
	            	if(args[0].equalsIgnoreCase("gui") && firstTimePlayers.indexOf(player.getName()) > -1){
	            		gui(player);
	            		return true;
	            	}
	            	else if(args[0].equalsIgnoreCase("class") && firstTimePlayers.indexOf(player.getName()) > -1){//first time player, so we can let them choose a class
            			gui(player);
            			return true;
	            	}
	            	else if((args[0].equalsIgnoreCase("class") || args[0].equalsIgnoreCase("gui")) && firstTimePlayers.indexOf(player.getName()) == -1){
	            		player.sendMessage(sam("You've already gotten your class!"));
	            		return true;
	            	}
	            	
	            	
	            	
            		if(!player.hasPermission("Apocalypse.*")){//not allowed to do fancy commands
            			player.sendMessage(sam("Need Apocalypse.* permission!"));
            			return true;
            		}
	            	if(args[0].equalsIgnoreCase("zombie")){
	            		addZombies();
	            		return true;
	            	}
	            	else if(args[0].equalsIgnoreCase("chest")){
	            		updateChests();
	            		return true;
	            	}
            	}
        		PluginDescriptionFile p = this.getDescription();
                player.sendMessage(sam(ChatColor.AQUA + p.getName() + ChatColor.GREEN + " V" + p.getVersion() + ChatColor.AQUA + " , by " + ChatColor.RED + "mjkaufer"));
            	return true;
            }
            return false;
           
    }
    
    public String sam(String s){
    	return ChatColor.RED + "[" + ChatColor.AQUA + "Apocalypse" + ChatColor.RED + "] " + ChatColor.RESET + s;
    }
    
    public void callback(Player player, String cn){
		if(createFile(player.getName())){//the file created correctly
			player.getInventory().setContents(listener.getClasses().get(cn));
			player.sendMessage(sam(ChatColor.AQUA + "Chose " + ChatColor.ITALIC + cn + ChatColor.RESET + ChatColor.AQUA + " class"));
			firstTimePlayers.remove(player.getName());//make sure we get rid of them from the player list
			s.broadcastMessage(sam("Everybody please welcome " + ChatColor.GREEN + player.getName() + ChatColor.RESET + "... " + ChatColor.BOLD + ChatColor.RED + ChatColor.UNDERLINE + "TO THEIR DOOM!"));
			return;
		}
		
		player.sendMessage(sam("There was an error getting you your class. Contact server administrator."));


    }
    
    public void gui(Player p){
    	        
        
        Inventory inv = Bukkit.createInventory(null, 9, INVNAME);
        for(String cn : listener.getClasses().keySet()){
        	ItemStack a = new ItemStack(corresponding.get(cn), 1);
        	ItemMeta im = a.getItemMeta();
        	im.setDisplayName(cn.toUpperCase());
        	a.setItemMeta(im);
        	inv.addItem(a);
        }
        p.openInventory(inv);
//        inv.setItem(5, new ItemStack(Material.COBBLESTONE,45));
    }
    
    public boolean addZombies(){
    	World apocalypseWorld = this.getServer().getWorld("world");
    	List<Player> players = apocalypseWorld.getPlayers();
    	
    	for(Player p : players){
//    		p.sendMessage(ChatColor.GREEN + "MMMMmmmmmmmm.... Braaaaainsssss...");
    		//TODO - make it so zombies don't spawn in nothing
    		//check if location to spawn at has air, if not, move up, modulus around possibilities, when done, quit
    		Location l = p.getLocation();
    		for(int i = 0; i < (int)(Math.random() * 8 + 12); i++){//spawn a few zombies
        		float yaw = l.getYaw();//Yaw of person
        		//we want the zombie to spawn behind the person, more or less, so we'll give it an offset of +-30
        		float angle = 75;//variance
        		yaw += (Math.random() * angle*2 - angle);//+-angle
        		yaw = (yaw + 360) % 360;//360 is to get it out of 360
        		//with yaw, 90 maps to -x, -90 maps to x, so we're going to negate x values
        		yaw = yaw * (float)(Math.PI) / 180.0f;//to radians, very important

        		Vector sub = new Vector(-1 * magnitude * Math.sin(yaw),0,magnitude * Math.cos(yaw));//sin is to x because of weirdass layout - the -2 y val is so the monster spawns in the air 
        		Location mobPosition = l.subtract(sub);
        		mobPosition.setY(apocalypseWorld.getHighestBlockYAt(mobPosition));
        		apocalypseWorld.spawnEntity(mobPosition, EntityType.ZOMBIE);
    		}

    	}
    	
    	return true;
    }
    
    public boolean updateChests(){//refills chests with random shite
    	World apocalypseWorld = this.getServer().getWorld("world");
    	
    	for(Chunk c : apocalypseWorld.getLoadedChunks()){
    		for(BlockState b : c.getTileEntities()){
	    		if(b instanceof Chest){
		    		Chest chest = (Chest) b;
		    		Inventory ci = chest.getBlockInventory();
		    		
		    		ItemStack[] ris = randomItemStack();
	    			ci.clear();//empty that chest!

	    			int size = ci.getSize();
	    			
	    			for(int i = 0; i < ris.length; i++){
	    				int index = (int)(Math.random() * size);
	    				ci.setItem(index, ris[i]);//we might overlap, but that's ok
	    				
	    			}
	    		}
    		}
    	}
    	
    	s.broadcastMessage(ChatColor.AQUA + "[Apocalypse] " + ChatColor.GREEN + "Chests reset!");
    	
    	return true;
    }
    
    public ItemStack[] randomItemStack(){
    	
    	int length = Math.min(items.length, (int)(Math.random() * Math.random() * 4 + 5));
    	ItemStack[] ret = new ItemStack[length];
    	for(int i = 0; i < ret.length; i++){
    		if(i%4 == 0){//we'll do tools instead
    			int index = (int)(Math.random() * Math.random() * tools.length);
    			
    			
        		Material m = tools[index];
        		ret[i] = new ItemStack(m, 1, (short)Math.max(Math.random() * Math.random() * 100, 10));//add it

    			continue;
    		}
    		int index = (int)(Math.random() * Math.random() * items.length);//we square math.random so it stays near the front of the array, so more rare stuff goes in the back of the items array
    		int quantity = (int)Math.ceil((items.length - index) * (0.5+Math.random()));//more of items in front vs those in back - ceil so we don't get 0 of an item
//    		ret[i] = items[index];
    		Material m = items[index];
    		ret[i] = new ItemStack(m, quantity);//add it
    		
    	}
    	return ret;
    }



	
	
}

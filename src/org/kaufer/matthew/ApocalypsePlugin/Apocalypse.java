package org.kaufer.matthew.ApocalypsePlugin;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Apocalypse extends JavaPlugin {

	public final Logger logger = Logger.getLogger("Minecraft");
	
	private int magnitude = 10;//how far away zombie will spawn
	private Server s = this.getServer();
	@Override
    public void onDisable()
    {
    		PluginDescriptionFile p = this.getDescription();
    		this.logger.info(p.getName() + " V" + p.getVersion() + " has been disnabled.");   
    		getServer().clearRecipes();
    }
   
    @Override
    public void onEnable()
    {
    		PluginDescriptionFile p = this.getDescription();
    		this.logger.info(p.getName() + " V" + p.getVersion() + " has been enabled.");
            PluginManager pm = this.getServer().getPluginManager();
            pm.registerEvents(new ApocalypseListener(this), this);       
            
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
            scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    addZombies();
                }
            }, 0L, 20L * 45L);//20 ticks is a second
            
    }
   

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
            Player player = (Player)sender;
            if(cmd.getName().equalsIgnoreCase("apocalypse"))
            {
            	if(args.length > 0 && args[0].equalsIgnoreCase("zombie")){
            		addZombies();
            	}
        		else{
            		PluginDescriptionFile p = this.getDescription();
                    player.sendMessage(ChatColor.AQUA + p.getName() + ChatColor.GREEN + " V" + p.getVersion() + ChatColor.AQUA + " , by " + ChatColor.RED + "mjkaufer");
        		}
            	return true;
            }
            return false;
           
    }
    
    public boolean addZombies(){
    	World apocalypseWorld = this.getServer().getWorld("world");
    	List<Player> players = apocalypseWorld.getPlayers();
    	
    	for(Player p : players){
    		p.sendMessage(ChatColor.GREEN + "MMMMmmmmmmmm.... Braaaaainsssss...");
    		//TODO - make it so zombies don't spawn in nothing
    		//check if location to spawn at has air, if not, move up, modulus around possibilities, when done, quit
    		Location l = p.getLocation();
    		for(int i = 0; i < (int)(Math.random() * 4 + 2); i++){//spawn a few zombies
        		float yaw = l.getYaw();//Yaw of person
        		//we want the zombie to spawn behind the person, more or less, so we'll give it an offset of +-30
        		float angle = 60;//variance
        		yaw += (Math.random() * angle*2 - angle);//+-angle
        		yaw = (yaw + 360) % 360;//360 is to get it out of 360
        		//with yaw, 90 maps to -x, -90 maps to x, so we're going to negate x values
        		yaw = yaw * (float)(Math.PI) / 180.0f;//to radians, very important

        		Vector sub = new Vector(-1 * magnitude * Math.sin(yaw),0,magnitude * Math.cos(yaw));//sin is to x because of weirdass layout
        		Location mobPosition = l.subtract(sub);
        		apocalypseWorld.spawnEntity(mobPosition, EntityType.ZOMBIE);
    		}

    	}
    	
    	return true;
    }



	
	
}

package org.kaufer.matthew.ApocalypsePlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ApocalypseListener implements Listener{
    private Apocalypse plugin;
    private HashMap<String, ItemStack[]> classes = new HashMap<String, ItemStack[]>();
    
    
    public ApocalypseListener(Apocalypse instance)
    {
            plugin = instance;
            plugin.am("TEST2");
//            plugin.getConfig().options().
            //we'll hard code in the classes for now
            String[] c = new String[]{"beast","scout","archer","jackofalltrades"};
            
            for(String cn : c){//each class...
            	List<String> cs = plugin.getConfig().getStringList("classes."+cn);
            	plugin.am(cs.toString());
            	ItemStack[] add = new ItemStack[cs.size()];
            	for(int i = 0; i < add.length; i++){
            		String mName = cs.get(i).split(" ")[0];
//            		plugin.am(cs.get(i));
            		int quantity = Integer.parseInt(cs.get(i).split(" ")[1]);
            		Material m = Material.valueOf(mName);
            		add[i] = new ItemStack(m, quantity);
            		classes.put(cn, add);
            	}
//        		plugin.am(add.toString());
            }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){//only want to do first time
        Player pla = event.getPlayer();
        String dis = pla.getName();
        System.out.println("First:"+firstTime(dis));
    }
    
    public boolean firstTime(String player){//return true if it's the player's first time joining
       
        File saveTo = new File(plugin.getDataFolder() + "data/"+player+".dat");
        if (saveTo.exists())//if they already were on this server...
            return false;//we only want do do stuff for first time
        
        if(!saveTo.exists())
        {
            try {
                saveTo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[Apocalypse]Serious error - Could not create player data file for player " + player + ". Contact mjkaufer");
            }
        }
        return true;
    }
}

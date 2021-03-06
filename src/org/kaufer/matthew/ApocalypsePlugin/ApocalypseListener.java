package org.kaufer.matthew.ApocalypsePlugin;

import java.util.HashMap;
import java.util.List;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ApocalypseListener implements Listener{
    private Apocalypse plugin;
    private HashMap<String, ItemStack[]> classes = new HashMap<String, ItemStack[]>();
    
    
    public ApocalypseListener(Apocalypse instance)
    {
            plugin = instance;
//            plugin.am("TEST2");
//            plugin.getConfig().options().
            //we'll hard code in the classes for now
            String[] c = new String[]{"beast","scout","archer","jackofalltrades"};
            
            for(String cn : c){//each class...
            	List<String> cs = plugin.getConfig().getStringList("classes."+cn);
//            	plugin.am(cs.toString());
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
    
    public HashMap<String, ItemStack[]> getClasses(){
    	return classes;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){//only want to do first time
        Player pla = event.getPlayer();
        String dis = pla.getName();
        plugin.firstTime(dis);
//        System.out.println("First:"+plugin.firstTime(dis));
        
    }
    
    @EventHandler
    public void inventoryClick(InventoryClickEvent event){
        Player p = (Player)event.getWhoClicked();
        if(!plugin.firstTime(p.getName()))//not their first time
        	return;
        if(event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase(Apocalypse.INVNAME)){
            if(event.getCurrentItem() != null){
                if(event.getCurrentItem().getType() != Material.AIR){
                    event.setCancelled(true);
//                    System.out.println("Got a class!");
                    String cn = event.getCurrentItem().getItemMeta().getDisplayName();
//                    p.sendMessage(plugin.sam("You picked a class! The " + cn + " class!"));
                    p.closeInventory();//close their inventory
                    plugin.callback(p, cn.toLowerCase());
                }
            }
        }
    }
    
}

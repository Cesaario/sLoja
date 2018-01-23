package me.soldado.loja;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LojaSegurança implements Listener {

	public Main plugin;

	public LojaSegurança(Main plugin)
	{
		this.plugin = plugin;
	}
	
	public HashMap<Location, Loja> lojas = new HashMap<Location, Loja>();
	
	@EventHandler
	public void abrirBau(PlayerInteractEvent event){

		if(event.isCancelled()) return;
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

		Block b = event.getClickedBlock();
		Player p = event.getPlayer();
		if(b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)){

			lojas = plugin.core.lojas;
			Location loc = b.getLocation();
			if(lojas.containsKey(loc)){
				Loja l = lojas.get(loc);
				Chest c = (Chest) b.getState();
				boolean aux = false;
				if(l.getBau() != null && l.getBau().equals(c)){
					aux = true;
				}
				if(l.getDono() == p) aux = false; 

				if(aux) event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void quebrarBau(BlockBreakEvent event){

		if(event.isCancelled()) return;

		Block b = event.getBlock();
		Player p = event.getPlayer();
		if(b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)){

			Chest c = (Chest) b.getState();
			boolean aux = false;
			lojas = plugin.core.lojas;
			Location loc = b.getLocation();
			if(lojas.containsKey(loc)){
				
				Loja l = lojas.get(loc);

				if(l.getBau() != null){
					if(l.getBau().equals(c)){
						aux = true;
					}
				}
				if(l.getDono() == p) aux = false; 
				if(l.isAdm()) aux = false;

				if(aux) event.setCancelled(true);
			}
		}

	}
}

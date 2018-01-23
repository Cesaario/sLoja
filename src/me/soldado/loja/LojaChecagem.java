package me.soldado.loja;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

public class LojaChecagem implements Listener {
	
	public Main plugin;

	public LojaChecagem(Main plugin)
	{
		this.plugin = plugin;
	}
	
	public HashMap<Location, Loja> lojas = new HashMap<Location, Loja>();
	FileConfiguration msg = Main.msg;

	String lojadestruida = msg.getString("LojaDestruida").replace("&", "§");
	
	@EventHandler
	public void placaCaindo(BlockPhysicsEvent event) {

		if(event.isCancelled()) return;

		Block b = event.getBlock();
		if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
			Sign s = (Sign) b.getState().getData();
			Block attachedBlock = b.getRelative(s.getAttachedFace());
			if (attachedBlock.getType() == Material.AIR) {
				if(plugin.core.validarLoja(b.getLocation())){
					Loja l = plugin.core.getLoja(b);
					plugin.core.removerLoja(l);
				}
			}
		}
	}

	@EventHandler
	public void quebrarPlaca(BlockBreakEvent event){

		if(event.isCancelled()) return;

		Block b = event.getBlock();
		if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
			if(plugin.core.validarLoja(b.getLocation())){
				Loja l = plugin.core.getLoja(b);
				Player p = event.getPlayer();
				if(l.getDono() == p || l.isAdm()){
					plugin.core.removerLoja(l);
					p.sendMessage(lojadestruida);
				}else{
					event.setCancelled(true);
				}
			}
		}
	}
	
//	public void validarLojas(){
//		
//		lojas = plugin.core.lojas;
//		
//		for(Loja l : lojas){
//			Block b = l.getLoc().getBlock();
//			if(!(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)){
//				
//				plugin.core.removerLoja(l);
//				
//			}
//		}
//	}

}

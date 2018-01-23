package me.soldado.loja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LojaCore {
	
	public Main plugin;
	public HashMap<Location, Loja> lojas;
	
	public LojaCore(Main plugin)
	{
		this.plugin = plugin;
		lojas = new HashMap<Location, Loja>();
	}
	
	public FileConfiguration msg = Main.msg;

	String lojacriada = msg.getString("LojaCriada").replace("&", "§");
	
	public void criarLoja(OfflinePlayer p, Location loc,
			ItemStack item, int compra, int venda, int quantidade, boolean adm){
		
		Loja l = new Loja(p, loc, item, compra, venda, quantidade, adm);
		lojas.put(loc, l);
		try{
			Player pl = (Player) p;
			pl.sendMessage(lojacriada);
		}
		catch(Exception e){
			
		}
	}
	
	public boolean validarLoja(Location loc){
		
		if(lojas.containsKey(loc)){
			return true;
		}
		return false;
	}
	
//	public boolean validarLoja(Location loc){
//		Bukkit.getServer().broadcastMessage(loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
//		Bukkit.getServer().broadcastMessage(loc.getWorld().getName()+"linha52");
//		boolean aux = false;
//		for(Loja l : lojas.key){
//			Bukkit.getServer().broadcastMessage(l.getLoc().getWorld().getName()+"linha 55");
//			Bukkit.getServer().broadcastMessage(l.getItem().getType().toString());
//			if(l.getLoc().getWorld().getName().equals(loc.getWorld().getName())){
//				if(l.getLoc().equals(loc)){
//					Bukkit.getServer().broadcastMessage(l.getLoc().getBlockX() + " " + l.getLoc().getBlockY() + " " + l.getLoc().getBlockZ());
//					
//					aux = true;
//					break;
//				}
//			}
//		}
//		return aux;
//	}
	
	public Loja getLoja(Block b){
		
		Loja returnloja = null;
		Location loc = b.getLocation();
		if(validarLoja(loc)){
			returnloja = lojas.get(loc);
		}
		return returnloja;
	}
	
//	public Loja getLoja(Block b){
//		
//		Loja returnloja = null;
//		
//		if(validarLoja(b.getLocation())){
//			for(Loja l : lojas){
//				if(l.getLoc().getWorld().equals(b.getLocation().getWorld())){
//					if(l.getLoc().getBlock().equals(b)){
//						returnloja = l;
//						break;
//					}
//				}
//			}
//		}
//		return returnloja;
//	}
	
	public void removerLoja(Loja l){
		Location loc = l.getLoc();
		lojas.remove(loc);
	}
	
	public void recarregarLoja(String s){
		
		boolean suc = true;
		
		String[] param = s.split(";");
		OfflinePlayer dono;
		Location loc;
		ItemStack item;
		int compra;
		int venda;
		int quantidade;
		boolean adm;

		if(param.length != 7) suc = false;
		
		try{
			dono = Bukkit.getServer().getOfflinePlayer(param[0]);
			String locs = param[1];
			String[] xyz = locs.split(":");
			int x = Integer.parseInt(xyz[0]);
			int y = Integer.parseInt(xyz[1]);
			int z = Integer.parseInt(xyz[2]);
			WorldCreator wc = new WorldCreator(xyz[3]);
			World mundo = Bukkit.createWorld(wc);
			loc = new Location(mundo, x, y, z);
			String id = param[2];
			if(id.contains(":")){
				String ids[] = id.split(":");
				int idnumero = Integer.parseInt(ids[0]);
				int datanumero = Integer.parseInt(ids[1]);
				item = new ItemStack(Material.getMaterial(idnumero), 1, (byte) datanumero);
			}else{
				int idnumero = Integer.parseInt(id);
				item = new ItemStack(Material.getMaterial(idnumero));
			}
			compra = Integer.parseInt(param[3]);
			venda = Integer.parseInt(param[4]);
			quantidade = Integer.parseInt(param[5]);
			if(param[6].equals("true")){
				adm = true;
			}else adm = false;
			
			criarLoja(dono, loc, item, compra, venda, quantidade, adm);
			
		}catch(Exception e){
			suc = false;
			e.printStackTrace();
		}
	}

}

package me.soldado.loja;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class LojaGUI implements Listener {
	
	public Main plugin;

	public LojaGUI(Main plugin)
	{
		this.plugin = plugin;
	}
	
	public HashMap<Location, Loja> lojas = new HashMap<Location, Loja>();
	
	@EventHandler
	public void clicar(InventoryClickEvent event){
		
		if(!(event.getWhoClicked() instanceof Player)) return;
			
		if(!event.getInventory().getTitle().contains("Menu de Lojas")) return;
		
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
		
		Player p = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if(checkLoja(item)){
		
			Location loc = getLoc(item);
			p.teleport(loc);
			p.closeInventory();
			
		}else if(item.getType().equals(Material.EMPTY_MAP)){
			
			String invnome = event.getInventory().getTitle();
			String spagatual = ChatColor.stripColor(invnome).replace("Menu de Lojas ", "");
			int pagatual = Integer.parseInt(spagatual);
			
			if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
				
				String nome = item.getItemMeta().getDisplayName();
				if(nome.contains("anterior")){
					p.closeInventory();
					abrirGUI(p, pagatual-1);
				}else if(nome.contains("próxima")){
					p.closeInventory();
					abrirGUI(p, pagatual+1);
				}
				
			}
		}
		event.setCancelled(true);
	}
	
	public void abrirGUI(Player p, int pag){
		lojas = plugin.core.lojas;
		Inventory inv = Bukkit.getServer().createInventory(null, 54, "§aMenu de Lojas " + pag);
		for(int i = 0; i < 45; i++){
		
			int index = i + ((pag-1)*45);
			if(index < lojas.size()) inv.setItem(i, getLoja(index));
			
		}
		if(pag > 1){
			ItemStack ant = new ItemStack(Material.EMPTY_MAP);
			ItemMeta antm = ant.getItemMeta();
			antm.setDisplayName(ChatColor.GOLD + "Clique para ir para a página anterior");
			ant.setItemMeta(antm);
			inv.setItem(48, ant);
		}
		ItemStack prox = new ItemStack(Material.EMPTY_MAP);
		ItemMeta proxm = prox.getItemMeta();
		proxm.setDisplayName(ChatColor.GOLD + "Clique para ir para a próxima página");
		prox.setItemMeta(proxm);
		inv.setItem(50, prox);
		
		p.openInventory(inv);
		
	}
	
	public ItemStack getLoja(int i){

		lojas = plugin.core.lojas;
		ArrayList<Loja> lojs = new ArrayList<Loja>(lojas.values());
		Loja l = lojs.get(i);
		Location loc = l.getLoc();
		String dono = l.getDono().getName();
		String xyz = "x" + loc.getBlockX() + " y" + loc.getBlockY() + " z" + loc.getBlockZ();
		String mundo = l.getLoc().getWorld().getName();
		String compra = l.getCompra()+"";
		String venda = l.getVenda()+"";
		String nomeitem = l.getItem().getType().toString();
		String quantidade = l.getQuantiade()+"";
		String admnome;
		boolean adm = l.isAdm();
		if(adm) admnome = "Sim";
		else admnome = "Não";
		
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta sm = (SkullMeta) skull.getItemMeta();
		sm.setOwner(dono);
		sm.setDisplayName(ChatColor.DARK_AQUA + "Loja de " + dono);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "Localização: " + ChatColor.WHITE + xyz);
		lore.add(ChatColor.GREEN + "Mundo: " + ChatColor.WHITE + mundo);
		lore.add(ChatColor.GREEN + "Valor de compra: " + ChatColor.WHITE + compra);
		lore.add(ChatColor.GREEN + "Valor de venda: " + ChatColor.WHITE + venda);
		lore.add(ChatColor.GREEN + "Item: " + ChatColor.WHITE + nomeitem);
		lore.add(ChatColor.GREEN + "Quantidade: " + ChatColor.WHITE + quantidade);
		lore.add(ChatColor.GREEN + "Loja do servidor: " + ChatColor.WHITE + admnome);
		lore.add(ChatColor.GOLD + "Clique aqui para se teletransportar para a loja.");
		sm.setLore(lore);
		
		skull.setItemMeta(sm);
		return skull;
	}
	
	public boolean checkLoja(ItemStack item){
		
		boolean aux = false;
		if(item == null || item.getType() == Material.AIR) return false;
		
		if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			
			String s = item.getItemMeta().getDisplayName();
			if(s.contains("Loja de ")) aux = true;
		}
		return aux;
	}
	
	public Location getLoc(ItemStack item){
		//Sem muitos checks, não precisa né?
		String loreloc = item.getItemMeta().getLore().get(0);
		String loreaux = ChatColor.stripColor(loreloc).replace("Localização: ", "");
		String mundo = item.getItemMeta().getLore().get(1);
		String mundoaux = ChatColor.stripColor(mundo).replace("Mundo: ", "");
		String[] locs = loreaux.split(" ");
		String xs = locs[0].replace("x", "");
		String ys = locs[1].replace("y", "");
		String zs = locs[2].replace("z", "");
		World w = Bukkit.getServer().getWorld(mundoaux);
		int x = Integer.parseInt(xs);
		int y = Integer.parseInt(ys);
		int z = Integer.parseInt(zs);
		
		return new Location(w, x, y, z);
	}

}

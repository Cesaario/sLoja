package me.soldado.loja;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.Sign;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class LojaCriar implements Listener {
	
	public Main plugin;

	public LojaCriar(Main plugin)
	{
		this.plugin = plugin;
	}

	public FileConfiguration msg = Main.msg;
	public FileConfiguration cfg = Main.cfg;

	String semperm = msg.getString("SemPermissao").replace("&", "§");
	String erro = msg.getString("ErroAoCriarLoja").replace("&", "§");
	String sembau = msg.getString("LojaCriadaForaDoBau").replace("&", "§");

	String prefixo = cfg.getString("PrefixoLoja").replace("&", "§");
	String corloja = cfg.getString("CorLojaVip").replace("&", "§");
	
	@EventHandler
	public void criarLoja(SignChangeEvent event){

		if(event.isCancelled()) return;

		if(event.getLine(0).equals("[Loja]")){

			Player p = event.getPlayer();
			Block b = event.getBlock();
			
			Player dono;
			Location loc;
			ItemStack item;
			int venda;
			int compra;
			int quantidade;
			boolean adm;

			if(!(b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN))) return;

			if(!p.hasPermission("loja.criar")){
				p.sendMessage(semperm);
				b.breakNaturally();
				return;
			}
			
			if(!event.getLine(3).equals(null)){
				if(!validarID(event.getLine(3))){
					event.setLine(3, "???");
				}
			}else event.setLine(3, "???");

			if(checkLoja(event.getLines())){
				
				ItemStack aux = new ItemStack(Material.AIR);
				if(!event.getLine(3).equals("???") && validarID(event.getLine(3))){
					String ids = event.getLine(3);
					if(isNumericString(ids)){
						int id = Integer.parseInt(ids);
						aux = new ItemStack(Material.getMaterial(id));
					}else if(ids.contains(":")){
						String[] n = ids.split(":");
						if(isNumericString(n[0]) && isNumericString(n[1])){
							int id = Integer.parseInt(n[0]);
							int data = Integer.parseInt(n[1]);
							aux = new ItemStack(Material.getMaterial(id), 1, (byte) data);
						}
					}
				}
				boolean adminshop = false;
				if(p.hasPermission("loja.adm")){
					adminshop = true;
				}
				int qnt = 1;
				if(isNumericString(event.getLine(1))){
					int auxi = Integer.parseInt(event.getLine(1));
					qnt = auxi;
				}
				int valorvenda = getValorVenda(event.getLine(2));
				int valorcompra = getValorCompra(event.getLine(2));
				
				//Setando as informações da loja
				dono = event.getPlayer();
				loc = b.getLocation();
				item = aux;
				quantidade = qnt;
				venda = valorvenda;
				compra = valorcompra;
				adm = adminshop;
				
				if(!p.hasPermission("loja.adm")){
					event.setLine(0, corloja+p.getName());
				}else event.setLine(0, prefixo);
				
				String s = "§aC§r " + compra + " : " + "§cV§r "+ venda;
				if(venda == 0){
					String[] s2 = s.split(":");
					s = s2[0];
				}
				if(compra == 0){
					String[] s2 = s.split(":");
					s = s2[1];
				}
				event.setLine(2, s);
				
				if(validarID(event.getLine(3))){
					String linha = event.getLine(3);
					int id = 0;
					int data = 0;
					if(linha.contains(":")){
						String[] lados = linha.split(":");
						String idstring = lados[0];
						String datastring = lados[1];
						id = Integer.parseInt(idstring);
						data = Integer.parseInt(datastring);
					}else{
						id = Integer.parseInt(event.getLine(3));
					}
					String mn = NomeItens.nomes.get(id);
					if(data != 0) mn += ":" + data;
					event.setLine(3, mn);
				}

				if(!checkBau(b) && !adm){
					p.sendMessage(sembau);
				}
				
				plugin.core.criarLoja(p, loc, item, valorcompra, valorvenda, quantidade, adminshop);
				
			}else{

				p.sendMessage(erro);
				b.breakNaturally();

			}
		}
	}
	
	@SuppressWarnings("unused")
	public boolean checkLoja(String[] linhas){
		
		boolean aux = true;
		int numero;
		
		if(linhas.length == 4){
			
			if(!linhas[0].equals("[Loja]")){
				aux = false;
			}else if(!isNumericString(linhas[1])){
				aux = false;
			}else if(!checkLinha3(linhas[2])){
				aux = false;
			}
			
		}
		
		return aux;
	}
	
	/*public boolean checkLinha3(String s){
		
		int aux = 0;
		boolean flag = false;
		boolean flag2 = false;
		
		for(int i = 0; i < s.length(); i++){
			
			if(aux == 0){
				if(s.charAt(0) == 'V') aux++;
			}else
			if(aux == 1){
				if(!(isNumeric(s.charAt(i)) || (s.charAt(i) == ' '))){
					if(s.charAt(i) == ':'){
						aux ++;
					}else aux = 4;
				}else{
					if(isNumeric(s.charAt(i))) flag = true;
					else if((s.charAt(i) == ' ') && flag) aux = 4; 
				}
			}else 
			if(aux == 2){
				if(!(isNumeric(s.charAt(i)) || (s.charAt(i) == ' '))){
					if(s.charAt(i) == 'C'){
						aux ++;
					}else aux = 4;
				}else{
					if((s.charAt(i) == ' ')) flag2 = true;
					else if(isNumeric(s.charAt(i)) && flag2) aux = 4; 
				}
			}
		}
		
		if(aux == 3) return true;
		else return false;
		
	}*/
	
	public boolean checkLinha3(String s){
		
		boolean l1aux = false;
		boolean l2aux = false;
		if(s.contains(":")){
			String[] lados = s.split(":");
			String l1 = lados[1];
			String l2 = lados[0];
			if(l1.contains("V")){
				String lad1 = l1.replace("V", "").replace(" ", "");
				if(isNumericString(lad1)){
					l1aux = true;
				}
			}
			if(l2.contains("C")){
				String lad2 = l2.replace("C", "").replace(" ", "");
				if(isNumericString(lad2)){
					l2aux = true;
				}
			}
		}
		
		if(l1aux && l2aux) return true;
		else return false;
	}
	
	@SuppressWarnings("unused")
	public boolean isNumeric(char c)  
	{  
		String s = "";
		s += c;
	  try  
	  {  
	    double d = Double.parseDouble(s);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}	
	@SuppressWarnings("unused")
	public boolean isNumericString(String s)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(s);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public boolean validarID(String s){
		
		boolean aux = false;
		if(isNumericString(s)){
			int id = Integer.parseInt(s);
			try{
				Material m = Material.getMaterial(id);
				aux = true;
			}
			catch(Exception e){
				
			}
		}else if(s.contains(":")){
			String[] n = s.split(":");
			if(isNumericString(n[0]) && isNumericString(n[1])){
				int id = Integer.parseInt(n[0]);
				int data = Integer.parseInt(n[1]);
				try{
					ItemStack item = new ItemStack(Material.getMaterial(id), 1, (byte) data);
					aux = true;
				}
				catch(Exception e){
					aux = false;
				}
			}
		}
		return aux;
	}
	
	public boolean checkBau(Block b){

		boolean aux = false;

		if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
			Sign s = (Sign) b.getState().getData();
			Block bau = b.getRelative(s.getAttachedFace());
			if(bau.getType().equals(Material.CHEST) || bau.getType().equals(Material.TRAPPED_CHEST)){
				aux = true;
			}
		}

		return aux;
	}
	
	public int getValorCompra(String s){
		
		String[] lados = s.split(":");
		String compra = lados[0];
		String valor = "";
		for(int i = 0; i < compra.length(); i++){
			
			char c = compra.charAt(i);
			if(isNumeric(c)){
				valor += c;
			}
		}
		int valorint = Integer.parseInt(valor);	
		return valorint;
	}
	
	public int getValorVenda(String s){
		
		String[] lados = s.split(":");
		String venda = lados[1];
		String valor = "";
		for(int i = 0; i < venda.length(); i++){
			
			char c = venda.charAt(i);
			if(isNumeric(c)){
				valor += c;
			}
		}
		int valorint = Integer.parseInt(valor);	
		return valorint;
		
	}

}

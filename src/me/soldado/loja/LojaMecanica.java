package me.soldado.loja;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Sign;

public class LojaMecanica implements Listener {

	public Main plugin;

	public LojaMecanica(Main plugin)
	{
		this.plugin = plugin;
	}
	
	//Botão esquerdo, vender
	//Bodão direito, comprar
	
	FileConfiguration msg = Main.msg;

	String naovende = msg.getString("LojaSoCompra").replace("&", "§");
	String naocompra = msg.getString("LojaSoVende").replace("&", "§");
	String sembau = msg.getString("LojaSemBau").replace("&", "§");
	String autocomprar = msg.getString("NaoPodeComprarDeSiMesmo").replace("&", "§");
	String comprar = msg.getString("Comprar").replace("&", "§");
	String semespaco = msg.getString("SemEspacoNoInventario").replace("&", "§");
	String comprou = msg.getString("ComprouNaSuaLoja").replace("&", "§");
	String naocontemnobau = msg.getString("SemItemNoBau").replace("&", "§");
	String semdinheiro = msg.getString("SemDinheiro").replace("&", "§");
	String autovender = msg.getString("NaoPodeVenderParaSiMesmo").replace("&", "§");
	String vender = msg.getString("Vender").replace("&", "§");
	String semitem = msg.getString("JogadorSemItemParaVender").replace("&", "§");
	String vendeu = msg.getString("VendeuNaSuaLoja").replace("&", "§");
	String semespaconobau = msg.getString("SemEspacoNoBau").replace("&", "§");
	String donosemdinheiro = msg.getString("DonoDaLojaSemDinheiro").replace("&", "§");
	String semitemdefinido = msg.getString("SemItemDefinido").replace("&", "§");
	String lojaatualizada = msg.getString("LojaAtualizada").replace("&", "§");
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void definirItem(PlayerInteractEvent event){
		Action ac = event.getAction();

		if(!ac.equals(Action.RIGHT_CLICK_BLOCK)) return;
		
		Player p = event.getPlayer();
		
		if(!p.isSneaking()) return;
		
		if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
		
		Block b = event.getClickedBlock();
		
		if(checkPlaca(b)){
			
			if(plugin.core.validarLoja(b.getLocation())){
				Loja l = plugin.core.getLoja(b);
				if(l.getDono().equals(p) || (l.isAdm() && (p.hasPermission("loja.adm") || p.isOp()))){
					
					ItemStack item = p.getItemInHand().clone();
					item.setAmount(1);
					l.setItem(item);
					
					Sign s = (Sign) b.getState();
					s.setLine(3, NomeItens.nomes.get(item.getTypeId()));
					s.update();
					
					p.sendMessage(lojaatualizada);
					event.setCancelled(true);
				}
			}
		}
		
	}
	
	@EventHandler
	public void comprar(PlayerInteractEvent event){
		
		Action ac = event.getAction();

		if(!(ac.equals(Action.LEFT_CLICK_BLOCK) || ac.equals(Action.RIGHT_CLICK_BLOCK))) return;

		Block b = event.getClickedBlock();
		Player p = event.getPlayer();
		if(checkPlaca(b)){

			if(plugin.core.validarLoja(b.getLocation())){

				Loja l = plugin.core.getLoja(b);
				if(ac.equals(Action.LEFT_CLICK_BLOCK)){
					if(l.soCompra()){
						p.sendMessage(naovende);
					}else{
						vender(p, l);
					}
				}else if(ac.equals(Action.RIGHT_CLICK_BLOCK)){
					if(l.soVende()){
						if(!p.isSneaking()) p.sendMessage(naocompra);
					}else{
						comprar(p, l);
					}
				}
			}
		}
	}
	
	public boolean checkPlaca(Block b){
		
		if(b != null && b.getType() != Material.AIR){
			
			Material tipo = b.getType();
			if(tipo == Material.SIGN_POST || tipo == Material.WALL_SIGN){
				return true;
			}else return false;	
		
		}else return false;
	
	}
	
	@SuppressWarnings("deprecation")
	public void comprar(Player p, Loja l){

		ItemStack item = l.getItem();
		int quantidade = l.getQuantiade();
		int valor = l.getCompra();
		OfflinePlayer dono = l.getDono();
		Chest c = l.getBau();
		
		if(p.isSneaking()) return;

		if(item.getType().getId() == 0){
			p.sendMessage(semitemdefinido);
			return;
		}
		
		if(c == null && !l.isAdm()){
			p.sendMessage(sembau);
			return;
		}
		
		if(!l.isAdm() && p == dono){
			p.sendMessage(autocomprar);
			return;
		}
		
		String vfi = getValorString(valor);
		
		if(possuiDinheiro(p, valor)){
			if(l.isAdm()){
				if(temEspaço(p.getInventory(), item, quantidade)){
					
					adicionarItem(p.getInventory(), item, quantidade);
					retirarDinheiro(p, valor);
					p.sendMessage(comprar.replace("%valor%", vfi)
							.replace("%item%", NomeItens.nomes.get(item.getTypeId()))
							.replace("%quantidade%", quantidade+""));
					
				}else p.sendMessage(semespaco);
			}else{
				if(contemNoInventario(c.getInventory(), item.getType(), quantidade)){
					if(temEspaço(p.getInventory(), item, quantidade)){
						
						retirarDinheiro(p,valor);
						adicionarDinheiro(dono,valor);
//						removerItens(c.getInventory(), item.getType(), quantidade);
//						adicionarItem(p.getInventory(), item, quantidade);
						ArrayList<ItemStack> itens = removerItens(c.getInventory(), item.getType(), quantidade);
						for(ItemStack it : itens){
							adicionarItem(p.getInventory(), it, it.getAmount());
						}
						
						p.sendMessage(comprar.replace("%valor%", vfi)
								.replace("%item%", NomeItens.nomes.get(item.getTypeId()))
								.replace("%quantidade%", quantidade+""));
						try{
							Player donom = Bukkit.getServer().getPlayer(dono.getName());
							donom.sendMessage(comprou.replace("%jogador%", p.getName())
									.replace("%valor%", vfi)
									.replace("%item%", NomeItens.nomes.get(item.getTypeId()))
									.replace("%quantidade%", quantidade+""));
						}
						catch(Exception e){
						}
						
					}else p.sendMessage(semespaco);
				}else p.sendMessage(naocontemnobau.replace("%item%", NomeItens.nomes.get(item.getTypeId())));
			}
		}else p.sendMessage(semdinheiro);
	}
	
	@SuppressWarnings("deprecation")
	public void vender(Player p, Loja l){

		ItemStack item = l.getItem();
		int quantidade = l.getQuantiade();
		int valor = l.getVenda();
		OfflinePlayer dono = l.getDono();
		Chest c = l.getBau();
		boolean tudo = p.isSneaking();

		if(item.getType().getId() == 0){
			p.sendMessage(semitemdefinido);
			return;
		}
		
		if(c == null && !l.isAdm()){
			p.sendMessage(sembau);
			return;
		}

		if(!l.isAdm() && p == dono){
			p.sendMessage(autovender);
			return;
		}
		
		double vf;
		int qf;
		
		double valord = valor;
		double quantidaded = quantidade;
		
		double vpi = valord/quantidaded;
		int quant = quantidadeNoInv(p.getInventory(), item.getType());
		
		if(tudo){
			qf = quant;
			vf = vpi * quant;
		}else{
			if(quant < quantidade){
				qf = quant;
				vf = vpi * quant;
			}else{
				qf = quantidade;
				vf = valor;
			}
		}
		
		Double vfaux = vf;
		String vfi = getValorString(vfaux);
		

		if(l.isAdm()){
			if(contemNoInventario(p.getInventory(), item.getType(), qf)){
				
				removerItens(p.getInventory(), item.getType(), qf);
				adicionarDinheiro(p, vf);
				p.sendMessage(vender.replace("%valor%", vfi+"")
						.replace("%item%", NomeItens.nomes.get(item.getTypeId()))
						.replace("%quantidade%", qf+""));

			}else p.sendMessage(semitem);
		}else{
			if(possuiDinheiro(dono, vf)){
				if(temEspaço(c.getInventory(), item, qf)){
					if(contemNoInventario(p.getInventory(), item.getType(), qf)){
						
						retirarDinheiro(dono, vf);
						adicionarDinheiro(p, vf);
//						removerItens(p.getInventory(), item.getType(), quantidade);
//						adicionarItem(c.getInventory(), item, quantidade);
						ArrayList<ItemStack> itens = removerItens(p.getInventory(), item.getType(), qf);
						for(ItemStack it : itens){
							adicionarItem(c.getInventory(), it, it.getAmount());
						}
						p.sendMessage(vender.replace("%valor%", vfi+"")
								.replace("%item%", NomeItens.nomes.get(item.getTypeId()))
								.replace("%quantidade%", qf+""));
						
						try{
							Player donom = Bukkit.getServer().getPlayer(dono.getName());
							donom.sendMessage(vendeu.replace("%jogador%", p.getName())
									.replace("%valor%", vfi+"")
									.replace("%item%", NomeItens.nomes.get(item.getTypeId()))
									.replace("%quantidade%", qf+""));
						}
						catch(Exception e){
						}
						
					}else p.sendMessage(semitem);
				}else p.sendMessage(semespaconobau);
			}else p.sendMessage(donosemdinheiro);
		}

	}
	
	public void adicionarItem(Inventory inv, ItemStack item, int quantidade){
		
		int aux = quantidade;
		
		if(item.getMaxStackSize() < aux){
			int max = item.getMaxStackSize();
			while(aux > 0){
				item.setAmount(max);
				inv.addItem(item);
				aux -= max;
			}
		}else{
			item.setAmount(aux);
			inv.addItem(item);
			aux = 0;
		}
	}
	
	public boolean contemNoInventario(Inventory inv, Material tipo, int quantidade){

    	int quant = -1;
    	
    	for(int i = 0; i < inv.getSize(); i++){
    		ItemStack item = inv.getItem(i);
    		if(item != null && item.getType() != Material.AIR){
	    		if(item.getType().equals(tipo)){
	    			if(quant == -1) quant = 0;
	    			quant += item.getAmount();
	    		}
    		}
    	}
    	
    	if(quant >= quantidade) return true;
    	else return false;
		
		/*if(inv.containsAtLeast(item, quantidade)){
			if(quantidade > 0){
				return true;
			}else return false;
			
		}return false;*/
	}
	
	public boolean temEspaço(Inventory inv, ItemStack item, int quantidade){
		
		boolean aux = false;
		
		if(inv.firstEmpty() == -1){
			int n = 0;
			for(int i = 0; i < inv.getSize(); i++){
				
				if(inv.getItem(i).getType().equals(item.getType())){
					int qnt = inv.getItem(i).getAmount();
					int resto = item.getMaxStackSize() - qnt;
					n += resto;
				}
				
			}
			if(n >= quantidade){
				aux = true;
			}
		}else aux = true;
		
		return aux;
	}
	
    public ArrayList<ItemStack> removerItens(Inventory inventory, Material type, int amount) {
        
        int size = inventory.getSize();
        ArrayList<ItemStack> itens = new ArrayList<ItemStack>();
        boolean baux = true;
        
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()){
            	if(baux){
	            	ItemStack aux = inventory.getItem(slot).clone();
	                aux.setAmount(amount);
	                itens.add(aux);
	                baux = false;
            	}
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
        return itens;
    }
    
    public int quantidadeNoInv(Inventory inv, Material type){
    	
    	int quant = 0;
    	
    	for(int i = 0; i < inv.getSize(); i++){
    		ItemStack item = inv.getItem(i);
    		if(item != null && item.getType() != Material.AIR){
	    		if(item.getType().equals(type)){
	    			quant += item.getAmount();
	    		}
    		}
    	}
    	return quant;
    }
    
    public String getValorString(double d){
    	
    	double nd = Math.round(d * 100.0) / 100.0;
    	DecimalFormat df = new DecimalFormat("###,###,###,###.##");
    	String s = df.format(nd);
    	return s;
    	
    }
	
	@SuppressWarnings("static-access")
	public boolean possuiDinheiro(OfflinePlayer dono, double d){
		double dinheiro = this.plugin.econ.getBalance(dono);
		if(dinheiro >= d){
			return true;
		}else return false;
	}
	
	@SuppressWarnings("static-access")
	public void adicionarDinheiro(OfflinePlayer dono, double d){
		this.plugin.econ.depositPlayer(dono, d);
	}
	
	@SuppressWarnings("static-access")
	public void retirarDinheiro(OfflinePlayer p, double d){
		this.plugin.econ.withdrawPlayer(p, d);
	}
	
	@SuppressWarnings("static-access")
	public double dinheiro(OfflinePlayer p){
		return this.plugin.econ.getBalance(p);
	}
	
	
}

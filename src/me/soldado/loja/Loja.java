package me.soldado.loja;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

public class Loja {
	
	OfflinePlayer dono;
	Location loc;
	ItemStack item;
	int compra;
	int venda;
	int quantiade;
	boolean adm;
	
	String mundo;
	
	public Loja(OfflinePlayer dono, Location loc, ItemStack item, int compra, int venda, int quantidade, boolean adm){
		
		this.dono = dono;
		this.loc = loc;
		this.item = item;
		this.compra = compra;
		this.venda = venda;
		this.quantiade = quantidade;
		this.adm = adm;
		this.mundo = loc.getWorld().getName();
		
	}

	public OfflinePlayer getDono() {
		return dono;
	}

	public void setP(Player dono) {
		this.dono = dono;
	}

	public Location getLoc() {
		return loc;
	}

	public String getMundo() {
		return loc.getWorld().getName();
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public int getCompra() {
		return compra;
	}

	public void setCompra(int compra) {
		this.compra = compra;
	}

	public int getVenda() {
		return venda;
	}

	public void setVenda(int venda) {
		this.venda = venda;
	}

	public int getQuantiade() {
		return quantiade;
	}

	public void setQuantiade(int quantiadde) {
		this.quantiade = quantiadde;
	}

	public boolean isAdm() {
		return adm;
	}

	public void setAdm(boolean adm) {
		this.adm = adm;
	}
	
	public boolean soCompra(){
		if(this.venda == 0){
			return true;
		}else return false;
	}
	
	public boolean soVende(){
		if(this.compra == 0){
			return true;
		}else return false;
	}
	
	public Chest getBau(){
		
		Block b = this.loc.getBlock();
		Sign placa = (Sign) b.getState().getData();
		Block blocobau = b.getRelative(placa.getAttachedFace());
		if(blocobau.getType().equals(Material.CHEST) || blocobau.getType().equals(Material.TRAPPED_CHEST)){
			Chest c = (Chest) blocobau.getState();
			return c;
		}else return null;
		
	}
	
	public String getString(){
		
		String data = "";
		if(item.getData().getData() > 0){
			data += item.getData().getData()+"";
		}
		String n = dono.getName();
		String l = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + this.mundo;
		String i = item.getType().getId()+"";
		if(data != "") i += ":" + data;
		String c = compra + "";
		String v = venda + "";
		String q = quantiade + "";
		String a = adm + "";

		String s = n + ";" + l + ";" + i + ";" + c + ";" + v + ";" + q  + ";" + a;
		return s;
	}

}

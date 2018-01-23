package me.soldado.loja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	public static File msgfile;
	public static FileConfiguration msg;

	public static File cfgfile;
	public static FileConfiguration cfg;

	public static File bdfile;
	public static FileConfiguration bd;

	public static Economy econ = null;
	
	//Decompilou o plugin e tirou o allatori por que?
	//Não roube o plugin cara, isso é feio :(

	LojaChecagem chec;
	LojaCore core;
	LojaCriar criar;
	LojaGUI gui;
	LojaMecanica mec;
	LojaSegurança seg;
	
	public void onEnable(){

		iniciarConfig();
		
		core = new LojaCore(this);
		carregarLojas();
		chec = new LojaChecagem(this);
		criar = new LojaCriar(this);
		gui = new LojaGUI(this);
		mec = new LojaMecanica(this);
		seg = new LojaSegurança(this);
		
		this.getLogger().info("Plugin habilitado!MANO");
		this.getLogger().info("Autor: Soldado_08");
		
		//Entrou aqui para ver a chave né?
		//Vai, rouba aí, fazer o que né =/
		if(!cfg.getString("Chave").equals("sce6db6db8893119c7a6afffeb3dc015cs")){
			if(!autenticar()){
				this.getLogger().info("Plugin feito somente para o servidor SmiteCraft.");
				this.getLogger().info("Esse plugin não pode ser usado em outros servidores.");
				this.getLogger().info("Se você acredita que isso é um erro, contate o autor do plugin");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		
		Bukkit.getServer().getPluginManager()
		.registerEvents(criar, this);

		Bukkit.getServer().getPluginManager()
		.registerEvents(mec, this);

		Bukkit.getServer().getPluginManager()
		.registerEvents(chec, this);

		Bukkit.getServer().getPluginManager()
		.registerEvents(seg, this);

		Bukkit.getServer().getPluginManager()
		.registerEvents(gui, this);

		this.getCommand("lojagui").setExecutor(new Comandos(this));

		NomeItens.carregarNomes();

		if (!iniciarEconomia()){
			getLogger().severe(
					String.format("[%s] - Desabilitado por falta do plugin Vault!", 
							new Object[] { getDescription().getName() }));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

	}

	public void onDisable(){
		this.getLogger().info("Plugin desabilitado!");
		this.getLogger().info("Autor: Soldado_08");

		salvarLojas();
	}
	
	private boolean autenticar(){
		
		String s = null;
		try {
			s = getHTML("https://api.ipify.org/");
		} catch (Exception e1) {
			e1.printStackTrace();
			return true;
		}
		
		InetAddress address = null;
		try {
			address = InetAddress.getByName("jogar.smitecraft.com.br");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return true;
		}
		
		if(s.equals(address.getHostAddress())){
			return true;
		}else return false;
		
	}
	
	public static String getHTML(String urlToRead) throws Exception {
	      StringBuilder result = new StringBuilder();
	      URL url = new URL(urlToRead);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
	   }

	public void iniciarConfig(){

		if (msgfile == null) {
			msgfile = new File(getDataFolder(), "mensagens.yml");
		}
		if (!msgfile.exists()) {
			saveResource("mensagens.yml", false);
		}
		msg = YamlConfiguration.loadConfiguration(msgfile);

		if (cfgfile == null) {
			cfgfile = new File(getDataFolder(), "config.yml");
		}
		if (!cfgfile.exists()) {
			saveResource("config.yml", false);
		}
		cfg = YamlConfiguration.loadConfiguration(cfgfile);

		if (bdfile == null) {
			bdfile = new File(getDataFolder(), "lojas.dat");
		}
		if (!bdfile.exists()) {
			saveResource("lojas.dat", false);
		}
		bd = YamlConfiguration.loadConfiguration(bdfile);

	}
	public void carregarLojas(){

		if(bd.getStringList("lojas") != null){
			
			List<String> s = bd.getStringList("lojas");
			
			for(String str : s){
				core.recarregarLoja(str);
			}
		}
	}

	public void salvarLojas(){

		ArrayList<Loja> lojas = new ArrayList<Loja>(core.lojas.values());
		
		if(bd.getStringList("lojas") != null){
			
			List<String> s = bd.getStringList("lojas");
			s.clear();

			for(Loja l : lojas){
				if(l != null){
					String str = l.getString();
					s.add(str);
					this.getLogger().info("LOJA DE " + l.getDono().getName() + " FOI SALVA COM SUCESSO!!!");
				}else this.getLogger().info("LOJA NULA!!!!!!!!!!!!!!");
			}

			bd.set("lojas", s);
			try {
				bd.save(bdfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean iniciarEconomia(){
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		this.econ = ((Economy)rsp.getProvider());
		return this.econ != null;
	}

}

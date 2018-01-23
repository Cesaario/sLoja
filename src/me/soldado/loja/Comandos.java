package me.soldado.loja;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comandos implements CommandExecutor {

	public Main plugin;

	public Comandos(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("lojagui")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(p.isOp() || p.hasPermission("loja.adm")){
				if(plugin.core.lojas == null){
					p.sendMessage("§cSem lojas definidas");
				}else
					plugin.gui.abrirGUI(p, 1);
				}
			}
			return true;
		}
		return false;
	}

}

package pdx.nekkoya48.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pdx.nekkoya48.event.PDXEvent;
import pdx.nekkoya48.farming.Farmer;
import pdx.nekkoya48.fishing.Fisher;
import pdx.nekkoya48.mining.VoidQuarry;
import pdx.nekkoya48.multithread.MachineThread;

public class PDXNekkoya extends JavaPlugin {

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new PDXEvent(), this);
		new Farmer(new Location(Bukkit.getWorlds().get(0), 0, 0, 0)).register();
		new VoidQuarry(new Location(Bukkit.getWorlds().get(0), 0, 0, 0)).register();
		new Fisher(new Location(Bukkit.getWorlds().get(0), 0, 0, 0)).register();
		new MachineThread().start();
	}
	
	public void onDisable() {
		MachineThread.isRunning = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("nekkoya")) {
			p.getInventory().addItem(new Fisher(p.getLocation()).getItem());
			p.getInventory().addItem(new Farmer(p.getLocation()).getItem());
			p.getInventory().addItem(new VoidQuarry(p.getLocation()).getItem());
		}
		
		return true;
	}
	
}

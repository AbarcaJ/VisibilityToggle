package dev.cleusgamer201.visibilitytoggle;

import static dev.cleusgamer201.visibilitytoggle.Main.getPrefix;
import static dev.cleusgamer201.visibilitytoggle.Utils.color;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("all")
public class Commands implements CommandExecutor {

	private final Main plugin;
	public Commands(Main plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender Sender, Command Cmd, String Label, String[] Args) {
		if (!Sender.hasPermission("visibilitytoggle.admin")) {
			Sender.sendMessage(getPrefix() + color("&cNo Permission!"));
			return true;
		}
		
		if (Args.length < 1) {
			Help(Sender);
			return true;
		}
		
		if (Args[0].equalsIgnoreCase("Help")) {
			Help(Sender);
			return true;
        }
		
		if (Args[0].equalsIgnoreCase("Load")) {
			if (Args.length > 1) {
				try {
					Player t = Bukkit.getPlayerExact(Args[1]);
					if (t.isOnline()) {
						plugin.updateVisibility(t);
						Sender.sendMessage(getPrefix() + color("&7" + t.getName() + "&b Visibility &aLoaded!"));
						return true;
					}
					Sender.sendMessage(getPrefix() + color("&cThe player is not online!"));
				} catch (Exception ex) {
					Sender.sendMessage(getPrefix() + color("&cThe player is not online!"));
				}
			} else {
				Sender.sendMessage(getPrefix() + color("&cError please specify the player!"));
			}
			return true;
        }
		
		if (Args[0].equalsIgnoreCase("Toggle")) {
			if (Args.length > 1) {
				try {
					Player t = Bukkit.getPlayerExact(Args[1]);
					if (t.isOnline()) {
						plugin.toggleVisibility(t);
						Sender.sendMessage(getPrefix() + color("&7" + t.getName() + "&b Visibility &aToggled!"));
						return true;
					}
					Sender.sendMessage(getPrefix() + color("&cThe player is not online!"));
				} catch (Exception ex) {
					Sender.sendMessage(getPrefix() + color("&cThe player is not online!"));
				}
			} else {
				Sender.sendMessage(getPrefix() + color("&cError please specify the player!"));
			}
			return true;
        }
		
		if (Args[0].equalsIgnoreCase("Reload")) {
			if (plugin.isToggleItem()) {
				int slot = plugin.getItemSlot();
				for (Player all : Bukkit.getOnlinePlayers()) {
					ItemStack item = all.getInventory().getItem(slot);
					if (item != null && (item.isSimilar(plugin.getOnItem()) || item.isSimilar(plugin.getRankItem())) || item.isSimilar(plugin.getOffItem())) {
		    			all.getInventory().clear(slot);
		    		}
					
				}
			}
			plugin.getConfig().reload();
			plugin.loadConfigValues();
			Bukkit.getOnlinePlayers().forEach(plugin::updateVisibility);
			Sender.sendMessage(getPrefix() + color("&6Configuration reload and visibility refreshed for all players."));
			return true;
        }
        
        Sender.sendMessage(getPrefix() + color("&cArgument Invalid!"));
        Bukkit.dispatchCommand(Sender, "Vt");
        return true;
	}
	
	public void Help(CommandSender Sender) {
		Sender.sendMessage(color("&8&m-&f&m-&8&m-&f&m-&8&m-&f&m-&r &bVisibilityToggle &f&m-&8&m-&f&m-&8&m-&f&m-&8&m-&r"));
		Sender.sendMessage("        ");
		Sender.sendMessage(color("&a /Vt Load <player>               "));
		Sender.sendMessage(color("&a /Vt Toggle <player>                 "));
		Sender.sendMessage(color("&a /Vt Reload                      "));
		Sender.sendMessage("        ");
		Sender.sendMessage(color("&8&m-&f&m-&8&m-&f&m-&8&m-&f&m-&r &bVisibilityToggle &f&m-&8&m-&f&m-&8&m-&f&m-&8&m-&r"));
	}


}

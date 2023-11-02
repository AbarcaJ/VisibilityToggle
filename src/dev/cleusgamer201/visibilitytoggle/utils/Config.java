package dev.cleusgamer201.visibilitytoggle.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import dev.cleusgamer201.visibilitytoggle.Main;
import dev.cleusgamer201.visibilitytoggle.Utils;

public class Config extends YamlConfiguration {

    private final File file;
    private final String path;
	
	public Config(String path) {
		this.path = (path + ".yml");
		this.file = new File(Main.getInstance().getDataFolder(), this.path);
		saveDefault();
		reload();
	}

	public void reload() {
    	try {
    		 super.load(file);
    	} catch (InvalidConfigurationException | IOException ex) {
    		Utils.log("&cReloadConfig Path: &e" + path + " &cError Ex>> &f" + ex);
    	}
	}

	public void save() {
		try {
   		 	super.save(file);
		} catch (IOException ex) {
			Utils.log("&cSave Path: &e" + path + " &cError Ex>> &f" + ex);
		}
	}

	public void saveDefault() {
		try {
			if (!file.exists()) {
				if(Main.getInstance().getResource(path) != null) {
					Main.getInstance().saveResource(path, false);
				} else {
					file.createNewFile();
				}
		    }
		} catch (Exception ex) {
			Utils.log("&cSaveDefault Path: &e" + path + " &cError Ex>> &f" + ex);
		}
	}
}

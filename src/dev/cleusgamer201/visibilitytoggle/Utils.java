package dev.cleusgamer201.visibilitytoggle;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Utils {
	
	public static String color(String Text) {
		Text = Text.replaceAll("<1>", "✖");
        Text = Text.replaceAll("<2>", "✔");
        Text = Text.replaceAll("<3>", "●");
        Text = Text.replaceAll("<4>", "♥");
        Text = Text.replaceAll("<5>", "♦");
        Text = Text.replaceAll("<6>", "★");
        Text = Text.replaceAll("<7>", "►");
        Text = Text.replaceAll("<8>", "◄");
        Text = Text.replaceAll("<9>", "✪");
        Text = Text.replaceAll("<10>", "☠");
        Text = Text.replaceAll("<11>", "░");
        Text = Text.replaceAll("<12>", "■");
        Text = Text.replaceAll("<13>", "»");
        Text = Text.replaceAll("<14>", "«");
        Text = Text.replaceAll("<a>", "á");
        Text = Text.replaceAll("<e>", "é");
        Text = Text.replaceAll("<i>", "í");
        Text = Text.replaceAll("<o>", "ó");
        Text = Text.replaceAll("<u>", "ú");
    	Text = Text.replaceAll("<u>", "ú");
    	Text = Text.replaceAll("<A>", "Á");
    	Text = Text.replaceAll("<E>", "É");
    	Text = Text.replaceAll("<I>", "Í");
    	Text = Text.replaceAll("<O>", "Ó");
    	Text = Text.replaceAll("<U>", "Ú");
        return ChatColor.translateAlternateColorCodes('&', Text);
    }
	
	public static List<String> color(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			lines.set(i, color(lines.get(i)));
		}
		return lines;
	}

    public static void log(String Text) {
        Bukkit.getConsoleSender().sendMessage(Main.getPrefix() + color(Text));
    }
}


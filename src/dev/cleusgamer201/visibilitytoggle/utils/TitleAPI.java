package dev.cleusgamer201.visibilitytoggle.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleAPI {
	
    public static void sendTitle(Player player, String title) {
        try {
            title = ChatColor.translateAlternateColorCodes('&', title);
            Object packetTitle = getMcClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            Object objectTitle = getMcClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
            Constructor<?> cTitle = getMcClass("PacketPlayOutTitle").getConstructor(getMcClass("PacketPlayOutTitle").getDeclaredClasses()[0], getMcClass("IChatBaseComponent"));
            sendPacket(player, cTitle.newInstance(packetTitle, objectTitle));
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException ignored) {
        }
    }
    
    public static void sendSubTitle(Player player, String subtitle) {
        try {
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            Object packetTitle = getMcClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Object objectTitle = getMcClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
            Constructor<?> cTitle = getMcClass("PacketPlayOutTitle").getConstructor(getMcClass("PacketPlayOutTitle").getDeclaredClasses()[0], getMcClass("IChatBaseComponent"));
            sendPacket(player, cTitle.newInstance(packetTitle, objectTitle));
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException ignored) {
        }
    }
    
    public static void clearTitle(Player player) {
        sendTitle(player, "");
        sendSubTitle(player, "");
    }
    
    private static Class<?> getMcClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
    
    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getMcClass("Packet")).invoke(playerConnection, packet);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException ignored) {
        }
    }
}
package dev.cleusgamer201.visibilitytoggle.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.cleusgamer201.visibilitytoggle.Main;
import dev.cleusgamer201.visibilitytoggle.Utils;
import dev.cleusgamer201.visibilitytoggle.api.Visibility;

public class Cache {

    private final Player player;
    private double lastUse = 0;
    private Visibility state = Visibility.SHOW_ALL;
    private boolean loaded = false;

    public Cache(Player player) {
        this.player = player;
        final String uuid = getUuid().toString();
        final String name = getName();
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            try {
                final ResultSet rs = Main.getInstance().getDbManager().executeQuery("SELECT * FROM VisibilityToggle WHERE Uuid = ?;", uuid);
                if (rs.next()) {
                    this.state = Visibility.valueOf(rs.getString("Visibility").toUpperCase());
                    rs.close();
                } else {
                    rs.close();
                    Main.getInstance().getDbManager().executeUpdate("INSERT INTO VisibilityToggle (Uuid, Visibility) VALUES (?, ?);", uuid, state.name());
                }
                this.loaded = true;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> Bukkit.getPluginManager().callEvent(new CacheLoadEvent(this, player)));
            } catch (SQLException ex) {
                Utils.log("&4WARNING: &7" + name + "&c get state/insert error: &f" + ex);
            }
        }, 3L);
    }

    public Player getPlayer() {
        return player;
    }

    public void setVisbility(Visibility value) {
        this.state = value;
    }

    public Visibility getVisibility() {
        return this.state;
    }

    public void setLastUse(double delay) {
        this.lastUse = (TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) / 1000.0) + delay;
    }

    public double getLastUse() {
        return lastUse;
    }

    public UUID getUuid() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public boolean isLoaded() {
        return loaded;
    }
}

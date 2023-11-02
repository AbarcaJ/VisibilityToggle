package dev.cleusgamer201.visibilitytoggle.database;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.cleusgamer201.visibilitytoggle.Main;
import dev.cleusgamer201.visibilitytoggle.Utils;
import dev.cleusgamer201.visibilitytoggle.utils.Config;

public class DBManager implements Listener {

    private boolean shutdown = false;
    private final HashMap<Player, Cache> cached = new HashMap<>();
    private DB db;

    private final Main plugin;

    public DBManager(Main plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        final DBSettings.Builder builder = new DBSettings.Builder();
        final Config config = plugin.getConfig();
        if (config.getBoolean("MySQL.Enabled")) {
            builder.host(config.getString("MySQL.Host"));
            builder.port(config.getInt("MySQL.Port"));
            builder.user(config.getString("MySQL.Username"));
            builder.database(config.getString("MySQL.Database"));
            builder.password(config.getString("MySQL.Password"));
            builder.type(DBSettings.DBType.MYSQL);
        } else {
            builder.database("Database");
        }
        db = new DB(builder.build());
        if (db.isConnected()) {
            db.executeUpdate("CREATE TABLE IF NOT EXISTS VisibilityToggle (Uuid VARCHAR(36), Visibility VARCHAR(10));");
        } else {
            Utils.log("&cNo database connection detected disabling...");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void shutdown() {
        this.shutdown = true;
        if (db.isConnected()) {
            ExecutorService scheduler = Executors.newCachedThreadPool();
            for (List<Cache> data : splitCache(6)) {
                scheduler.execute(() -> data.forEach(DBManager.this::save));
            }
            scheduler.shutdown();
        }
    }

    public DB getDB() {
        return db;
    }

    public void executeUpdate(String query) {
        db.executeUpdate(query);
    }

    public ResultSet executeQuery(String query) {
        return db.executeQuery(query);
    }

    public void executeUpdate(String query, Object... values) {
        db.executeUpdate(query, values);
    }

    public ResultSet executeQuery(String query, Object... values) {
        return db.executeQuery(query, values);
    }

    public void makeCache(Player player) {
        if (!cached.containsKey(player)) {
            cached.put(player, new Cache(player));
        }
    }

    public Cache getCache(Player player) {
        makeCache(player);
        return cached.get(player);
    }

    public void removeCache(Player player) {
        cached.remove(player);
    }

    public void saveAsync() {
        cached.values().forEach(this::saveAsync);
    }

    public void save() {
        cached.values().forEach(this::save);
    }

    public Collection<List<Cache>> splitCache(int split) {
        AtomicInteger counter = new AtomicInteger(0);
        return cached.values().stream().collect(Collectors.partitioningBy(e -> counter.getAndIncrement() < cached.size() / split, Collectors.toList())).values();
    }

    public void saveAsync(Cache cache) {
        if (!cache.isLoaded()) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> db.executeUpdate("UPDATE VisibilityToggle SET Visibility = ? WHERE Uuid = ?;", cache.getVisibility().name(), cache.getUuid().toString()));
    }

    public void save(Cache cache) {
        if (!cache.isLoaded()) {
            return;
        }
        db.executeUpdate("UPDATE VisibilityToggle SET Visibility = ? WHERE Uuid = ?;", cache.getVisibility().name(), cache.getUuid().toString());
    }

    public HashMap<Player, Cache> getAll() {
        return cached;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        makeCache(p);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        if (!this.shutdown) {
            final Cache cache = getCache(player);
            if (cache.isLoaded()) {
                saveAsync(cache);
            }
            removeCache(player);
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new CacheUnloadEvent(cache, player)));
        }
    }
}

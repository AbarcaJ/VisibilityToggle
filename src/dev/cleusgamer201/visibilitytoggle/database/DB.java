package dev.cleusgamer201.visibilitytoggle.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import dev.cleusgamer201.visibilitytoggle.Main;
import dev.cleusgamer201.visibilitytoggle.Utils;
import dev.cleusgamer201.visibilitytoggle.database.DBSettings.DBType;

public class DB {

    private final DBSettings settings;
    private Connection con;

    public DB(DBSettings settings) {
        this.settings = settings;
        this.setup();
    }

    private void setup() {
        try {
            DBType type = settings.type();
            if (type == DBType.MYSQL) {
                Class.forName(type.getDriver());
                con = DriverManager.getConnection("jdbc:mysql://" + settings.host() + ":" + settings.port() + "/" + settings.database() + "?autoReconnect=true", settings.user(), settings.password());
            } else {
                Class.forName(type.getDriver());
                con = DriverManager.getConnection("jdbc:sqlite:" + Main.getInstance().getDataFolder() + "/" + settings.database() + ".db");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Utils.log("&4Error to establish connection with the database &cDisabling...");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    public Connection getConnection() {
        try {
            if (!isConnected()) {
                Utils.log("&cError the connection is closed reconnecting...");
                setup();
            }
            if (con != null && !con.isClosed()) {
                return con;
            }
        } catch (SQLException ex) {
            Utils.log("&cError getConnection() exception: &f" + ex);
        }
        return null;
    }

    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException ex) {
            Utils.log("&cError isConnected() exception: &f" + ex);
        }
        return false;
    }

    public void close() {
        if (isConnected()) {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Utils.log("&cError to close the connection: &f" + ex);
            }
        }
    }

    public void executeUpdate(String query) {
        try {
            getConnection().createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Utils.log("&cError @executeUpdate() exception: &f" + ex);
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            return getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            Utils.log("&cError @executeQuery() exception: &f" + ex);
        }
        return null;
    }

    public void executeUpdate(String query, Object... values) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            int count = 1;
            for (Object obj : values) {
                ps.setObject(count, obj);
                count++;
            }
            ps.execute();
        } catch (SQLException ex) {
            Utils.log("&cError executeUpdate() exception: &f" + ex);
        }
    }

    public ResultSet executeQuery(String query, Object... values) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            int count = 1;
            for (Object obj : values) {
                ps.setObject(count, obj);
                count++;
            }
            return ps.executeQuery();
        } catch (SQLException ex) {
            Utils.log("&cError executeQuery() exception: &f" + ex);
        }
        return null;
    }
}

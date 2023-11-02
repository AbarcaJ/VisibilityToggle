package dev.cleusgamer201.visibilitytoggle.database;

public class DBSettings {

    enum DBType {

        MYSQL("com.mysql.jdbc.Driver"), SQLITE("org.sqlite.JDBC");

        private final String driver;

        DBType(String driver) {
            this.driver = driver;
        }

        public String getDriver() {
            return driver;
        }
    }

    private String host = "";
    private int port = 3306;
    private String user = "";
    private String password = "";
    private String database = "Database";
    private DBType type = DBType.SQLITE;

    private DBSettings() {
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    public String database() {
        return database;
    }

    public DBType type() {
        return type;
    }

    public static class Builder {

        private final DBSettings settings = new DBSettings();

        public Builder host(String host) {
            settings.host = host;
            return this;
        }

        public Builder port(int port) {
            settings.port = port;
            return this;
        }


        public Builder user(String user) {
            settings.user = user;
            return this;
        }

        public Builder password(String password) {
            settings.password = password;
            return this;
        }

        public Builder database(String database) {
            settings.database = database;
            return this;
        }

        public Builder type(DBType type) {
            settings.type = type;
            return this;
        }

        public DBSettings build() {
            return settings;
        }
    }
}

package dev.jobrapati.discordlinkrolesync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Database instance;
    private Connection connection;
    final String username = DiscordLinkRoleSync.config.getString("databaseUsername");
    final String password = DiscordLinkRoleSync.config.getString("databasePassword");
    final String url = DiscordLinkRoleSync.config.getString("databaseUrl");

    private Database() throws SQLException
    {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public Connection getConnection()
    {
        return connection;
    }

    public static Database getInstance() throws SQLException
    {
        if(instance == null)
            instance = new Database();
        else if(instance.getConnection().isClosed())
            instance = new Database();

        return instance;
    }
}

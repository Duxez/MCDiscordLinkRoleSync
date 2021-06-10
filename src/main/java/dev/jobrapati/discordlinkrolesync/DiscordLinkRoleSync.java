package dev.jobrapati.discordlinkrolesync;

import dev.jobrapati.discordlinkrolesync.commands.LinkCommand;
import dev.jobrapati.discordlinkrolesync.commands.ForceSync;
import dev.jobrapati.discordlinkrolesync.listeners.DiscordListener;
import dev.jobrapati.discordlinkrolesync.listeners.PlayerEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class DiscordLinkRoleSync extends JavaPlugin {

    private static Permission perms = null;
    public static JDA api;
    public static Server server;
    public static FileConfiguration config;
    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        config = this.getConfig();
        config.addDefault("botToken", "");
        config.addDefault("databaseUrl", "");
        config.addDefault("databaseUsername", "");
        config.addDefault("databasePassword", "");
        config.addDefault("rolesAndGroups", "");
        config.addDefault("discordGuildId", "");
        config.options().copyDefaults(true);
        saveConfig();
        if(config.getString("botToken").isEmpty() || config.getString("databaseUrl").isEmpty() || config.getString("databaseUsername").isEmpty() || config.getString("databasePassword").isEmpty())
        {
            getLogger().log(Level.CONFIG, "Some values in config.yml may not be set. Please make sure the values are filled and restart the server.");
            return;
        }
        server = getServer();

        try {
            api = JDABuilder.createDefault(config.getString("botToken"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();
        } catch (LoginException e) {
            getLogger().log(Level.SEVERE, e, null);
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Links(McUser varchar(100) NOT NULL, DiscordId varchar(100) NULL, LinkCode varchar(4) NULL)";
            PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
            statement.execute();
            Database.getInstance().getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        this.getCommand("link").setExecutor(new LinkCommand());
        this.getCommand("ForceSync").setExecutor(new ForceSync());

        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        api.addEventListener(new DiscordListener());
        setupPermissions();
    }

    @Override
    public void onDisable()
    {
        //TODO: Close everything
    }


    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission GetPermissions() {
        return perms;
    }
}

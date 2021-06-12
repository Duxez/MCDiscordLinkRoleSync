package dev.jobrapati.discordlinkrolesync;

import dev.jobrapati.discordlinkrolesync.commands.LinkCommand;
import dev.jobrapati.discordlinkrolesync.commands.ForceSync;
import dev.jobrapati.discordlinkrolesync.helpers.DiscordHelper;
import dev.jobrapati.discordlinkrolesync.helpers.EmbedBuild;
import dev.jobrapati.discordlinkrolesync.listeners.DiscordListener;
import dev.jobrapati.discordlinkrolesync.listeners.PlayerEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class DiscordLinkRoleSync extends JavaPlugin {

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
        config.addDefault("discordChannelId", "");
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        this.getCommand("link").setExecutor(new LinkCommand());
        this.getCommand("ForceSync").setExecutor(new ForceSync());

        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        api.addEventListener(new DiscordListener());

        server.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            int currentPlayers = server.getOnlinePlayers().size();
            int maxPlayers = server.getMaxPlayers();

            api.getPresence().setPresence(Activity.playing(currentPlayers + "/" + maxPlayers + " online."), false);
        }, 20, 100);
    }

    @Override
    public void onDisable()
    {
        MessageEmbed embed = EmbedBuild.CreateEmbedAuthorValuesOnly(null, "Server Stopping", Color.RED);
        DiscordHelper.SendEmbedToDiscordChannel(embed);
        api.shutdown();
        try {
            Database.getInstance().getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}

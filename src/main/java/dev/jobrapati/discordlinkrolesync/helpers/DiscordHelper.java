package dev.jobrapati.discordlinkrolesync.helpers;

import dev.jobrapati.discordlinkrolesync.DiscordLinkRoleSync;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class DiscordHelper {

    public static void SendEmbedToDiscordChannel(MessageEmbed embed) {
        JDA api = DiscordLinkRoleSync.api;
        FileConfiguration config = DiscordLinkRoleSync.config;

        String channelId = config.getString("discordChannelId");
        Guild guild = api.getGuildById(config.getString("discordGuildId"));
        TextChannel channel = guild.getTextChannelById(channelId);
        channel.sendMessage(embed).queue();
    }
}

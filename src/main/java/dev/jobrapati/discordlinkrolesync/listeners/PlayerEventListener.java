package dev.jobrapati.discordlinkrolesync.listeners;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.jobrapati.discordlinkrolesync.DiscordLinkRoleSync;
import dev.jobrapati.discordlinkrolesync.helpers.DiscordHelper;
import dev.jobrapati.discordlinkrolesync.helpers.EmbedBuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        final TextComponent joinMessage = Component.text("Welcome ")
                .color(TextColor.color(106, 42, 145))
                .append(event.getPlayer().displayName());
        event.joinMessage(joinMessage);

        MessageEmbed message = EmbedBuild.CreateEmbedAuthorValuesOnly(String.format("https://crafatar.com/avatars/%s", player.getUniqueId()), player.getName() + " joined", Color.GREEN);

        DiscordHelper.SendEmbedToDiscordChannel(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MessageEmbed message = EmbedBuild.CreateEmbedAuthorValuesOnly(String.format("https://crafatar.com/avatars/%s", player.getUniqueId()), player.getName() + " left", Color.RED);

        DiscordHelper.SendEmbedToDiscordChannel(message);
    }
}

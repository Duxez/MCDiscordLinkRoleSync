package dev.jobrapati.discordlinkrolesync.listeners;

import dev.jobrapati.discordlinkrolesync.Database;
import dev.jobrapati.discordlinkrolesync.helpers.SyncRole;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import dev.jobrapati.discordlinkrolesync.DiscordLinkRoleSync;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        DiscordLinkRoleSync.server.sendMessage(Component.text(content));
        try {
            String getUser = String.format("SELECT McUser FROM Links WHERE LinkCode = '%s'", content);
            PreparedStatement select = Database.getInstance().getConnection().prepareStatement(getUser);
            ResultSet set = select.executeQuery();
            UUID uuid;
            if(set.next()) {
                uuid = UUID.fromString(set.getString("McUser"));
            }
            else {
                message.getChannel().sendMessage("Couldn't find your Minecraft user");
                return;
            }
            String sql = String.format("UPDATE Links SET DiscordId = '%s', LinkCode = NULL WHERE LinkCode = '%s'", event.getAuthor().getId(), content);
            PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
            statement.executeUpdate();

            Player player = DiscordLinkRoleSync.server.getPlayer(uuid);

            player.sendMessage(Component.text(String.format("Your account is now connected to the Discord account with the name %s", event.getAuthor().getName())));
            SyncRole.SyncRoleForPlayer(player, Long.parseLong(event.getAuthor().getId()));
            Database.getInstance().getConnection().close();
        } catch (SQLException throwables) {
            DiscordLinkRoleSync.server.sendMessage(Component.text(throwables.getMessage()));
        }
    }
}

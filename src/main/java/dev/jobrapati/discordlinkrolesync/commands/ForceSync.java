package dev.jobrapati.discordlinkrolesync.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.jobrapati.discordlinkrolesync.Database;
import dev.jobrapati.discordlinkrolesync.DiscordLinkRoleSync;
import dev.jobrapati.discordlinkrolesync.helpers.SyncRole;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForceSync implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            try
            {
                String sql = "SELECT DiscordId FROM Links WHERE McUser=?";
                PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, String.valueOf(player.getUniqueId()));
                ResultSet set = statement.executeQuery();
                if(set.next())
                {
                    String id = set.getString("DiscordId");
                    SyncRole.SyncRoleForPlayer(player, Long.parseLong(id));
                }
                else
                {
                    player.sendMessage("You don't have a discord account linked, use /link to link your account");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return true;
    }
}

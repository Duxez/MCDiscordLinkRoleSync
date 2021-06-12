package dev.jobrapati.discordlinkrolesync.helpers;

import dev.jobrapati.discordlinkrolesync.DiscordLinkRoleSync;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncRole {

    public static void SyncRoleForPlayer(Player player, long discordUserId)
    {
        final long guildId = DiscordLinkRoleSync.config.getLong("discordGuildId");
        player.sendMessage("" + guildId);
        final Guild guild = DiscordLinkRoleSync.api.getGuildById(guildId);
        final Member guildMember = guild != null ? guild.getMemberById(discordUserId) : null;
        player.sendMessage((guildMember == null) + "");
        final List<Role> roles = guildMember != null ? guildMember.getRoles() : null;
        ArrayList<String> roleIds = new ArrayList<>();
        
        if(roles == null) {
            player.sendMessage("Player does not have roles to sync.");
            return;
        }
        
        for(int i = 0; i < roles.size(); i++)
        {
            player.sendMessage(roles.get(i).getName());
            roleIds.add(roles.get(i).getId());
        }

        String admin = DiscordLinkRoleSync.config.getString("rolesAndGroups.admin");
        String mod = DiscordLinkRoleSync.config.getString("rolesAndGroups.mod");
        List<String> member = (List<String>) DiscordLinkRoleSync.config.getList("rolesAndGroups.member");
        for(String item : roleIds) {
            if(item.equals(admin)) {
                String command = String.format("lp user %s parent add admin", player.getName());
                DiscordLinkRoleSync.server.dispatchCommand(DiscordLinkRoleSync.server.getConsoleSender(), command);
                break;
            }
            else if(item.equals(mod)) {
                String command = String.format("lp user %s parent add mod", player.getName());
                DiscordLinkRoleSync.server.dispatchCommand(DiscordLinkRoleSync.server.getConsoleSender(), command);
                break;
            }
            else if(member.contains(item)) {
                String command = String.format("lp user %s parent add admin", player.getName());
                DiscordLinkRoleSync.server.dispatchCommand(DiscordLinkRoleSync.server.getConsoleSender(), command);
                break;
            }
        }

        player.sendMessage("Roles synced");
    }
}

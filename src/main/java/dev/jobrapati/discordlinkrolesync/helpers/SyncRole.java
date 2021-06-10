package dev.jobrapati.discordlinkrolesync.helpers;

import dev.jobrapati.discordlinkrolesync.DiscordLinkRoleSync;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyncRole {

    public static void SyncRoleForPlayer(Player player, long discordUserId)
    {
        final long guildId = DiscordLinkRoleSync.config.getLong("discordGuildId");
        player.sendMessage("" + guildId);
        final Guild guild = DiscordLinkRoleSync.api.getGuildById(guildId);
        final Member member = guild != null ? guild.getMemberById(discordUserId) : null;
        player.sendMessage((member == null) + "");
        final List<Role> roles = member != null ? member.getRoles() : null;
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

        Map<String, Object> section = DiscordLinkRoleSync.config.getConfigurationSection("rolesAndGroups").getValues(false);
        for (int i = 0; i < section.size(); i++) {
            Object[] values = section.values().toArray();
            Object[] keys = section.keySet().toArray();
            if(keys[i].toString().equals("admin") && roleIds.contains(values[i].toString())) {
                DiscordLinkRoleSync.GetPermissions().playerAddGroup(player, "admin");
            }
            else if(keys[i].toString().equals("mod") && roleIds.contains(values[i].toString())) {
                DiscordLinkRoleSync.GetPermissions().playerAddGroup(player, "mod");
            }
            else if(section.values().toArray()[i].getClass().getSimpleName().equals("ArrayList"))
            {
                ArrayList memberValues = (ArrayList) section.values().toArray()[i];
                for(int j = 0; j < memberValues.size(); j++)
                {
                    if(roleIds.contains(memberValues.get(i).toString()))
                    {
                        DiscordLinkRoleSync.GetPermissions().playerAddGroup(player, "member");
                        break;
                    }
                }
            }
        }

        player.sendMessage("Roles synced");
    }
}

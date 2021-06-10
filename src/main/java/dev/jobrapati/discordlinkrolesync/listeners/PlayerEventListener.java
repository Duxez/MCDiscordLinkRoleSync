package dev.jobrapati.discordlinkrolesync.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final TextComponent joinMessage = Component.text("Welcome ")
                .color(TextColor.color(106, 42, 145))
                .append(event.getPlayer().displayName());
        event.joinMessage(joinMessage);
    }
}

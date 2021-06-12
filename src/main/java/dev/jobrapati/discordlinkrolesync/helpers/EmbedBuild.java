package dev.jobrapati.discordlinkrolesync.helpers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbedBuild {

    public static MessageEmbed CreateEmbedAuthorValuesOnly(String image, String value, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(value, null, image);
        eb.setColor(color);
        return eb.build();
    }
}

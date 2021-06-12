package dev.jobrapati.discordlinkrolesync.commands;

import dev.jobrapati.discordlinkrolesync.Database;
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
import java.util.Random;

public class LinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            //based on args do stuff
            String id = "";
            try {
                String existsCheck = "SELECT * FROM Links WHERE McUser = ?";
                PreparedStatement checkStatement = Database.getInstance().getConnection().prepareStatement(existsCheck);
                checkStatement.setString(1, String.valueOf(player.getUniqueId()));
                ResultSet set = checkStatement.executeQuery();

                if(set.next()) {
                    if(set.getString("LinkCode") == null)
                        player.sendMessage("You have already linked your account to a discord account");
                    else
                        player.sendMessage(String.format("You can link your account by messaging the bot your link code: %s", set.getString("LinkCode")));

                    return true;
                }

                boolean success = false;
                while(!success)
                {
                    String code = GenerateCode();
                    if(code != "bad")
                    {
                        id = code;
                        success = true;
                    }
                }
                String sql = "INSERT INTO Links (McUser, LinkCode) VALUES (?, ?)";
                PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, String.valueOf(player.getUniqueId()));
                statement.setString(2, id);
                statement.execute();
                player.sendMessage(String.format("You can link your account by DMing the Discord bot with code %s", id));
            } catch (Exception e) {
                player.sendMessage(e.getMessage() + " code: " + id);
            }
        }

        return true;
    }

    private String GenerateCode() throws SQLException {
        Random random = new Random();
        String id = String.format("%04d", random.nextInt(10000));
        String sql = "SELECT * FROM Links WHERE LinkCode=?";

        try {
            PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {
                return id;
            }
            else {
                return "bad";
            }

        } catch (SQLException e) {
            throw e;
        }
    }
}

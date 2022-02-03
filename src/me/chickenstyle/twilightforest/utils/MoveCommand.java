package me.chickenstyle.twilightforest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {

        try {
            World world = Bukkit.getWorld(args[0]);
            Player player = (Player) sender;
            Location loc = player.getLocation();
            loc.setWorld(world);
            player.teleport(loc);

        }  catch (Exception ignored) {}
        return false;
    }
}

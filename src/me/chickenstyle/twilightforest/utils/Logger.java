package me.chickenstyle.twilightforest.utils;

import org.bukkit.Bukkit;

public class Logger {

    private static String prefix = "&5Twilight&2Forest &7- &f";
    public static void log(String msg) {
        msg = Utils.color(prefix + msg);
        Bukkit.getConsoleSender().sendMessage(msg);
    }
}
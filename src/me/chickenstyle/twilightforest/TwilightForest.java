package me.chickenstyle.twilightforest;

import me.chickenstyle.twilightforest.events.PlayerEvents;
import me.chickenstyle.twilightforest.nms.NMSHandler;
import me.chickenstyle.twilightforest.utils.Logger;
import me.chickenstyle.twilightforest.utils.MoveCommand;
import me.chickenstyle.twilightforest.world.TFChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TwilightForest extends JavaPlugin {

    private NMSHandler nmsHandler;

    @Override
    public void onEnable() {



        if (!detectNMSVersion()) {
            Logger.log("&4This version of minecraft isn't supported!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        nmsHandler.registerBiomes();
        registerListeners();
        registerCommands();



        new BukkitRunnable() {

            @Override
            public void run() {
                if (Bukkit.getWorld("TwilightForest") == null) { //Load TwilightForest world
                    Logger.log("Generating Twilight Forest World");
                    WorldCreator creator = new WorldCreator("TwilightForest");
                    creator.generator(new TFChunkGenerator(TwilightForest.getPlugin(TwilightForest.class)));
                    getServer().createWorld(creator);
                    Logger.log("Finished Generating Twilight Forest World");
                }

            }
        }.runTaskLater(this,400);

        Logger.log("&aPlugin has been loaded!");
    }

    @Override
    public void onDisable() {

    }

    public NMSHandler getNmsHandler() {
        return nmsHandler;
    }

    private boolean detectNMSVersion() {

        String version;

        try {

            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
        version = version.substring(1);

        try {
            nmsHandler = (NMSHandler) Class.forName("me.chickenstyle.twilightforest.nms.Handler_" +version).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return false;
        }

        return true;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerEvents(this),this);
    }

    private void registerCommands() {
        getCommand("move").setExecutor(new MoveCommand());
    }

}


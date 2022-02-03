package me.chickenstyle.twilightforest.nms;

import me.chickenstyle.twilightforest.world.TFBiome;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public interface NMSHandler {

    void registerBiomes();

    boolean setCustomBiomeAt(TFBiome biome, Location l);

    boolean setBiomeInChunk(TFBiome biome, Chunk chunk);
}

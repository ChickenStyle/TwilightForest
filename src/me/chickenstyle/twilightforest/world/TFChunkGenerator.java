package me.chickenstyle.twilightforest.world;

import me.chickenstyle.twilightforest.TwilightForest;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TFChunkGenerator extends ChunkGenerator {

    private TwilightForest main;

    public TFChunkGenerator(TwilightForest main) {
        this.main = main;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
    	ArrayList<BlockPopulator> populator = new ArrayList<>();
    	return populator;
       
    }
    
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
    	SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        ChunkData chunk = createChunkData(world);
        generator.setScale(0.01D);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlock(x,60,z,Material.GRASS_BLOCK);

                for (int y = 1; y < 60; y++) {
                    chunk.setBlock(x,y,z,Material.STONE);
                }
                chunk.setBlock(x, 0, z, Material.BEDROCK);
            }
        }

        main.getNmsHandler().setBiomeInChunk(TFBiome.FIRE_SWAP,world.getChunkAt(chunkX,chunkZ));

        return chunk;
    }

}

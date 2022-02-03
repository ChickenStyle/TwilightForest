package me.chickenstyle.twilightforest.nms;


import com.mojang.serialization.Lifecycle;
import me.chickenstyle.twilightforest.utils.Logger;
import me.chickenstyle.twilightforest.world.BiomeBuilder;
import me.chickenstyle.twilightforest.world.TFBiome;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Handler_1_17_R1 implements NMSHandler {

	@Override
	public void registerBiomes() {
		Server server = Bukkit.getServer();
		CraftServer craftserver = (CraftServer) server;
		DedicatedServer dedicatedserver = craftserver.getServer();

		for (TFBiome biome : TFBiome.values()) {
			ResourceKey<BiomeBase> newKey = ResourceKey.a(IRegistry.aO, biome.getMinecraftKey());
			BiomeBase base = new BiomeBuilder().build(biome);
			IRegistryWritable<BiomeBase> rw = dedicatedserver.getCustomRegistry().b(IRegistry.aO);
			rw.a(newKey, base, Lifecycle.stable());
		}
		Logger.log("All biomes have been registered!");
	}

	@Override
	public boolean setBiomeInChunk(TFBiome biome, Chunk c) {

		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				for(int y = 0; y <= c.getWorld().getMaxHeight(); y++) {
					setCustomBiomeAt(biome,new Location(c.getWorld(),c.getX() * 16 + x, y, c.getZ() * 16 + z));
				}
			}
		}
		refreshChunksForAll(c);
		return true;
	}

	@Override
	public boolean setCustomBiomeAt(TFBiome biome, Location l) {
		Server server = Bukkit.getServer();
		CraftServer craftserver = (CraftServer) server;
		DedicatedServer dedicatedserver = craftserver.getServer();
		BiomeBase base;
		IRegistryWritable<BiomeBase> registrywritable = dedicatedserver.getCustomRegistry().b(IRegistry.aO);

		ResourceKey<BiomeBase> rkey = ResourceKey.a(IRegistry.aO, biome.getMinecraftKey());
		base = registrywritable.a(rkey);


		setBiome(l.getBlockX(), l.getBlockY(), l.getBlockZ(), ((CraftWorld)l.getWorld()).getHandle(), base);
		return true;
	}

	private void setBiome(int x, int y, int z, net.minecraft.world.level.World w, BiomeBase bb) {
		BlockPosition pos = new BlockPosition(x, 0, z);

		if (w.isLoaded(pos)) {

			net.minecraft.world.level.chunk.Chunk chunk = w.getChunkAtWorldCoords(pos);
			if (chunk != null) {

				chunk.getBiomeIndex().setBiome(x >> 2, y >> 2, z >> 2, bb);
				chunk.markDirty();
			}
		}
	}


	private void refreshChunksForAll(Chunk chunk) {
		net.minecraft.world.level.chunk.Chunk c = ((CraftChunk)chunk).getHandle();
		for (Player player : chunk.getWorld().getPlayers()) {
			if (player.isOnline()) {
				if((player.getLocation().distance(chunk.getBlock(0, 0, 0).getLocation()) < (Bukkit.getServer().getViewDistance() * 16))) {
					((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutMapChunk(c));
				}
			}
		}
	}


}

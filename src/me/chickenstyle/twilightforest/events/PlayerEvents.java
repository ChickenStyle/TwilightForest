package me.chickenstyle.twilightforest.events;

import me.chickenstyle.twilightforest.utils.Logger;
import me.chickenstyle.twilightforest.TwilightForest;
import me.chickenstyle.twilightforest.world.TFBiome;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public record PlayerEvents(TwilightForest main) implements Listener {

    @EventHandler //To test if the custom biomes work
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        main.getNmsHandler().setBiomeInChunk(TFBiome.FIRE_SWAP,e.getClickedBlock().getLocation().getChunk());
    }

    @EventHandler
    public void PlayerPortalEvent(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        Block block = player.getLocation().getBlock();

        if (block.getType() != Material.END_PORTAL) return;
        if (!(block.getState() instanceof TileState)) return;

        TileState state = (TileState) block.getState();
        PersistentDataContainer container = state.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(main, "TFPortal");

        if (container.has(key, PersistentDataType.STRING)) {
            World world = Bukkit.getWorld("TwilightForest");

            Location loc = block.getLocation();
            loc.setWorld(world);
            e.setTo(loc);
            Logger.log(player.getName() + " has been teleported to the Twilight Forest!");
        }
    }

    //@Todo add an option to add worlds when the portal is enabled
    @EventHandler
    public void onPlayerThrowItem(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() != Material.DIAMOND) return;

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter >= 10) cancel(); //Checks if runnable ran 10 times
                if (e.getItemDrop().isDead()) cancel(); //Checks if item isn't dead
                if (e.getItemDrop().isOnGround()) cancel(); //Check is item is in the air

                Location loc = e.getItemDrop().getLocation();
                if (loc.getBlock().getType() == Material.WATER) {

                    Levelled waterLevel = (Levelled) loc.getBlock().getBlockData();
                    if (waterLevel.getLevel() != waterLevel.getMaximumLevel())
                        cancel(); //Check if the block is a full water source

                    PortalChecker portalChecker = new PortalChecker(main);

                    if (portalChecker.isPortal(loc)) {
                        loc.getWorld().strikeLightning(loc);
                        portalChecker.activePortal(loc);
                    }


                    cancel();
                }
                counter++;
            }
        }.runTaskTimer(main, 0, 20);

    }


    private static class PortalChecker {
        // "Complicated" math to calculate if the area is a twilight forest portal
        private TwilightForest main;

        private PortalChecker(TwilightForest main) {
            this.main = main;
        }

        private static final Material[] flowers =
                {
                        Material.DANDELION, Material.POPPY,
                        Material.BLUE_ORCHID, Material.ALLIUM,
                        Material.AZURE_BLUET, Material.RED_TULIP,
                        Material.ORANGE_TULIP, Material.WHITE_TULIP,
                        Material.PINK_TULIP, Material.OXEYE_DAISY,
                        Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY,
                        Material.WITHER_ROSE, Material.CRIMSON_FUNGUS,
                        Material.WARPED_FUNGUS
                };


        private static final List<RelBlock> portal = new ArrayList<RelBlock>() {{
            add(new RelBlock(0, 0, 0, Material.WATER));
            add(new RelBlock(1, 0, 0, Material.GRASS_BLOCK));
            add(new RelBlock(1, 1, 0, flowers));
            add(new RelBlock(-1, 0, 0, Material.WATER));
            add(new RelBlock(-2, 0, 0, Material.GRASS_BLOCK));
            add(new RelBlock(-2, 1, 0, flowers));
            add(new RelBlock(0, 0, -1, Material.GRASS_BLOCK));
            add(new RelBlock(0, 1, -1, flowers));
            add(new RelBlock(1, 0, -1, Material.GRASS_BLOCK));
            add(new RelBlock(1, 1, -1, flowers));
            add(new RelBlock(-1, 0, -1, Material.GRASS_BLOCK));
            add(new RelBlock(-1, 1, -1, flowers));
            add(new RelBlock(-2, 0, -1, Material.GRASS_BLOCK));
            add(new RelBlock(-2, 1, -1, flowers));
            add(new RelBlock(0, 0, 1, Material.WATER));
            add(new RelBlock(-1, 0, 1, Material.WATER));
            add(new RelBlock(-2, 0, 1, Material.GRASS_BLOCK));
            add(new RelBlock(-2, 1, 1, flowers));
            add(new RelBlock(1, 0, 1, Material.GRASS_BLOCK));
            add(new RelBlock(1, 1, 1, flowers));
            add(new RelBlock(0, 0, 2, Material.GRASS_BLOCK));
            add(new RelBlock(0, 1, 2, flowers));
            add(new RelBlock(1, 0, 2, Material.GRASS_BLOCK));
            add(new RelBlock(1, 1, 2, flowers));
            add(new RelBlock(-1, 0, 2, Material.GRASS_BLOCK));
            add(new RelBlock(-1, 1, 2, flowers));
            add(new RelBlock(-2, 0, 2, Material.GRASS_BLOCK));
            add(new RelBlock(-2, 1, 2, flowers));
        }};


        private void reverseX(List<RelBlock> portal) {
            for (RelBlock relBlock : portal) {
                relBlock.x *= -1;
            }
        }

        private void reverseZ(List<RelBlock> portal) {
            for (RelBlock relBlock : portal) {
                relBlock.z *= -1;
            }
        }

        public boolean isPortal(Location loc) {

            List<RelBlock> copyPortal = new ArrayList<>(portal);

            A:
            for (int i = 0; i < 4; i++) {
                if (i == 1) reverseX(copyPortal);
                if (i == 2) reverseZ(copyPortal);
                if (i == 3) reverseX(copyPortal);

                for (RelBlock relBlock : copyPortal) {
                    Block block = loc.getBlock().getRelative(relBlock.x, relBlock.y, relBlock.z);
                    if (!relBlock.containsMaterial(block.getType())) {
                        continue A;
                    }
                }

                return true;
            }

            return false;
        }

        public void activePortal(Location loc) {
            List<RelBlock> copyPortal = new ArrayList<>(portal);
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                if (i == 1) reverseX(copyPortal);
                if (i == 2) reverseZ(copyPortal);
                if (i == 3) reverseX(copyPortal);

                for (RelBlock relBlock : copyPortal) {
                    Block block = loc.getBlock().getRelative(relBlock.x, relBlock.y, relBlock.z);
                    if (relBlock.containsMaterial(Material.WATER)) {
                        block.setType(Material.END_PORTAL);

                        TileState state = (TileState) block.getState();
                        PersistentDataContainer container = state.getPersistentDataContainer();

                        NamespacedKey key = new NamespacedKey(main, "TFPortal");
                        container.set(key, PersistentDataType.STRING, "Portal");
                        state.update();

                        counter++;
                    }
                    if (counter == 4) return;
                }
            }
        }
        private static class RelBlock {
            private int x, y, z;
            private Material[] materials;

            public RelBlock(int x, int y, int z, Material material) {
                this(x, y, z, new Material[]{material});
            }

            private RelBlock(int x, int y, int z, Material... materials) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.materials = materials;
            }

            public boolean containsMaterial(Material material) {
                for (Material mat : materials) {
                    if (mat == material) return true;
                }
                return false;
            }


        }
    }
}

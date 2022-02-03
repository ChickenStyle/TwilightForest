package me.chickenstyle.twilightforest.world;

import net.minecraft.resources.MinecraftKey;
import org.bukkit.NamespacedKey;

public enum TFBiome {

    TWILIGHT_FOREST("FB00FF", //purple
            "1FFF00", //green
            "FF0000", //red
            "000CFF","FF0000","FF0000"),


    DENSE_TWILIGHT_FOREST("FB00FF", //purple
            "1FFF00", //green
            "FF0000", //red
            "000CFF","FF0000","FF0000"),

    SWAMP("FB00FF", //purple
            "1FFF00", //green
            "FF0000", //red
            "000CFF","FF0000","FF0000"),

    FIRE_SWAP("FB00FF", //purple
            "1FFF00", //green
            "FF0000", //red
            "000CFF","FF0000","FF0000");

    private final String fogColor, waterColor, waterFogColor, skyColor,grassColor,foliageColor;

    TFBiome(String fogColor, String waterColor, String waterFogColor, String skyColor, String grassColor, String foliageColor) {
        this.fogColor = fogColor;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
        this.skyColor = skyColor;
        this.grassColor = grassColor;
        this.foliageColor = foliageColor;
    }

    public String getFogColor() {
        return fogColor;
    }

    public String getWaterColor() {
        return waterColor;
    }

    public String getWaterFogColor() {
        return waterFogColor;
    }

    public String getSkyColor() {
        return skyColor;
    }

    public String getGrassColor() {
        return grassColor;
    }

    public String getFoliageColor() {
        return foliageColor;
    }

    @Override
    public String toString() {
        String name = super.toString();
        String[] args = name.split("_");
        String newName = "";

        for (int i = 0; i < args.length;i++) {
            newName += args[i];

            if (i != args.length - 1) newName += " ";
        }
        return newName;
    }

    public MinecraftKey getMinecraftKey() {
        return new MinecraftKey("twilight-forest",super.toString().toLowerCase());
    }
}

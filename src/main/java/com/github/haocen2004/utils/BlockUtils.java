package com.github.haocen2004.utils;

import com.github.haocen2004.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.haocen2004.utils.ConfigUtils.load;


public class BlockUtils {

    public static List<Material> materials = new ArrayList<>();
    public static List<Material> materialsBlacklist = new ArrayList<>();

    private static File blockConfig = new File(Main.getMain().getDataFolder(), "blocks.yml");
    private static File blocksblacklistConfig = new File(Main.getMain().getDataFolder(), "blocks_blacklist.yml");

    private static FileConfiguration blocks;
    private static FileConfiguration blocksBlacklist;

    public static Material getRandomBlock(Boolean hasBlacklist) {
        if (materials.size() == 1){
            return materials.get(0);
        }else {
            Random r = new Random();
            if (hasBlacklist) return materialsBlacklist.get(r.nextInt(materialsBlacklist.size()));
            return materials.get(r.nextInt(materials.size()));

        }
    }

    public static void loadBlocks() {
        blocks = load(blockConfig);
        List<String> blockList = (List<String>) blocks.getList("blocks");
        materials.clear();
        for (String block : blockList) {
            if (!block.startsWith("-")) {
                materials.add(Material.getMaterial(block.toUpperCase()));
                materialsBlacklist.add(Material.getMaterial(block.toUpperCase()));
            }
        }
        blockList.clear();
        blocksBlacklist = load(blocksblacklistConfig);
        blockList = (List<String>) blocksBlacklist.getList("blocks");
        for (String block : blockList) {
            if (!block.startsWith("-")) {
                materialsBlacklist.remove(Material.getMaterial(block.toUpperCase()));
            }
        }
    }
}

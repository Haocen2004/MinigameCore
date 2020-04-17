package com.github.haocen2004;

import com.github.haocen2004.shuffle.Shuffle;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.*;
import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;


public final class Main extends JavaPlugin implements Listener {

    public static FileConfiguration lang;
    public static List<Material> materials = new ArrayList<>();

    private static FileConfiguration blocks;
    private static Plugin plugin;

    {
        plugin = this;
    }

    private File mainConfig = new File(getDataFolder(), "config.yml");
    private File langConfig;
    private File blockConfig = new File(getDataFolder(), "blocks.yml");

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfigs();
        getPluginManager().registerEvents(new Shuffle(), this);
        getPluginCommand("shuffle").setExecutor(new Shuffle());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                langConfig = new File(getDataFolder(), getConfig().get("lang") + ".yml");
                lang = loadLang(langConfig);
                blocks = load(blockConfig);
                List<String> blockList = (List<String>) blocks.getList("blocks");
                materials.clear();
                for (String block : blockList) {
                    if (!block.startsWith("-")) {
                        materials.add(Material.getMaterial(block.toUpperCase()));
                    }
                }

                sender.sendMessage(lang.getString("reload"));
                return true;
            }

        }
        return false;
    }

    public void loadConfigs() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!(mainConfig.exists())) {
            saveDefaultConfig();
        }
        reloadConfig();
        langConfig = new File(getDataFolder(), getConfig().get("lang") + ".yml");
        lang = loadLang(langConfig);
        blocks = load(blockConfig);
        List<String> blockList = new ArrayList<>();
        blockList.clear();
        blockList = (List<String>) blocks.getList("blocks");
        for (String block : blockList) {
            if (!block.startsWith("-")) {
                materials.add(Material.getMaterial(block.toUpperCase()));
            }
        }
    }


    public FileConfiguration loadLang(File file) {
        if (!file.exists()) {
            langConfig = new File(getDataFolder(), "zh_cn.yml");
            getServer().broadcastMessage("Load failed,use default language zh_cn.");
            getLogger().warning("Load failed,use default language zh_cn.");
            file = langConfig;
            try {
                file.createNewFile();
                saveResource("en_us.yml", true);
                saveResource("zh_cn.yml", true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return loadConfiguration(file);
    }

    public FileConfiguration load(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                saveResource(file.getName(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loadConfiguration(file);
    }


    public static Plugin getMain() {
        return plugin;
    }

}

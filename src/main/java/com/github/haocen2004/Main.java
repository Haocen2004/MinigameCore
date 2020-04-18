package com.github.haocen2004;

import com.github.haocen2004.assassin.Assassin;
import com.github.haocen2004.randomray.RandomRay;
import com.github.haocen2004.shuffle.Shuffle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import static com.github.haocen2004.utils.BlockUtils.loadBlocks;
import static org.bukkit.Bukkit.*;
import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;


public final class Main extends JavaPlugin implements Listener {

    public static FileConfiguration lang;

    private static Plugin plugin;

    {
        plugin = this;
    }

    private File mainConfig = new File(getDataFolder(), "config.yml");
    private File langConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfigs();
        getPluginManager().registerEvents(new Shuffle(), this);
        getPluginCommand("shuffle").setExecutor(new Shuffle());
        getPluginManager().registerEvents(new RandomRay(), this);
        getPluginCommand("randomray").setExecutor(new RandomRay());
        getPluginManager().registerEvents(new Assassin(), this);
        getPluginCommand("assassin").setExecutor(new Assassin());
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
                loadBlocks();

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
        loadBlocks();
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

    public static Plugin getMain() {
        return plugin;
    }

}

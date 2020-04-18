package com.github.haocen2004.utils;

import com.github.haocen2004.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;

public class ConfigUtils {

    public static FileConfiguration load(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                Main.getMain().saveResource(file.getName(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loadConfiguration(file);
    }
}

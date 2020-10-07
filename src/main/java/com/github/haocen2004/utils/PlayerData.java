package com.github.haocen2004.utils;

import com.github.haocen2004.Main;
import com.github.haocen2004.assassin.Assassin;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PlayerData {
    private UUID uuid;
    private Material block;
    private boolean isSurvival = true;
    private boolean isChecked = false;
    private boolean isAssassin = false;
    private Location lookat;
    private ScoreboardUtils board = new ScoreboardUtils(Main.getMain(),getServer().getPlayer(uuid));

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public void setBlock(Material block) {
        this.block = block;
    }

    public void setSurvival(boolean survival) {
        isSurvival = survival;
    }

    public boolean isSurvival() {
        return isSurvival;
    }

    public Material getBlock() {
        return block;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public Location getLookat() {
        return lookat;
    }

    public void setLookat(Location lookat) {
        this.lookat = lookat;
    }

    public void setAss(boolean b) {
        isAssassin = b;
    }

    public boolean isAssassin() {
        return isAssassin;
    }

    public ScoreboardUtils getBoard() {
        return board;
    }
}

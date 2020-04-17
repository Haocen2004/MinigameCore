package com.github.haocen2004.shuffle;

import org.bukkit.Material;

import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private Material block;
    private boolean isSurvival;
    private boolean isChecked = false;

    public PlayerData(UUID uuid, Material randomBlock) {
        this.uuid = uuid;
        block = randomBlock;
        isSurvival = true;
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
}

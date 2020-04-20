package com.github.haocen2004.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class PlayerUtils {
    public static Entity getCursorTarget(Player p, double range) {
        Block block;
        Entity target;
        Iterator<Entity> entities;
        Location loc = p.getEyeLocation();
        Vector vec = loc.getDirection().multiply(0.15);
        while ((range -= 0.1) > 0 && ((block = loc.getWorld().getBlockAt(loc)).isLiquid() || block.isEmpty())) {
            entities = loc.getWorld().getNearbyEntities(loc.add(vec), 0.001, 0.001, 0.001).iterator();
            while(entities.hasNext()){
                if((target = entities.next()) != p){
                    return target;
                }
            }
        }
        return null;
    }

    public static int getSurvival(List<PlayerData> playerList) {
        int count = 0;
        for (PlayerData playerData : playerList) {
            if (playerData.isSurvival()) count++;
        }
        return count;
    }

    public static Location getNearlyPlayer(Player p) {
        double distance = 0;
        Player nearlyp = null;
        for (Player player : getServer().getOnlinePlayers()) {
            if (player == p) continue;
            if (p.getWorld() != player.getWorld()) continue;
            double thisdistance = p.getLocation().distance(player.getLocation());
            if (thisdistance < distance || distance == 0) {
                distance = thisdistance;
                nearlyp = player;
            }
        }
        if (nearlyp == null) return null;
        return nearlyp.getLocation();

    }
}

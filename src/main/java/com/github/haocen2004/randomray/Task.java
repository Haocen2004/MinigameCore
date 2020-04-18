package com.github.haocen2004.randomray;

import com.github.haocen2004.utils.PlayerData;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.github.haocen2004.randomray.RandomRay.isStart;
import static com.github.haocen2004.utils.BlockUtils.getRandomBlock;
import static com.github.haocen2004.utils.BlockUtils.materialsBlacklist;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.FluidCollisionMode.SOURCE_ONLY;

public class Task extends BukkitRunnable {

    private List<PlayerData> playerList = new ArrayList<>();
    private boolean isFirstRun = true;

    @Override
    public void run() {
        if (!isStart()) {
            this.cancel();
        }

        Server server = getServer();

        if (isFirstRun) {
            for (Player player : server.getOnlinePlayers()) {
                playerList.add(new PlayerData(player.getUniqueId()));
            }
            isFirstRun = false;
        }

        for (PlayerData playerData : playerList) {
            Player p = server.getPlayer(playerData.getUuid());
            if (p == null) continue;
            try {
                Location lookat = p.rayTraceBlocks(256,SOURCE_ONLY).getHitBlock().getLocation();
                if (playerData.getLookat() == null) {
                    playerData.setLookat(lookat);
                    continue;
                }
                if (!playerData.getLookat().equals(lookat) && !materialsBlacklist.contains(p.getWorld().getBlockAt(lookat).getType())) {
                    p.getWorld().getBlockAt(playerData.getLookat()).setType(getRandomBlock(true));
                    playerData.setLookat(lookat);
                }
            } catch (Exception ignore) {
            }

        }
    }
}

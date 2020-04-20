package com.github.haocen2004.assassin;

import com.github.haocen2004.utils.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.haocen2004.Main.lang;
import static com.github.haocen2004.assassin.Assassin.*;
import static com.github.haocen2004.utils.PlayerUtils.getCursorTarget;
import static com.github.haocen2004.utils.PlayerUtils.getSurvival;
import static org.bukkit.Bukkit.getServer;

public class Task extends BukkitRunnable {

    private final List<PlayerData> playerList = new ArrayList<>();
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
            PlayerData p = playerList.get(new Random().nextInt(playerList.size()));
            p.setAss(true);
            server.getPlayer(p.getUuid()).setPlayerListName("§4"+server.getPlayer(p.getUuid()).getName());
            server.getPlayer(p.getUuid()).setCustomName("§4"+server.getPlayer(p.getUuid()).getName());
            server.getPlayer(p.getUuid()).addScoreboardTag("assassin");
            server.broadcastMessage(lang.getString("assassin.start").replace("%s",  server.getPlayer(p.getUuid()).getName()));
            isFirstRun = false;
        }

        setSeeAssassin(false);

        for (PlayerData playerData : playerList) {
            if (playerData.isAssassin()) continue;
            Player p = server.getPlayer(playerData.getUuid());
            if (p.getScoreboardTags().contains("death_by_ass")) playerData.setSurvival(false);
            if (p == null) continue;
            if (p.getGameMode() != GameMode.SURVIVAL) continue;
            try {
                if (!isSeeAssassin() && getCursorTarget(p, 128).getScoreboardTags().contains("assassin")) {
                    ((Player)getCursorTarget(p, 128)).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lang.getString("assassin.actionbar.assassin")));
                    setSeeAssassin(true);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lang.getString("assassin.actionbar.player")));
                }
            } catch (NullPointerException ignore){}
        }

        if(getSurvival(playerList) == 1){

            server.broadcastMessage(lang.getString("assassin.win"));
            this.cancel();

        }
    }
}

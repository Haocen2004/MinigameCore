package com.github.haocen2004.shuffle;

import com.github.haocen2004.Main;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;


public final class Shuffle implements Listener, CommandExecutor {

    private static boolean isStart = false;
    private static FileConfiguration lang;
    private List<UUID> playerList = new ArrayList<>();
    private BukkitRunnable mainTask = new Tasks();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean result = false;
        if (sender.isOp()) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    isStart = true;
                    mainTask = new Tasks();
                    mainTask.runTaskTimerAsynchronously(Main.getMain(), 20, 20);
                    playerList.clear();
                    for (Player player : getServer().getOnlinePlayers()) {
                        playerList.add(player.getUniqueId());
                    }
                    result = true;
                } else if (args[0].equalsIgnoreCase("stop")) {
                    mainTask.cancel();
                    isStart = false;
                    result = true;
                }

            }
        }
        return result;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (isStart) {
            boolean ingame = false;
            for (UUID player : playerList) {
                if (event.getPlayer().getUniqueId().equals(player)) {
                    ingame = true;
                    break;
                }
            }
            if (!ingame) event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    public static boolean isStart() {
        return isStart;
    }

}

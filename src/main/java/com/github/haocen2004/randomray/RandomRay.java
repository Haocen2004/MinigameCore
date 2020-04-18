package com.github.haocen2004.randomray;

import com.github.haocen2004.Main;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.getServer;

public class RandomRay implements Listener, CommandExecutor {

    private static boolean isStart = false;
    private BukkitRunnable mainTask = new Task();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        boolean result = false;
        if (sender.isOp()) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    isStart = true;
                    mainTask = new Task();
                    mainTask.runTaskTimer(Main.getMain(), 0, 1);
                    for (Player p : getServer().getOnlinePlayers()){
                        p.addScoreboardTag("ray_random_ingame");
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
            if (!event.getPlayer().getScoreboardTags().contains("ray_random_ingame")) event.getPlayer().setGameMode(GameMode.SPECTATOR);
        } else {
            event.getPlayer().removeScoreboardTag("ray_random_ingame");
        }
    }

    public static boolean isStart() {
        return isStart;
    }

}

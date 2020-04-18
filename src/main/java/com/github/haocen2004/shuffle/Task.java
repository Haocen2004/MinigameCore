package com.github.haocen2004.shuffle;

import com.github.haocen2004.Main;
import com.github.haocen2004.utils.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.github.haocen2004.Main.lang;
import static com.github.haocen2004.shuffle.Shuffle.isStart;
import static com.github.haocen2004.utils.BlockUtils.getRandomBlock;
import static com.github.haocen2004.utils.BlockUtils.materials;
import static com.github.haocen2004.utils.PlayerUtils.getSurvival;
import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;


public class Task extends BukkitRunnable {
    private List<PlayerData> playerList = new ArrayList<>();
    private boolean isFirstRun = true;
    private int succeed = 0;
    private int second = 0;
    private int round = 0;
    private int roundSecond = Main.getMain().getConfig().getInt("shuffle.time",300);

    @Override
    public void run() {

        if (!isStart()) {
            this.cancel();
        }

        Server server = getServer();

        if (isFirstRun) {


            getLogger().info("Total " + materials.size() + " blocks");

            for (Player player : server.getOnlinePlayers()) {
                Material block = getRandomBlock(false);
                PlayerData pd = new PlayerData(player.getUniqueId());
                pd.setBlock(block);
                playerList.add(pd);
                TextComponent msg = new TextComponent(lang.getString("shuffle.block").split("%s")[0]);
                msg.setColor(ChatColor.DARK_GREEN);
                TranslatableComponent itemmsg = new TranslatableComponent("block.minecraft." + block.getKey().getKey());
                itemmsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder("{Count:1b,id:\"" + block.getKey().getKey() + "\"}").create()));
                msg.addExtra(itemmsg);
                try {
                    msg.addExtra(lang.getString("shuffle.block").split("%s")[1]);
                } catch (Exception ignored) {
                }
                player.spigot().sendMessage(msg);

            }

            isFirstRun = false;

        } else {

            if (succeed == getSurvival(playerList)) {
                if (succeed != 0) {
                    startNewRound();
                } else {
                    server.broadcastMessage(lang.getString("shuffle.all"));
                    this.cancel();
                    second = 0;
                }
            }

            for (PlayerData pd : playerList) {
                if (pd.isSurvival()) {
                    Player p = server.getPlayer(pd.getUuid());
                    if (p == null) continue;
                    Location location = p.getLocation();
                    Block block = p.getWorld().getBlockAt(location);
                    location.setY(location.getY() - 1);
                    Block block2 = p.getWorld().getBlockAt(location);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lang.getString("shuffle.actionbar.round").replace("%s", "" + (round + 1))));
                    if (!pd.isChecked() && (block.getType().equals(pd.getBlock()) || block2.getType().equals(pd.getBlock()))) {
                        pd.setChecked(true);
                        succeed++;
                        getServer().broadcastMessage(lang.getString("shuffle.found").replace("%s", p.getName()));

                    }
                }
            }


        }

        if (second >= roundSecond ) {
            for (PlayerData player : playerList) {
                if (!player.isChecked() && player.isSurvival()) {
                    player.setSurvival(false);
                    Player p = server.getPlayer(player.getUuid());
                    getServer().broadcastMessage(lang.getString("shuffle.fail").replace("%s", p.getName()));
                    p.sendMessage(lang.getString("shuffle.round").replace("%s", "" + round));
                    p.setGameMode(GameMode.SPECTATOR);
                }
            }
            startNewRound();

        } else if (second >= (roundSecond-1)) {
            for (PlayerData player : playerList) {
                if (!player.isChecked()) {
                    server.getPlayer(player.getUuid()).sendMessage(lang.getString("shuffle.last1s"));
                }
            }
        } else if (second >= (roundSecond-10)) {
            for (PlayerData player : playerList) {
                if (!player.isChecked()) {
                    server.getPlayer(player.getUuid()).sendMessage(lang.getString("shuffle.last10s").replace("%s", "" + (300 - second)));
                }
            }
        }

        second++;


    }

    void startNewRound() {

        for (PlayerData player : playerList) {
            if (player.isSurvival()) {
                Material block = getRandomBlock(false);
                player.setBlock(block);
                TextComponent msg = new TextComponent(lang.getString("shuffle.block").split("%s")[0]);
                msg.setColor(ChatColor.DARK_GREEN);
                TranslatableComponent itemmsg = new TranslatableComponent("block.minecraft." + block.getKey().getKey());
                itemmsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder("{Count:1b,id:\"" + block.getKey().getKey() + "\"}").create()));
                msg.addExtra(itemmsg);
                try {
                    msg.addExtra(lang.getString("shuffle.block").split("%s")[1]);
                } catch (Exception ignored) {
                }
                getServer().getPlayer(player.getUuid()).spigot().sendMessage(msg);
                second = 0;
                player.setChecked(false);
            }
        }
        round++;
        succeed = 0;
    }

}

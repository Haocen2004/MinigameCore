package com.github.haocen2004.shuffle;

import com.github.haocen2004.Main;
import com.github.haocen2004.utils.ConfigUtils;
import com.github.haocen2004.utils.PlayerData;
import com.github.haocen2004.utils.ScoreboardUtils;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.github.haocen2004.Main.*;
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
    private int block_l = 0;
    private int round_l = 0;
    private int time_l = 0;
    private int player_l = 0;
    private FileConfiguration cfg = getMain().getConfig();
    private final int roundSecond = cfg.getInt("shuffle.time",300);


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

                getLogger().info(getGameTag());

                playerList.add(pd);
                TextComponent msg = new TextComponent(lang.getString("shuffle.block").split("%s")[0]);
                msg.setColor(ChatColor.DARK_GREEN);
                TranslatableComponent itemmsg = new TranslatableComponent("block.minecraft." + block.getKey().getKey());
                itemmsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder("{Count:1b,id:\"" + block.getKey().getKey() + "\"}").create()));
                msg.addExtra(itemmsg);
                try {
                    msg.addExtra(lang.getString("shuffle.block").split("%s")[1]);
                } catch (Exception ignored) {}
                player.spigot().sendMessage(msg);
                scoreboardSetup(pd);

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
                    /*
                    * ====== Shuffle ======1
                    * Next Block2
                    * Stone3        2
                    *4
                    * Time5
                    * 60s6          5
                    *7
                    * Round8
                    * 59            8
                    *10
                    * Player11
                    *812            11
                    *13
                    * dark grey: GameTag14
                    * */
                    Material block3 = pd.getBlock();

                    TextComponent msg = new TextComponent(lang.getString("shuffle.block").split("%s")[0]);
                    msg.setColor(ChatColor.DARK_GREEN);
                    TranslatableComponent itemmsg = new TranslatableComponent("block.minecraft." + block3.getKey().getKey());
                    itemmsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder("{Count:1b,id:\"" + block3.getKey().getKey() + "\"}").create()));
                    msg.addExtra(itemmsg);
                    try {
                        msg.addExtra(lang.getString("shuffle.block").split("%s")[1]);
                    } catch (Exception ignored) {}
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, msg);
                    if (!pd.isChecked() &&(block.getType().equals(pd.getBlock()) || block2.getType().equals(pd.getBlock()))) {
                        pd.setChecked(true);
                        succeed++;
                        getServer().broadcastMessage(lang.getString("shuffle.found").replace("%s", p.getName()));

                    }

                    try{
                        ScoreboardUtils board = pd.getBoard();
                        board.setLine(block_l, lang.getString("shuffle.scoreboard.line"+(block_l+1)).replace("%block%",itemmsg.toLegacyText()));
                        board.setLine(time_l, lang.getString("shuffle.scoreboard.line"+(time_l+1)).replace("%time%",(roundSecond-second)+""));
                        board.setLine(round_l, lang.getString("shuffle.scoreboard.line"+(round_l+1)).replace("%round%",(round+1)+""));
                        board.setLine(player_l, lang.getString("shuffle.scoreboard.line"+(player_l+1)).replace("%player%",getSurvival(playerList)+""));
                    } catch (Exception e){
                        scoreboardSetup(pd);
                        e.printStackTrace();
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

    void scoreboardSetup(PlayerData pd){
        getLogger().info("start gen scoreboard");
        ScoreboardUtils board = pd.getBoard();
        board.setLine_count(cfg.getInt("shuffle.scoreboard.line"));
        board.setTitle(lang.getString("shuffle.scoreboard.title"));
        for (int i = 0; i < cfg.getInt("shuffle.scoreboard.line") ; i++) {
            int i1 = i+1;
            getLogger().info("try to get shuffle.scoreboard.line"+i1);
            String line = lang.getString("shuffle.scoreboard.line"+i1);
            getLogger().info("get "+line);
            if(line != null) {
                if (line.contains("%block%")) {
                    block_l = i;
                    getLogger().info(i+"");
                }
                if (line.contains("%round%")) {
                    round_l = i;
                    getLogger().info(i+"");
                }
                if (line.contains("%player%")) {
                    player_l = i;
                    getLogger().info(i+"");
                }
                if (line.contains("%time%")) {
                    time_l = i;
                    getLogger().info(i+"");
                }
                if (line.contains("%tag%")) {
                    String tag = getGameTag();
                    line = line.replace("%tag%",tag);
                    getLogger().info("tag replace "+tag);
                    getLogger().info(line);
                }

            }
            board.setLine(i,line);
        }
        getServer().getPlayer(pd.getUuid()).setScoreboard(board.board);
    }

}

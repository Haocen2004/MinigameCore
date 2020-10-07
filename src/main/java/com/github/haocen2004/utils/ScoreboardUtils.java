package com.github.haocen2004.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class ScoreboardUtils {
    private Player player;
    private Plugin plugin;
    public Scoreboard board;
    private Objective objective;
    private int line_count;

    private HashMap<Integer, String> cache = new HashMap<>();

    public ScoreboardUtils(Plugin plugin, Player player) {
        this.player = player;
        this.plugin = plugin;
        this.board = Objects.requireNonNull(this.plugin.getServer().getScoreboardManager()).getNewScoreboard();
        this.objective = this.board.registerNewObjective("sb1","dummy", "sb2");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("...");
    }


    public void setTitle(String string) {
        if(string == null) string = "";

        if(cache.containsKey(-1) && cache.get(-1).equals(string)) return;
        cache.remove(-1);
        cache.put(-1, string);
        objective.setDisplayName(string);
    }


    public void setLine(int line, String string) {
        Team t = board.getTeam(line + "");
        if(string == null) string = "";

        if(cache.containsKey(line) && cache.get(line).equals(string)) return;
        cache.remove(line);
        cache.put(line, string);
        string = prepForShortline(string); // Prepare the string to preserve colors
        ArrayList<String> parts = null;
        parts = convertIntoPieces(string, 16); // Convert it into pieces!

        try {
            t.setPrefix(fixAnyIssues(parts.get(0))); // Set the first
            t.setSuffix(fixAnyIssues(parts.get(1))); // Set the scond
        }catch (Exception ignore){}

    }

    public void setLine_count(int line_count) {
        this.line_count = line_count;
        int score = line_count;
        for (Team team : this.board.getTeams()) {
            team.unregister();
        }
        for(int i = 0; i < line_count;i++) {
            Team t = this.board.registerNewTeam(i + "");
            t.addEntry(ChatColor.values()[i] + "");

            this.objective.getScore(ChatColor.values()[i] + "").setScore(score);

            score--;
        }

    }
    private String fixAnyIssues(String part) {
            if(part.length() > 16)
            {
                return part.substring(16);
            } else {
                return part;
            }
    }

    private String prepForShortline(String color)
    {
        if(color.length() > 16)
        {
            ArrayList<String> pieces = convertIntoPieces(color, 16);
            return pieces.get(0) + "§f"  + getLastColor(pieces.get(0)) + pieces.get(1);
        }
        return color;
    }

    private String getLastColor(String s)
    {
        String last = ChatColor.getLastColors(s);
        return last;
    }

    private ArrayList<String> convertIntoPieces(String s, int allowed_line_size)
    {
        ArrayList<String> parts = new ArrayList<>();

        if(ChatColor.stripColor(s).length() > allowed_line_size)
        {
            parts.add(s.substring(0, allowed_line_size));

            String s2 = s.substring(allowed_line_size);
            if(s2.length() > allowed_line_size)
                s2 = s2.substring(0, allowed_line_size);
            parts.add(s2);
        } else {
            parts.add(s);
            parts.add("");
        }

        return parts;
    }
}

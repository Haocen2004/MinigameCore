package com.github.haocen2004.assassin;

import com.github.haocen2004.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static com.github.haocen2004.Main.lang;
import static com.github.haocen2004.utils.PlayerUtils.getNearlyPlayer;
import static org.bukkit.Bukkit.getServer;

public class Assassin implements Listener, CommandExecutor {

    private static boolean isStart = false;
    private BukkitRunnable mainTask = new Task();
    private static boolean isSeeAssassin = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        boolean result = false;
        if (sender.isOp()) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (getServer().getOnlinePlayers().size() <= 1) {
                        sender.sendMessage(lang.getString("notenoughplayer"));
                        return true;
                    }
                    isStart = true;
                    mainTask = new Task();
                    mainTask.runTaskTimer(Main.getMain(), 0, 1);
                    for (Player p : getServer().getOnlinePlayers()){
                        p.addScoreboardTag("ass_ingame");
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
            if (!event.getPlayer().getScoreboardTags().contains("ass_ingame")) event.getPlayer().setGameMode(GameMode.SPECTATOR);
        } else {
            event.getPlayer().removeScoreboardTag("ass_ingame");
            event.getPlayer().removeScoreboardTag("assassin");
            event.getPlayer().removeScoreboardTag("death_by_ass");
        }
    }

    @EventHandler
    public void onAssassinHurt(EntityDamageEvent event){
        if (event.getEntity().getScoreboardTags().contains("assassin")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        if (isSeeAssassin && event.getPlayer().getScoreboardTags().contains("assassin")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        if (isSeeAssassin && event.getPlayer().getScoreboardTags().contains("assassin")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageByEntityEvent event){
        if (isSeeAssassin && event.getDamager().getScoreboardTags().contains("assassin")){
            event.setCancelled(true);
        } else if(event.getDamager().getScoreboardTags().contains("assassin")){
            if(event.getEntityType() == EntityType.PLAYER){
                Player p = (Player) event.getEntity();
                p.setGameMode(GameMode.SPECTATOR);
                p.addScoreboardTag("death_by_ass");
            }else {
                event.setDamage(1024);
            }
        }
    }

    @EventHandler
    public void onPlayerPickItem(EntityPickupItemEvent event){
        if (isSeeAssassin && event.getEntity().getScoreboardTags().contains("assassin")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){
        if (isSeeAssassin && event.getPlayer().getScoreboardTags().contains("assassin")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event){
        if (isSeeAssassin && event.getPlayer().getScoreboardTags().contains("assassin")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent event) {
        if (isSeeAssassin && event.getPlayer().getScoreboardTags().contains("assassin")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerUsingItem(PlayerInteractEvent event) {
        if (isSeeAssassin && event.getPlayer().getScoreboardTags().contains("assassin")) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem().getType() == Material.COMPASS) {
                    Location location = getNearlyPlayer(event.getPlayer());
                    if (location != null) {
                        event.getPlayer().setCompassTarget(location);
                    } else {
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lang.getString("assassin.compass.fail")));
                    }
                }
            }
        }
    }


    public static boolean isStart() {
        return isStart;
    }

    public static boolean isSeeAssassin() {
        return isSeeAssassin;
    }

    public static void setSeeAssassin(boolean seeAssassin) {
        isSeeAssassin = seeAssassin;
    }
}

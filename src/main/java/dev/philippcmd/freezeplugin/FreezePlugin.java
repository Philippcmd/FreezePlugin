package dev.philippcmd.freezeplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class FreezePlugin extends JavaPlugin implements Listener {

    private final Map<UUID, Long> frozenPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("FreezePlugin enabled.");
    }

    @Override
    public void onDisable() {
        frozenPlayers.clear();
        getLogger().info("FreezePlugin disabled.");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isPlayerFrozen(player)) {
            event.setCancelled(true);
        }
    }

    private boolean isPlayerFrozen(Player player) {
        UUID playerId = player.getUniqueId();
        if (!frozenPlayers.containsKey(playerId)) {
            return false;
        }
        long freezeTime = frozenPlayers.get(playerId);
        if (freezeTime == 0 || System.currentTimeMillis() <= freezeTime) {
            return true;
        }
        frozenPlayers.remove(playerId);
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("freeze")) {
            return false;
        }

        if (args.length < 2 || args.length > 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /freeze <player> <time in sec> [freeze/unfreeze]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        try {
            int time = Integer.parseInt(args[1]);
            String action = args.length == 3 ? args[2].toLowerCase() : "toggle";

            if (action.equals("freeze")) {
                freezePlayer(target, time);
                sender.sendMessage(ChatColor.GREEN + target.getName() + " has been frozen for " +
                        (time == 0 ? "indefinitely" : time + " seconds") + ".");
            } else if (action.equals("unfreeze")) {
                unfreezePlayer(target);
                sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unfrozen.");
            } else if (action.equals("toggle")) {
                if (isPlayerFrozen(target)) {
                    unfreezePlayer(target);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unfrozen.");
                } else {
                    freezePlayer(target, time);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " has been frozen for " +
                            (time == 0 ? "indefinitely" : time + " seconds") + ".");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid action. Use freeze/unfreeze.");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Time must be a number.");
        }

        return true;
    }

    private void freezePlayer(Player player, int timeInSeconds) {
        UUID playerId = player.getUniqueId();
        if (timeInSeconds <= 0) {
            frozenPlayers.put(playerId, 0L);
        } else {
            frozenPlayers.put(playerId, System.currentTimeMillis() + timeInSeconds * 1000L);
        }
    }

    private void unfreezePlayer(Player player) {
        frozenPlayers.remove(player.getUniqueId());
    }
}

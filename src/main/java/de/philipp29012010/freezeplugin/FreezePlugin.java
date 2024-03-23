package de.philipp29012010.freezeplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class FreezePlugin extends JavaPlugin implements CommandExecutor, Listener {
    private Set<Player> frozenPlayers;

    @Override
    public void onEnable() {
        getCommand("freeze").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        frozenPlayers = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /freeze <player>");
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("Player not found!");
                return false;
            }

            if (frozenPlayers.contains(target)) {
                frozenPlayers.remove(target);
                player.sendMessage(target.getName() + " has been unfrozen.");
            } else {
                frozenPlayers.add(target);
                player.sendMessage(target.getName() + " has been frozen.");
            }
        }
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }
}
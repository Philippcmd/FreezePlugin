package de.philipp29012010.freezeplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class FreezePlugin extends JavaPlugin implements Listener, CommandExecutor {

    // Set to store frozen players
    private final Set<Player> frozenPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Register the plugin's command executor
        this.getCommand("freeze").setExecutor(this);
        // Register the event listener
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        frozenPlayers.clear(); // Clear the set on plugin disable
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("freeze")) {
            if (args.length < 1 || args.length > 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /freeze <player> <freeze/unfreeze>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (args.length == 1) {
                // Toggle the freeze state if no third argument is given
                if (frozenPlayers.contains(target)) {
                    frozenPlayers.remove(target);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unfrozen.");
                    target.sendMessage(ChatColor.YELLOW + "You have been unfrozen.");
                } else {
                    frozenPlayers.add(target);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " has been frozen.");
                    target.sendMessage(ChatColor.RED + "You have been frozen.");
                }
            } else if (args.length == 2) {
                String action = args[1].toLowerCase();
                switch (action) {
                    case "freeze":
                        if (!frozenPlayers.contains(target)) {
                            frozenPlayers.add(target);
                            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been frozen.");
                            target.sendMessage(ChatColor.RED + "You have been frozen.");
                        } else {
                            sender.sendMessage(ChatColor.RED + target.getName() + " is already frozen.");
                        }
                        break;

                    case "unfreeze":
                        if (frozenPlayers.contains(target)) {
                            frozenPlayers.remove(target);
                            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unfrozen.");
                            target.sendMessage(ChatColor.YELLOW + "You have been unfrozen.");
                        } else {
                            sender.sendMessage(ChatColor.RED + target.getName() + " is not frozen.");
                        }
                        break;

                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid action. Use 'freeze' or 'unfreeze'.");
                        break;
                }
            }

            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if the player is frozen
        if (frozenPlayers.contains(event.getPlayer())) {
            // Prevent movement by setting the player's position back to where it started
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Prevent interaction if the player is frozen
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}

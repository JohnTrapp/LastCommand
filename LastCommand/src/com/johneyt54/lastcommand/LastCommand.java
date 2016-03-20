/*
 * Copyright (C) 2016 John Trapp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * For questions, comments, concerns, or to suggest edits, please contact me at
 * LastCommand@johnvontrapp.com
 *
 * GitHub Repository: https://github.com/johneyt54/LastCommand.git
 */
package com.johneyt54.lastcommand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author John Trapp
 */
public class LastCommand extends JavaPlugin {

    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CommandPreprocessListener(), this);
        getLogger().info("LastCommand Started!");
    }

    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("CommandWarp")) {

            if (!(sender instanceof Player)) {  //If the console gives the command
                sender.sendMessage("Silly Console, you cannot warp places! Try ingame.");
                return false;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("lastcommand.commandWarp")) {  //If the player does not have the right permissions
                player.sendMessage("You don't have access to that command.");
                return false;
            } else {    //Player has right permissions
                if (args.length != 1) {   //Player did not specify arguments
                    sender.sendMessage("Please specify a player.");
                    return false;   //Quit method as false
                } else if (args.length == 1) {  //Player specified right number of arguments
                    Player target = Bukkit.getPlayer(args[0]);
                    String targetWarpLocation = target.getUniqueId().toString();

                    if (getConfig().getConfigurationSection(targetWarpLocation) == null) {  //Make sure there is an entry!
                        player.sendMessage(args[0] + " has not logged a command!");
                        return false;
                    }

                    ConfigurationSection cs = getConfig().getConfigurationSection(targetWarpLocation);  //Pulls up the correct entry
                    Location loc = new Location(getServer().getWorld(cs.getString("world")),
                            cs.getDouble("X"), cs.getDouble("Y"), cs.getDouble("Z"),
                            cs.getLong("yaw"), cs.getLong("pitch"));  //Gets the location stored
                    player.teleport(loc);   //Teleports requesting player to the location
                    player.sendMessage("Warp complete.");
                    player.sendMessage("Last command: " + cs.getString("command")); //Prints out last command

                    //sender.sendMessage("UUID: " + targetWarpLocation);  //Uncode for debug
                    //sender.sendMessage("Hash Value: " + targetWarpLocation.hashCode());
                    return true;
                }
                return false; //Player did not specify correct number of arguments.
            }
        }

        if (cmd.getName().equalsIgnoreCase("viewLast")) {
            if (args.length != 1) {  //Player did not specify arguments correctly
                sender.sendMessage("Please specify a player.");
                return false;   //Quit method as falsef
            }
            Player target = Bukkit.getPlayer(args[0]);
            String targetWarpLocation = target.getUniqueId().toString();
            if (getConfig().getConfigurationSection(targetWarpLocation) == null) {  //Make sure there is an entry!
                sender.sendMessage(args[0] + " has not logged a command!");
                return false;
            } else {
                ConfigurationSection cs = getConfig().getConfigurationSection(targetWarpLocation);  //Pulls up the correct entry
                sender.sendMessage("Last command of " + target.getDisplayName() + " : " + cs.getString("command")); //Prints out last command
            }
        }

        if (cmd.getName().equalsIgnoreCase("test")) {  //Just for shits and giggles
            sender.sendMessage("Test complete!!!");
            return true;
        }

        return false;  //Just in case everything goes to the shitter.
    }

    public class CommandPreprocessListener implements Listener {  //The listener that listens to the commands

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
            Player player = event.getPlayer();  //Who gave the command?
            String warpLocation = player.getUniqueId().toString(); //Get the UUID

            Location loc = player.getLocation();  //Save the player's location
            getConfig().createSection(warpLocation);  //Create a section for this location
            ConfigurationSection cs = getConfig().getConfigurationSection(warpLocation);
            cs.set("world", loc.getWorld().getName());  //Store the location!
            cs.set("X", loc.getX());
            cs.set("Y", loc.getY());
            cs.set("Z", loc.getZ());
            cs.set("yaw", loc.getYaw());
            cs.set("pitch", loc.getPitch());
            cs.set("command", event.getMessage());  //Store the command!
            saveConfig();  //Save that shit!
        }
    }
}

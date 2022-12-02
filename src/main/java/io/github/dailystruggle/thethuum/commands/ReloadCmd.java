package io.github.dailystruggle.thethuum.commands;

import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.commandsapi.common.CommandsAPI;
import io.github.dailystruggle.commandsapi.common.CommandsAPICommand;
import io.github.dailystruggle.thethuum.shouts.ShoutType;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import io.github.dailystruggle.thethuum.tools.TPS;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReloadCmd extends BukkitTreeCommand {
    public ReloadCmd(Plugin plugin, @Nullable CommandsAPICommand parent) {
        super(plugin, parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        Bukkit.getScheduler().runTaskAsynchronously(
                io.github.dailystruggle.thethuum.Plugin.getInstance(),
                this::executeRelaod
        );
        return true;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "thuum.reload";
    }

    @Override
    public String description() {
        return "update configuration from config.yml";
    }

    @Override
    public void msgBadParameter(UUID callerId, String parameterName, String parameterValue) {
        CommandSender sender;
        if(callerId.equals(CommandsAPI.serverId)) sender = Bukkit.getConsoleSender();
        else sender = Bukkit.getPlayer(callerId);
        if(sender == null) return;
        SendMessage.sendMessage(sender, ChatColor.YELLOW + "invalid parameter " + parameterName + ":" + parameterValue);
    }

    protected void executeRelaod() {
        io.github.dailystruggle.thethuum.Plugin instance = io.github.dailystruggle.thethuum.Plugin.getInstance();
        String msg = "[TheThuum] Reloading...";
        SendMessage.sendMessage(Bukkit.getConsoleSender(),msg);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("thuum.reload")) {
                SendMessage.sendMessage(player,msg);
            }
        }

        instance.reloadConfig();

        FileConfiguration config = instance.getConfig();
        for(ShoutCommand command : instance.commandMap.values()) {
            command.setPermissionMessage(SendMessage.format(null,
                    config.getString("pluginMessages.permission",
                            "#AA0000I'm sorry, but you do not have permission to perform instance command. " +
                                    "Please contact server administrators if you believe that instance is in error.")));
        }

        msg = "[TheThuum] Loading custom shouts.";
        SendMessage.sendMessage(Bukkit.getConsoleSender(),msg);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("thuum.reload")) {
                SendMessage.sendMessage(player,msg);
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, instance::registerCustomShouts);
        HandlerList.unregisterAll(instance);
        PluginManager pm = instance.getServer().getPluginManager();
        pm.registerEvents(instance.arngeir, instance);
        for(ShoutType blah : ShoutType.values()) {
            if (blah.shout instanceof Listener) {
                pm.registerEvents((Listener)blah.shout, instance);
            }
        }

        instance.commandTimer.cancel();
        instance.commandTimer = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            long avgTime = TPS.timeSinceTick(20) / 20;
            long currTime = TPS.timeSinceTick(1);
            CommandsAPI.execute(avgTime - currTime);
        }, 5, 1);

        msg = "[TheThuum] " + instance.getDescription().getVersion() + " loaded!";
        SendMessage.sendMessage(Bukkit.getConsoleSender(),msg);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("thuum.reload")) {
                SendMessage.sendMessage(player,msg);
            }
        }
    }
}

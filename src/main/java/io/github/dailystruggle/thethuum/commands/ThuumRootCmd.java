package io.github.dailystruggle.thethuum.commands;

import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.commandsapi.common.CommandsAPI;
import io.github.dailystruggle.commandsapi.common.CommandsAPICommand;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ThuumRootCmd extends BukkitTreeCommand {
    public ThuumRootCmd(Plugin plugin, @Nullable CommandsAPICommand parent) {
        super(plugin, parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        if(nextCommand!=null) return true;
        UUID uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : CommandsAPI.serverId;
        List<String> help = help(uuid,sender::hasPermission);
        for(String s : help) {
            SendMessage.sendMessage(sender,s);
        }
        return true;
    }

    @Override
    public String name() {
        return "thuum";
    }

    @Override
    public String permission() {
        return "thuum.see";
    }

    @Override
    public String description() {
        return "root for thuum plugin commands";
    }

    @Override
    public void msgBadParameter(UUID callerId, String parameterName, String parameterValue) {
        CommandSender sender;
        if(callerId.equals(CommandsAPI.serverId)) sender = Bukkit.getConsoleSender();
        else sender = Bukkit.getPlayer(callerId);
        if(sender == null) return;
        SendMessage.sendMessage(sender, ChatColor.YELLOW + "invalid parameter " + parameterName + ":" + parameterValue);
    }
}

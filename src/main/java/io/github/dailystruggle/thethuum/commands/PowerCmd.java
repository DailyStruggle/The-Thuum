package io.github.dailystruggle.thethuum.commands;

import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.commandsapi.common.CommandsAPI;
import io.github.dailystruggle.commandsapi.common.CommandsAPICommand;
import io.github.dailystruggle.thethuum.Plugin;
import io.github.dailystruggle.thethuum.shouts.Shout;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PowerCmd extends BukkitTreeCommand {
    int pwr;

    public PowerCmd(Plugin plugin, @NotNull CommandsAPICommand parent, int pwr) {
        super(plugin, parent);
        this.pwr = pwr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        if(nextCommand!=null) return true;
        if(!(sender instanceof Player)) {
            SendMessage.sendMessage(sender,
                    Plugin.getInstance().getConfig().getString(
                            "pluginMessages.consoleNotSupported","console shouts not supported"));
            return true;
        }
        HashMap<String, Shout> ShoutTable = io.github.dailystruggle.thethuum.Plugin.getInstance().arngeir.ShoutTable;

        StringBuilder say = new StringBuilder();
        String name = parent().name();
        Shout shout = ShoutTable.get(name);
        for(int i = 0; i < pwr && i < 3; ++i) {
            say.append(shout.words()[i]).append(" ");
        }
        say.deleteCharAt(say.length()-1);

        if(Bukkit.isPrimaryThread()) ((Player) sender).chat(say.toString());
        else Bukkit.getScheduler().runTask(Plugin.getInstance(),()->((Player) sender).chat(say.toString()));
        return false;
    }

    @Override
    public String name() {
        return String.valueOf(pwr);
    }

    @Override
    public String permission() {
        CommandsAPICommand parent = parent();
        String name = parent.name();
        Shout shout = io.github.dailystruggle.thethuum.Plugin.getInstance().arngeir.ShoutTable.get(name);
        return "thuum.shout." + shout.words()[0] + shout.words()[1] + shout.words()[2] + "." + pwr;
    }

    @Override
    public String description() {
        return "power level " + pwr;
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

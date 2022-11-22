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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShoutExecutor extends BukkitTreeCommand {
    private final String name;
    private final String desc;

    public ShoutExecutor(@Nullable CommandsAPICommand parent, String name) {
        super(Plugin.getInstance(), parent);

        Plugin instance = Plugin.getInstance();

        this.name = name;

        Shout shout = instance.arngeir.ShoutTable.get(name);
        if(shout==null) throw new NullPointerException();
        this.desc = shout.words()[3] + " - " + shout.words()[4];

        addSubCommand(new PowerCmd(instance,this,1));
        addSubCommand(new PowerCmd(instance,this,2));
        addSubCommand(new PowerCmd(instance,this,3));
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        if(nextCommand!=null) return true;
        if(!(sender instanceof Player)) {
            SendMessage.sendMessage(sender,"console shouts not supported");
            return true;
        }

        getCommandLookup().get("1").onCommand(((Player) sender).getUniqueId(),parameterValues,null);

        return true;
    }

    @Override
    public String name() {
        if(name == null) return "";
        return name;
    }

    @Override
    public String permission() {
        Shout shout = Plugin.getInstance().arngeir.ShoutTable.get(name);
        return "thuum.shout." + shout.words()[0] + shout.words()[1] + shout.words()[2];
    }

    @Override
    public String description() {
        return desc;
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

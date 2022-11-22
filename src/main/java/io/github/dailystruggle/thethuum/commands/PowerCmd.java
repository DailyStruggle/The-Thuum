package io.github.dailystruggle.thethuum.commands;

import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.commandsapi.common.CommandsAPICommand;
import io.github.dailystruggle.thethuum.GreyBeard;
import io.github.dailystruggle.thethuum.shouts.Shout;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PowerCmd extends BukkitTreeCommand {
    int pwr;

    public PowerCmd(Plugin plugin, @NotNull CommandsAPICommand parent, int pwr) {
        super(plugin, parent);
        if(parent == null) throw new IllegalArgumentException();
        this.pwr = pwr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        if(nextCommand!=null) return true;
        if(!(sender instanceof Player)) {
            SendMessage.sendMessage(sender,"console shouts not supported");
            return true;
        }
        Player player = (Player) sender;
        HashMap<String, Shout> ShoutTable = io.github.dailystruggle.thethuum.Plugin.getInstance().arngeir.ShoutTable;
        String parsed = parent().name();

        int audible = io.github.dailystruggle.thethuum.Plugin.getInstance().getConfig().getInt("display.audible command");
        if (audible > 0) {
            String string = io.github.dailystruggle.thethuum.Plugin.getInstance().getConfig().getString("display.color");
            if(string!=null) {
                StringBuilder say = new StringBuilder(ChatColor.valueOf(string.toUpperCase()).toString());

                String name = parent().name();

                Shout shout = ShoutTable.get(name);

                for(int i = 0; i < pwr; ++i) {
                    say.append(shout.words()[i].toUpperCase()).append(" ");
                }

                say.insert(say.length() - 1, '!');
                if (audible == 1) {
                    SendMessage.sendMessage(sender,say.toString());
                } else if (audible == 2) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        SendMessage.sendMessage(onlinePlayer,say.toString());
                    }
                }
            }
        }

        if(Bukkit.isPrimaryThread()) GreyBeard.shout(player.getUniqueId(), ShoutTable.get(parsed), pwr);
        else Bukkit.getScheduler().runTask(io.github.dailystruggle.thethuum.Plugin.getInstance(),
                () -> GreyBeard.shout(player.getUniqueId(), ShoutTable.get(parsed), pwr));

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

    }
}

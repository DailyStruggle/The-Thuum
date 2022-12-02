package io.github.dailystruggle.thethuum.commands;

import io.github.dailystruggle.thethuum.Plugin;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoutCommand extends Command {
    private final ShoutExecutor executor;

    public ShoutCommand(@NotNull String name, ShoutExecutor executor) {
        super(name);
        this.executor = executor;

        setPermissionMessage(SendMessage.format(null,
                Plugin.getInstance().getConfig().getString("pluginMessages.permission",
                        "#AA0000I'm sorry, but you do not have permission to perform this command. Please contact server administrators if you believe that this is in error.")));
        String[] words = Plugin.getInstance().arngeir.ShoutTable.get(name).words();
        setDescription(words[3] + " - " + words[4]);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!sender.hasPermission(Objects.requireNonNull(getPermission()))){
            SendMessage.sendMessage(sender,getPermissionMessage());
            return false;
        }
        return executor.onCommand(sender,this, commandLabel,args);
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(!sender.hasPermission(Objects.requireNonNull(getPermission()))){
            return new ArrayList<>();
        }
        return Objects.requireNonNull(executor.onTabComplete(sender, this, alias, args));
    }

    @Override
    public String getPermission() {
        return executor.permission();
    }
}

package io.github.dailystruggle.thethuum;


import io.github.dailystruggle.commandsapi.common.CommandsAPI;
import io.github.dailystruggle.commandsapi.common.localCommands.TreeCommand;
import io.github.dailystruggle.thethuum.commands.ReloadCmd;
import io.github.dailystruggle.thethuum.commands.ShoutCommand;
import io.github.dailystruggle.thethuum.commands.ShoutExecutor;
import io.github.dailystruggle.thethuum.commands.ThuumRootCmd;
import io.github.dailystruggle.thethuum.shouts.CustomShout;
import io.github.dailystruggle.thethuum.shouts.Shout;
import io.github.dailystruggle.thethuum.shouts.ShoutType;
import io.github.dailystruggle.thethuum.tools.TPS;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Plugin extends JavaPlugin {
    public GreyBeard arngeir;
    private static Plugin instance;
    public Logger log;
    public BukkitTask commandTimer = null;

    public Plugin() {
    }

    public static Plugin getInstance() {
        return instance;
    }

    public TreeCommand rootCmd;
    public final Map<String,ShoutCommand> commandMap = new ConcurrentHashMap<>();

    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        this.arngeir = new GreyBeard();
        this.log = Logger.getLogger("Minecraft");
        PluginManager pm = this.getServer().getPluginManager();

        rootCmd = new ThuumRootCmd(this,null);
        rootCmd.addSubCommand(new ReloadCmd(this,rootCmd));
        this.log.info("[TheThuum] Loading default shouts.");
        for(ShoutType shoutType : ShoutType.values()) {
            String[] words = shoutType.shout.words();
            this.arngeir.ShoutTable.put(words[0], shoutType.shout);
            this.arngeir.ShoutTable.put(words[0] + " " + words[1], shoutType.shout);
            this.arngeir.ShoutTable.put(words[0] + " " + words[1] + " " + words[2], shoutType.shout);
            registerShoutPermission(shoutType.name());
            ShoutCommand command = registerShoutCommand(shoutType.shout);
            if(command!=null) commandMap.put(shoutType.name(),command);
        }
        this.log.info("[TheThuum] Loading custom shouts.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::registerCustomShouts);
        pm.registerEvents(this.arngeir, this);
        for(ShoutType blah : ShoutType.values()) {
            if (blah.shout instanceof Listener) {
                pm.registerEvents((Listener)blah.shout, this);
            }
        }

        commandTimer = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            long avgTime = TPS.timeSinceTick(20) / 20;
            long currTime = TPS.timeSinceTick(1);
            CommandsAPI.execute(avgTime - currTime);
        }, 40, 1);

        this.log.info("The Thu'um " + this.getDescription().getVersion() + " loaded!");
    }

    public void registerCustomShouts() {
        List<?> customshouts = getConfig().getList("customshouts");
        if(customshouts == null) customshouts = new ArrayList<>();
        for(Object customShout : customshouts) {
            if(!(customShout instanceof Map)) continue;
            Map<?,?> customShoutConfig = (Map<?,?>)customShout;
            CustomShout constructShout = new CustomShout();
            constructShout.words[0] = String.valueOf(customShoutConfig.get("firstword"));
            constructShout.words[1] = String.valueOf(customShoutConfig.get("secondword"));
            constructShout.words[2] = String.valueOf(customShoutConfig.get("thirdword"));
            constructShout.words[3] = String.valueOf(customShoutConfig.get("name"));
            constructShout.words[4] = String.valueOf(customShoutConfig.get("description"));

            Object firstcommands = customShoutConfig.get("firstcommands");
            if(!(firstcommands instanceof List)) continue;
            constructShout.playerCommands.put(1, (List<?>) firstcommands);
            Object secondcommands = customShoutConfig.get("secondcommands");
            if(!(secondcommands instanceof List)) continue;
            constructShout.playerCommands.put(2, (List<?>) secondcommands);
            Object thirdcommands = customShoutConfig.get("thirdcommands");
            if(!(thirdcommands instanceof List)) continue;
            constructShout.playerCommands.put(3, (List<?>) thirdcommands);
            this.arngeir.ShoutTable.put(constructShout.words[0], constructShout);
            this.arngeir.ShoutTable.put(constructShout.words[0] + " " + constructShout.words[1], constructShout);
            this.arngeir.ShoutTable.put(constructShout.words[0] + " " + constructShout.words[1] + " " + constructShout.words[2], constructShout);
            String shoutName = constructShout.words[0]+constructShout.words[1]+constructShout.words[2];
            shoutName = shoutName.replaceAll(" ","").toLowerCase();
            registerShoutPermission(shoutName);
            registerShoutCommand(constructShout);
        }
    }

    public void registerShoutPermission(final String name) {
        PluginManager manager = Bukkit.getPluginManager();
        String s1 = "thuum.shout." + name.toLowerCase();
        String s2 = "thuum.ignorecooldown." + name.toLowerCase();
        if(manager.getPermission(s1+".1")==null) manager.addPermission(new Permission(s1+".1"));
        if(manager.getPermission(s1+".2")==null) manager.addPermission(new Permission(s1+".2"));
        if(manager.getPermission(s1+".3")==null) manager.addPermission(new Permission(s1+".3"));
        if(manager.getPermission(s2+".1")==null) manager.addPermission(new Permission(s2+".1"));
        if(manager.getPermission(s2+".2")==null) manager.addPermission(new Permission(s2+".2"));
        if(manager.getPermission(s2+".3")==null) manager.addPermission(new Permission(s2+".3"));
    }

    public ShoutCommand registerShoutCommand(final Shout shout) {
        CommandMap commandMap;
        try {
            Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
            return null;
        }

        String[] words = shout.words();
        Command command = commandMap.getCommand(words[0]);
        if(command !=null) command.unregister(commandMap);
        ShoutCommand shoutCommand = new ShoutCommand(words[0], new ShoutExecutor(null, words[0]));
        commandMap.register(Plugin.getInstance().getName(), shoutCommand);
        return shoutCommand;
    }

    public void onDisable() {
        if(commandTimer!=null) commandTimer.cancel();
        HandlerList.unregisterAll(this);
    }
}

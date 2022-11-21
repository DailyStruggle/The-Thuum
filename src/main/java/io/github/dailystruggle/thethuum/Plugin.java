package io.github.dailystruggle.thethuum;


import io.github.dailystruggle.thethuum.shouts.CustomShout;
import io.github.dailystruggle.thethuum.shouts.ShoutType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Plugin extends JavaPlugin {
    GreyBeard arngeir;
    private static Plugin instance;
    public Logger log;

    public Plugin() {
    }

    public static Plugin getInstance() {
        return instance;
    }

    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        instance = this;
        this.arngeir = new GreyBeard();
        this.log = Logger.getLogger("Minecraft");
        PluginManager pm = this.getServer().getPluginManager();
        this.log.info("[TheThuum] Loading default shouts.");

        for(ShoutType shoutType : ShoutType.values()) {
            this.arngeir.ShoutTable.put(shoutType.shout.words()[0], shoutType.shout);
            this.arngeir.ShoutTable.put(shoutType.shout.words()[0] + " " + shoutType.shout.words()[1], shoutType.shout);
            this.arngeir.ShoutTable.put(shoutType.shout.words()[0] + " " + shoutType.shout.words()[1] + " " + shoutType.shout.words()[2], shoutType.shout);
        }

        this.log.info("[TheThuum] Loading custom shouts.");

        for(Object customShout : getConfig().getList("customshouts",new ArrayList<>())) {
            if(!(customShout instanceof Map)) continue;
            Map<?,?> customShoutConfig = (Map<?,?>)customShout;
            CustomShout constructShout = new CustomShout();
            constructShout.words[0] = (String)customShoutConfig.get("firstword");
            constructShout.words[1] = (String)customShoutConfig.get("secondword");
            constructShout.words[2] = (String)customShoutConfig.get("thirdword");
            constructShout.words[3] = (String)customShoutConfig.get("name");
            constructShout.words[4] = (String)customShoutConfig.get("description");

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
        }

        pm.registerEvents(this.arngeir, this);

        for(ShoutType blah : ShoutType.values()) {
            if (blah.shout instanceof Listener) {
                pm.registerEvents((Listener)blah.shout, this);
            }
        }

        this.log.info("The Thu'um" + this.getDescription().getVersion() + "loaded!");
    }

    public void onDisable() {
    }
}

package io.github.dailystruggle.thethuum;

import io.github.dailystruggle.thethuum.shouts.Shout;
import io.github.dailystruggle.thethuum.shouts.ShoutType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class GreyBeard implements Listener {
    public HashMap<String, Shout> ShoutTable = new HashMap();
    Configuration shoutCooldowns;
    Hashtable<String, Set<Shout>> onCooldown = new Hashtable();

    public GreyBeard() {
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onPlayerChat(PlayerChatEvent event) {
        if (!event.isCancelled()) {
            String parsed = event.getMessage().toLowerCase().replaceAll("[^A-Za-z\\s]", "");
            String[] message = parsed.split(" ", 4);
            int length = message.length;
            if (length != 4) {
                if (this.ShoutTable.containsKey(parsed)) {
                    int power = message.length;
                    switch(Plugin.getInstance().getConfig().getInt("display.audible chat")) {
                        case 1:
                            event.getPlayer()
                                    .sendMessage(ChatColor.valueOf(Plugin.getInstance().getConfig().getString("display.color").toUpperCase()) + event.getMessage());
                        case 0:
                            event.setCancelled(true);
                            break;
                        case 2:
                            event.setMessage(ChatColor.valueOf(Plugin.getInstance().getConfig().getString("display.color").toUpperCase()) + event.getMessage());
                    }

                    shout(event.getPlayer(), this.ShoutTable.get(parsed), power);
                }
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!event.isCancelled()) {
            Player dovahkiin = event.getPlayer();
            String[] message = event.getMessage().split(" ");
            message[0] = message[0].substring(1);
            if (message.length <= 2) {
                if (this.ShoutTable.containsKey(message[0])) {
                    int power = 1;
                    event.setCancelled(true);

                    try {
                        if (message.length == 2) {
                            power = Integer.parseInt(message[1]);
                        }
                    } catch (NumberFormatException var8) {
                        dovahkiin.sendMessage("Invalid parameter! Format is: /SHOUTNAME [power]");
                        return;
                    }

                    if (power < 0 || power > 3) {
                        dovahkiin.sendMessage("Invalid power! Must be 1, 2 or 3.");
                        return;
                    }

                    int audible = Plugin.getInstance().getConfig().getInt("display.audible command");
                    if (audible > 0) {
                        StringBuilder say = new StringBuilder(ChatColor.valueOf(Plugin.getInstance().getConfig().getString("display.color").toUpperCase()).toString());

                        for(int i = 0; i < power; ++i) {
                            say.append(this.ShoutTable.get(message[0]).words()[i].toUpperCase()).append(" ");
                        }

                        say.insert(say.length() - 1, '!');
                        if (audible == 1) {
                            dovahkiin.sendMessage(say.toString());
                        } else if (audible == 2) {
                            dovahkiin.chat(say.toString());
                        }
                    }

                    shout(event.getPlayer(), this.ShoutTable.get(message[0]), power);
                }
            }
        }
    }

    public static void shout(Player dragonBorn, Shout word, int level) {
        if (level <= 3 && level >= 0) {
            String shoutName = word.words()[0] + word.words()[1] + word.words()[2];
            if (dragonBorn.hasPermission("thuum.shout." + shoutName + "." + level)) {
                if (!dragonBorn.hasPermission("thuum.ignorecooldown." + shoutName + "." + level)
                        && !Plugin.getInstance().arngeir.putOnCooldown(dragonBorn, word, level)) {
                    dragonBorn.sendMessage(Plugin.getInstance().getConfig().getString("cooldown.alert message"));
                } else {
                    word.shout(dragonBorn, level);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int persistence = Plugin.getInstance().getConfig().getInt("cooldown.persistence");
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new GreyBeard.ClearCooldowns(event.getPlayer()), persistence * 20);
    }

    public boolean putOnCooldown(Player dovahkiin, Shout shout, int level) {
        String shoutName = shout.words()[0] + shout.words()[1] + shout.words()[2];
        int cooldownDuration = (int)(Plugin.getInstance().getConfig().getDoubleList("shouts." + shoutName).get(level - 1) * 20.0);
        if (Plugin.getInstance().getConfig().getBoolean("single cooldown", true)) {
            shout = ShoutType.FUSRODAH.shout;
        }

        if (!this.onCooldown.containsKey(dovahkiin.getName())) {
            this.onCooldown.put(dovahkiin.getName(), new HashSet());
        }

        if (this.onCooldown.get(dovahkiin.getName()).contains(shout)) {
            return false;
        } else {
            this.onCooldown.get(dovahkiin.getName()).add(shout);
            Cooldown task = new Cooldown(dovahkiin, shout);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), task, cooldownDuration);
            return true;
        }
    }

    public class ClearCooldowns implements Runnable {
        Player dovahkiin;

        public ClearCooldowns(Player player) {
            this.dovahkiin = player;
        }

        public void run() {
            if (!this.dovahkiin.isOnline()) {
                onCooldown.remove(this.dovahkiin.getName());
            }
        }
    }

    public class Cooldown implements Runnable {
        Player dovahkiin;
        Shout shout;

        public Cooldown(Player player, Shout shout) {
            this.dovahkiin = player;
            this.shout = shout;
        }

        public void run() {
            if (onCooldown.containsKey(this.dovahkiin.getName())) {
                onCooldown.get(this.dovahkiin.getName()).remove(this.shout);
                this.dovahkiin.sendMessage(Plugin.getInstance().getConfig().getString("cooldown.ready message"));
            }
        }
    }
}

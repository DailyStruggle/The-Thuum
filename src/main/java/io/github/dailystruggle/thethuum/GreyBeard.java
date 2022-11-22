package io.github.dailystruggle.thethuum;

import io.github.dailystruggle.thethuum.shouts.Shout;
import io.github.dailystruggle.thethuum.shouts.ShoutType;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class GreyBeard implements Listener {
    public HashMap<String, Shout> ShoutTable = new HashMap<>();
    Hashtable<UUID, Set<Shout>> onCooldown = new Hashtable<>();

    public GreyBeard() {
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            String parsed = event.getMessage().toLowerCase().replaceAll("[^A-Za-z\\s]", "");
            String[] message = parsed.split(" ", 4);
            int length = message.length;
            if (length != 4) {
                if (this.ShoutTable.containsKey(parsed)) {
                    int power = message.length;
                    String string = Plugin.getInstance().getConfig().getString("display.color");
                    if(string!=null && !string.isEmpty()) {
                        switch (Plugin.getInstance().getConfig().getInt("display.audible chat")) {
                            case 1:
                                event.getPlayer()
                                        .sendMessage(ChatColor.valueOf(string.toUpperCase()) + event.getMessage());
                            case 0:
                                event.setCancelled(true);
                                break;
                            case 2:
                                String format = event.getFormat();
                                int i = format.indexOf("%s:");
                                if(i<0) format = ChatColor.valueOf(string.toUpperCase()) + format;
                                else format = format.substring(0,i) + ChatColor.valueOf(string.toUpperCase()) + format.substring(i);
                                event.setFormat(format);
                        }

                        if(Bukkit.isPrimaryThread()) shout(event.getPlayer().getUniqueId(), this.ShoutTable.get(parsed), power);
                        else Bukkit.getScheduler().runTask(Plugin.getInstance(),
                                () -> shout(event.getPlayer().getUniqueId(), this.ShoutTable.get(parsed), power));
                    }
                }
            }
        }
    }

//    @EventHandler(
//            priority = EventPriority.HIGH
//    )
//    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
//        if (!event.isCancelled()) {
//            Player dovahkiin = event.getPlayer();
//            String[] message = event.getMessage().split(" ");
//            message[0] = message[0].substring(1);
//            if (message.length <= 2) {
//                if (this.ShoutTable.containsKey(message[0])) {
//                    int power = 1;
//                    event.setCancelled(true);
//
//                    try {
//                        if (message.length == 2) {
//                            power = Integer.parseInt(message[1]);
//                        }
//                    } catch (NumberFormatException var8) {
//                        SendMessage.sendMessage(dovahkiin,"Invalid parameter! Format is: /SHOUTNAME [power]");
//                        return;
//                    }
//
//                    if (power < 0 || power > 3) {
//                        SendMessage.sendMessage(dovahkiin,"Invalid power! Must be 1, 2 or 3.");
//                        return;
//                    }
//
//                    int audible = Plugin.getInstance().getConfig().getInt("display.audible command");
//                    if (audible > 0) {
//                        String string = Plugin.getInstance().getConfig().getString("display.color");
//                        if(string!=null) {
//                            StringBuilder say = new StringBuilder(ChatColor.valueOf(string.toUpperCase()).toString());
//
//                            for(int i = 0; i < power; ++i) {
//                                say.append(this.ShoutTable.get(message[0]).words()[i].toUpperCase()).append(" ");
//                            }
//
//                            say.insert(say.length() - 1, '!');
//                            if (audible == 1) {
//                                SendMessage.sendMessage(dovahkiin,say.toString());
//                            } else if (audible == 2) {
//                                dovahkiin.chat(say.toString());
//                            }
//                        }
//                    }
//
//                    shout(event.getPlayer().getUniqueId(), this.ShoutTable.get(message[0]), power);
//                }
//            }
//        }
//    }

    public static void shout(UUID dragonBorn, Shout word, int level) {
        Player p = Bukkit.getPlayer(dragonBorn);
        if(p == null || !p.isOnline()) return;
        if (level <= 3 && level >= 0) {
            Plugin instance = Plugin.getInstance();
            String shoutName = word.words()[0] + word.words()[1] + word.words()[2];
            if (p.hasPermission("thuum.shout." + shoutName + "." + level)) {
                if (!p.hasPermission("thuum.ignorecooldown." + shoutName + "." + level)
                        && !instance.arngeir.putOnCooldown(p, word, level)) {
                    String string = instance.getConfig().getString("cooldown.alert message");
                    if(string!=null && !string.isEmpty()) p.sendMessage(string);
                } else {
                    word.shout(dragonBorn, level);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int persistence = Plugin.getInstance().getConfig().getInt("cooldown.persistence");
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new GreyBeard.ClearCooldowns(event.getPlayer().getUniqueId()), persistence * 20L);
    }

    public boolean putOnCooldown(Player dovahkiin, Shout shout, int level) {
        String shoutName = shout.words()[0] + shout.words()[1] + shout.words()[2];
        int cooldownDuration = (int)(Plugin.getInstance().getConfig().getDoubleList("shouts." + shoutName).get(level - 1) * 20.0);
        if (Plugin.getInstance().getConfig().getBoolean("single cooldown", true)) {
            shout = ShoutType.FUSRODAH.shout;
        }

        if (!this.onCooldown.containsKey(dovahkiin.getUniqueId())) {
            this.onCooldown.put(dovahkiin.getUniqueId(), new HashSet<>());
        }

        if (this.onCooldown.get(dovahkiin.getUniqueId()).contains(shout)) {
            return false;
        } else {
            this.onCooldown.get(dovahkiin.getUniqueId()).add(shout);
            Cooldown task = new Cooldown(dovahkiin.getUniqueId(), shout);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), task, cooldownDuration);
            return true;
        }
    }

    public class ClearCooldowns implements Runnable {
        UUID uuid;

        public ClearCooldowns(UUID player) {
            this.uuid = player;
        }

        public void run() {
            onCooldown.remove(this.uuid);
        }
    }

    public class Cooldown implements Runnable {
        UUID uuid;
        Shout shout;

        public Cooldown(UUID uuid, Shout shout) {
            this.uuid = uuid;
            this.shout = shout;
        }

        public void run() {
            if (onCooldown.containsKey(uuid)) {
                onCooldown.get(uuid).remove(this.shout);
                String string = Plugin.getInstance().getConfig().getString("cooldown.ready message");
                if(string!=null && !string.isEmpty()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if(player!=null && player.isOnline()) player.sendMessage(string);
                }
            }
        }
    }
}

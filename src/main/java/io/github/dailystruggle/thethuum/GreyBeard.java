package io.github.dailystruggle.thethuum;

import io.github.dailystruggle.thethuum.shouts.Shout;
import io.github.dailystruggle.thethuum.shouts.ShoutType;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
            String eventMessage = event.getMessage();
            String parsed = eventMessage.toLowerCase().replaceAll("[^A-Za-z\\s]", "");
            String[] message = parsed.split(" ", 4);
            int length = message.length;
            if (length != 4) {
                if (this.ShoutTable.containsKey(parsed)) {
                    Shout shout = this.ShoutTable.get(parsed);
                    String shoutPermStr = shout.words()[0]+shout.words()[1]+shout.words()[2];
                    int power = message.length;
                    if (event.getPlayer().hasPermission("thuum.shout." + shoutPermStr + "." + power)) {
                        String colorStr = Plugin.getInstance().getConfig().getString("display.color");
                        Bukkit.getLogger().severe(colorStr);
                        colorStr = SendMessage.format(event.getPlayer(), colorStr);
                        switch (Plugin.getInstance().getConfig().getInt("display.audible chat")) {
                            case 1:
                                event.getPlayer().sendMessage(eventMessage);
                            case 0:
                                event.setCancelled(true);
                                break;
                            case 2:
                                event.setMessage(colorStr + eventMessage);
                        }

                        if (Bukkit.isPrimaryThread())
                            shout(event.getPlayer().getUniqueId(), shout, power);
                        else Bukkit.getScheduler().runTask(Plugin.getInstance(),
                                () -> shout(event.getPlayer().getUniqueId(), shout, power));
                    }
                }
            }
        }
    }

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

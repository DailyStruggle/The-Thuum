package io.github.dailystruggle.thethuum.shouts;

import io.github.dailystruggle.thethuum.EffectTracker;
import io.github.dailystruggle.thethuum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.UUID;

public class FeimZiiGron implements Shout, Listener {
    private final String[] words = new String[]{"feim", "zii", "gron", "Become Ethereal", "Makes you unable to take damage."};
    EffectTracker invincible = new EffectTracker();
    final int[] duration = new int[]{8, 13, 18};

    public FeimZiiGron() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dovahkiin, int level) {
        invincible.add(dovahkiin, this.duration[level - 1] * 20);
        FeimZiiGron.FeimZiiGronGlow task = new FeimZiiGron.FeimZiiGronGlow(dovahkiin);
        task.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.getInstance(), task, 0L, 10L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (invincible.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }

        if (event instanceof EntityDamageByEntityEvent) {
            invincible.remove(((EntityDamageByEntityEvent)event).getDamager().getUniqueId());
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            if (invincible.containsKey(event.getEntity().getUniqueId()) && event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
                event.setCancelled(true);
            }
        }
    }

    private class FeimZiiGronGlow implements Runnable {
        int id;
        UUID dovahkiin;

        FeimZiiGronGlow(UUID dovahkiin) {
            this.dovahkiin = dovahkiin;
        }

        public void run() {
            Player p = Bukkit.getPlayer(dovahkiin);
            if(p == null || !p.isOnline()) return;
            if (invincible.containsKey(this.dovahkiin)) {
                p.getWorld().playEffect(p.getLocation().add(0.0, 1.0, 0.0), Effect.ENDER_SIGNAL, 0);
            } else {
                Bukkit.getScheduler().cancelTask(this.id);
            }
        }
    }
}

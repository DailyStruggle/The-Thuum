package io.github.dailystruggle.thethuum.shouts;




import io.github.dailystruggle.thethuum.EffectTracker;
import io.github.dailystruggle.thethuum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LaasYahNir implements Shout {
    private final String[] words = new String[]{"laas", "yah", "nir", "Aura Whisper", "Makes nearby living creatures glow."};
    EffectTracker glowing = new EffectTracker();
    LaasYahNir.Glow task;

    public LaasYahNir() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(Player dovahkiin, int level) {
        if (this.task == null) {
            this.task = new LaasYahNir.Glow();
            this.task.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.getInstance(), this.task, 0L, 20L);
        }

        List<Entity> entities = new LinkedList<>();

        for(Entity test : dovahkiin.getNearbyEntities(90.0, 90.0, 90.0)) {
            if (test instanceof LivingEntity) {
                entities.add(test);
            }
        }

        this.glowing.addAll(entities, 200 * level);
    }

    class Glow implements Runnable {
        int id;

        Glow() {
        }

        public void run() {
            if (glowing.isEmpty()) {
                Bukkit.getScheduler().cancelTask(this.id);
                LaasYahNir.this.task = null;
            } else {
                for(UUID id : glowing.keySet()) {
                    Entity glows = Bukkit.getEntity(id);
                    if(glows == null) continue;
                    glows.getWorld().playEffect(glows.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                }
            }
        }
    }
}


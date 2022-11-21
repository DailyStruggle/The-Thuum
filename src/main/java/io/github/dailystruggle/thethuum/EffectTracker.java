package io.github.dailystruggle.thethuum;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Hashtable;
import java.util.UUID;

public class EffectTracker extends Hashtable<UUID, EffectTracker.EffectCooldown> {
    public EffectTracker() {
    }

    public void add(Entity effectsThis, int duration) {
        EffectCooldown cooldown = new EffectCooldown(effectsThis);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), cooldown, duration);
        this.put(effectsThis.getUniqueId(), cooldown);
    }

    public void addAll(Collection<Entity> effectsThese, int duration) {
        EffectCooldown cooldown = new EffectCooldownSet(effectsThese);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), cooldown, duration);

        for(Entity effectsThis : effectsThese) {
            this.put(effectsThis.getUniqueId(), cooldown);
        }
    }

    public class EffectCooldown implements Runnable {
        Entity target;

        public EffectCooldown(Entity effectsThis) {
            this.target = effectsThis;
        }

        public void run() {
            if (containsKey(this.target) && get(this.target) == this) {
                remove(this.target);
            }
        }
    }

    public class EffectCooldownSet extends EffectCooldown {
        Collection<Entity> targets;

        public EffectCooldownSet(Collection<Entity> effectsThese) {
            super(null);
            this.targets = effectsThese;
        }

        @Override
        public void run() {
            for(Entity target : this.targets) {
                if (containsKey(target) && get(target) == this) {
                    remove(target);
                }
            }
        }
    }
}

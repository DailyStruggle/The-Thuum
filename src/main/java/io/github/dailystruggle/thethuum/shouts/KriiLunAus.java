package io.github.dailystruggle.thethuum.shouts;




import io.github.dailystruggle.thethuum.EffectTracker;
import io.github.dailystruggle.thethuum.Shared;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.UUID;

public class KriiLunAus implements Shout, Listener {
    String[] words = new String[]{"krii", "lun", "aus", "Marked For Death", "Reduces armor and drains HP."};
    EffectTracker marked = new EffectTracker();
    int[] damage = new int[]{8196, 8260, 16420};

    public KriiLunAus() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dovahkiin, int level) {
        Player p = Bukkit.getPlayer(dovahkiin);
        if(p == null || !p.isOnline()) return;
        HashSet<Entity> addThese = new HashSet<>();

        for(Entity victim : Shared.getAreaOfEffect(p, 4, 10)) {
            if (victim instanceof LivingEntity) {
                addThese.add(victim);
                ((LivingEntity)victim).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1200, this.damage[level - 1]));
            }
        }

        this.marked.addAll(addThese, 1200);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (this.marked.contains(event.getEntity())) {
            event.setDamage(event.getDamage() + 1);
        }
    }
}


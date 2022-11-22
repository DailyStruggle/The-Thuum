package io.github.dailystruggle.thethuum.shouts;




import io.github.dailystruggle.thethuum.EffectTracker;
import io.github.dailystruggle.thethuum.Shared;
import io.github.dailystruggle.thethuum.tools.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KaanDremOv implements Shout, Listener {
    private final String[] words = new String[]{"kaan", "drem", "ov", "Kyne's Peace", "Makes creatures peaceful."};
    EffectTracker peaced = new EffectTracker();

    public KaanDremOv() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dovahkiin, int level) {
        Player p = Bukkit.getPlayer(dovahkiin);
        if(p == null || !p.isOnline()) return;
        Set<Entity> peaceThese = new HashSet<>();

        for(Entity toPeace : Shared.getAreaOfEffect(p, 4 + 2 * level, 15 + 5 * level)) {
            if (toPeace instanceof Creature) {
                ((Creature)toPeace).setTarget(null);
                peaceThese.add(toPeace);
            }
        }

        SendMessage.sendMessage(p, "You calmed " + peaceThese.size() + " creatures.");
        this.peaced.addAll(peaceThese, (30 + 20 * level) * 20);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (this.peaced.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}

package io.github.dailystruggle.thethuum.shouts;



import io.github.dailystruggle.thethuum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StrunBahQo implements Shout {
    private final String[] words = new String[]{"strun", "bah", "qo", "Storm Call", "Summons a storm that hits things with lightning."};
    Random RNG = new Random();

    public StrunBahQo() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dovahkiin, int level) {
        Player p = Bukkit.getPlayer(dovahkiin);
        if(p == null || !p.isOnline()) return;
        int duration = 1200 * level;
        new StrunBahQo.lightningStorm(duration, p, Plugin.getInstance(), Plugin.getInstance().getServer().getScheduler());
        World world = p.getWorld();
        world.setStorm(true);
        world.setThundering(true);
    }

    private class lightningStorm implements Runnable {
        int duration;
        Player dovahkiin;
        JavaPlugin plugin;
        BukkitScheduler scheduler;
        boolean stormBefore;
        boolean thunderBefore;

        public lightningStorm(int duration, Player dovahkiin, JavaPlugin plugin, BukkitScheduler scheduler) {
            this.dovahkiin = dovahkiin;
            this.duration = duration;
            this.plugin = plugin;
            this.scheduler = scheduler;
            this.stormBefore = dovahkiin.getWorld().hasStorm();
            this.thunderBefore = dovahkiin.getWorld().isThundering();
            this.schedule();
        }

        public void run() {
            List<Entity> zapPool = this.dovahkiin.getNearbyEntities(30.5, 30.5, 30.5);
            if (zapPool.size() > 0) {
                int tries = 0;

                Entity zapMe;
                do {
                    zapMe = zapPool.get(StrunBahQo.this.RNG.nextInt(zapPool.size()));
                    ++tries;
                } while((!(zapMe instanceof LivingEntity) || zapMe.isDead()) && tries < zapPool.size() * 2);

                zapMe.getWorld().strikeLightning(zapMe.getLocation());
            }

            if (this.duration > 0) {
                this.schedule();
            } else {
                this.dovahkiin.getWorld().setStorm(this.stormBefore);
                this.dovahkiin.getWorld().setThundering(this.thunderBefore);
            }
        }

        private void schedule() {
            int delay = 60 + StrunBahQo.this.RNG.nextInt(60);
            this.duration -= delay;
            this.scheduler.scheduleSyncDelayedTask(this.plugin, this, delay);
        }
    }
}

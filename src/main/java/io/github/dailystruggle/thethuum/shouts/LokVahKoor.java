package io.github.dailystruggle.thethuum.shouts;



import io.github.dailystruggle.thethuum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LokVahKoor implements Shout {
    private final String[] words = new String[]{"lok", "vah", "koor", "Clear Skies", "Temporarily calms a storm."};

    public LokVahKoor() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dovahkiin, int level) {
        Player p = Bukkit.getPlayer(dovahkiin);
        if(p == null || !p.isOnline()) return;
        World world = p.getWorld();
        if (level != 3) {
            int stormDuration;
            int thunderDuration = 0;
            if (world.hasStorm()) {
                stormDuration = world.getWeatherDuration();
                if (world.isThundering()) {
                    thunderDuration = world.getThunderDuration();
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Storm(world, thunderDuration, stormDuration), level * 200L);
            }
        }

        world.setStorm(false);
    }

    private static class Storm implements Runnable {
        World world;
        int stormDuration;
        int thunderDuration;

        public Storm(World world, int thunderDuration, int stormDuration) {
            this.world = world;
            this.thunderDuration = thunderDuration;
            this.stormDuration = stormDuration;
        }

        public void run() {
            this.world.setStorm(true);
            if (this.thunderDuration != 0) {
                this.world.setThundering(true);
            }

            this.world.setWeatherDuration(this.stormDuration);
            this.world.setThunderDuration(this.thunderDuration);
        }
    }
}


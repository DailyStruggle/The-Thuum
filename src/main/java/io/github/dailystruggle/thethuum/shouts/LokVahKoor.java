package io.github.dailystruggle.thethuum.shouts;



import io.github.dailystruggle.thethuum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LokVahKoor implements Shout {
    private final String[] words = new String[]{"lok", "vah", "koor", "Clear Skies", "Temporarily calms a storm."};

    public LokVahKoor() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(Player dovahkiin, int level) {
        World world = dovahkiin.getWorld();
        if (level != 3) {
            int stormDuration = 0;
            int thunderDuration = 0;
            if (world.hasStorm()) {
                stormDuration = world.getWeatherDuration();
                if (world.isThundering()) {
                    thunderDuration = world.getThunderDuration();
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Storm(world, thunderDuration, stormDuration), level * 200);
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


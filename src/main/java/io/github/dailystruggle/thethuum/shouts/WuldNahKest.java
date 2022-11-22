package io.github.dailystruggle.thethuum.shouts;



import io.github.dailystruggle.thethuum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class WuldNahKest implements Shout {
    static final double[] multiplier = new double[]{5.5, 5.8, 6.6};

    public WuldNahKest() {
    }

    public String[] words() {
        return new String[]{"wuld", "nah", "kest", "Whirlwind Sprint", "Rushes the dovahkiin forward."};
    }

    public void shout(UUID dovahkiin, int level) {
        Player p = Bukkit.getPlayer(dovahkiin);
        if(p == null || !p.isOnline()) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new SetSpeed(p.getVelocity(), p, 1), 3 + level);
        Vector heading = p.getEyeLocation().getDirection();
        Vector dash = new Vector();
        dash.copy(heading).setY(0).normalize();
        dash.multiply(multiplier[level - 1]).setY(0.3);
        p.setVelocity(dash);
        WuldNahKest.SetSpeed task = new SetSpeed(dash, p, 2 + level);
        task.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.getInstance(), task, 0L, 1L);
        p.getWorld().createExplosion(p.getLocation(), 0.0F);
    }

    static class SetSpeed implements Runnable {
        int id;
        int ticks;
        Vector speed;
        Player dovahkiin;

        SetSpeed(Vector speed, Player dovahkiin, int ticks) {
            this.speed = speed;
            this.dovahkiin = dovahkiin;
            this.ticks = ticks;
        }

        public void run() {
            this.dovahkiin.setVelocity(this.speed);
            if (this.ticks-- == 0) {
                Bukkit.getScheduler().cancelTask(this.id);
            }
        }
    }
}


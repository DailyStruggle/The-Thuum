package io.github.dailystruggle.thethuum.shouts;



import io.github.dailystruggle.thethuum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WuldNahKest implements Shout {
    static final double[] multiplier = new double[]{5.5, 5.8, 6.6};

    public WuldNahKest() {
    }

    public String[] words() {
        return new String[]{"wuld", "nah", "kest", "Whirlwind Sprint", "Rushes the dovahkiin forward."};
    }

    public void shout(Player dovahkiin, int level) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new WuldNahKest.SetSpeed(dovahkiin.getVelocity(), dovahkiin, 1), 3 + level);
        Vector heading = dovahkiin.getEyeLocation().getDirection();
        Vector dash = new Vector();
        dash.copy(heading).setY(0).normalize();
        dash.multiply(multiplier[level - 1]).setY(0.3);
        dovahkiin.setVelocity(dash);
        WuldNahKest.SetSpeed task = new WuldNahKest.SetSpeed(dash, dovahkiin, 2 + level);
        task.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.getInstance(), task, 0L, 1L);
        dovahkiin.getWorld().createExplosion(dovahkiin.getLocation(), 0.0F);
    }

    class SetSpeed implements Runnable {
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


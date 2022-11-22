package io.github.dailystruggle.thethuum.shouts;





import io.github.dailystruggle.thethuum.Plugin;
import io.github.dailystruggle.thethuum.Shared;
import io.github.dailystruggle.thethuum.delays.Explosion;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class FusRoDah implements Shout {
    private final String[] words = new String[]{"fus", "ro", "dah", "Unrelenting Force", "Sends mobs and items flying."};
    final double[] fusHoriStrength = new double[]{0.5, 2.0, 7.0};
    final double[] fusVertStrength = new double[]{0.5, 0.7, 1.5};

    public FusRoDah() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dragonBorn, int level) {
        int distance = 5 * level;
        Player p = Bukkit.getPlayer(dragonBorn);
        if(p == null) return;
        Vector heading = p.getEyeLocation().getDirection();
        Vector blastVector = new Vector();
        blastVector.copy(heading).setY(0).normalize();
        blastVector.multiply(this.fusHoriStrength[level - 1]).setY(this.fusVertStrength[level - 1]);

        for(Entity victim : Shared.getAreaOfEffect(p, 4, distance)) {
            victim.setVelocity(victim.getVelocity().add(blastVector));
        }

        p.getWorld().playEffect(p.getLocation(), Effect.GHAST_SHOOT, 0, distance + 10);
        if (level >= 2) {
            World world = p.getWorld();
            List<Block> sight = p.getLineOfSight(null, 4);
            world.createExplosion(sight.get(sight.size() - 1).getLocation(), 0.0F);
        }

        if (level == 3) {
            List<Block> sight = p.getLineOfSight(null, 32);

            for(int i = 8; i < 32 && i < sight.size(); i += 6) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Explosion(sight.get(i).getLocation(), 0, false), i / 3);
            }
        }
    }
}

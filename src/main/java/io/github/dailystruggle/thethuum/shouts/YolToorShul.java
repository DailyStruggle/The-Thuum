package io.github.dailystruggle.thethuum.shouts;

import io.github.dailystruggle.thethuum.Plugin;
import io.github.dailystruggle.thethuum.delays.RemoveEntity;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

import java.util.LinkedList;

public class YolToorShul implements Shout {
    private final String[] words = new String[]{"yol", "toor", "shul", "Fire Breath", "Sets things in front of you on fire."};

    public YolToorShul() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(Player dovahkiin, int level) {
        if (level <= 3 && level >= 0) {
            Location location = dovahkiin.getEyeLocation();
            Vector trajectory = new Vector();
            trajectory.copy(location.getDirection()).normalize();
            Location spawnFireball = location.clone().add(trajectory);
            LinkedList<Vector> Lateral = new LinkedList<>();
            Vector LateralSide = new Vector(0, 1, 0).crossProduct(trajectory).normalize();
            Vector LateralTop = new Vector().copy(trajectory).crossProduct(LateralSide).normalize();
            Lateral.add(LateralTop);
            Lateral.add(new Vector().zero().subtract(LateralTop));
            Lateral.add(LateralSide);

            for(int i = level - 1; i > 0; --i) {
                LinkedList<Vector> newLateral = new LinkedList<>();

                for(Vector combineOne : Lateral) {
                    for(Vector combineTwo : Lateral) {
                        Vector addMe = new Vector().copy(combineOne).add(combineTwo);
                        if (level != 3 || i != 1) {
                            addMe.normalize();
                        }

                        newLateral.add(addMe);
                    }
                }

                Lateral.addAll(newLateral);
            }

            LinkedList<Vector> newLateral = new LinkedList<>();

            for(Vector flipMe : Lateral) {
                newLateral.add(new Vector().zero().subtract(flipMe));
            }

            Lateral.addAll(newLateral);
            Lateral.add(new Vector().zero());

            for(Vector offset : Lateral) {
                World world = location.getWorld();
                if(world == null) return;
                Fireball fireball = location.getWorld().spawn(spawnFireball.clone().add(offset), SmallFireball.class);
                fireball.setVelocity(trajectory);
                fireball.setDirection(trajectory);
                fireball.setShooter(dovahkiin);
                fireball.setIsIncendiary(true);
                fireball.setYield(0.4F);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new RemoveEntity(fireball), 15L);
            }

            dovahkiin.getWorld().playEffect(dovahkiin.getLocation(), Effect.BLAZE_SHOOT, 0, 40);
        }
    }
}


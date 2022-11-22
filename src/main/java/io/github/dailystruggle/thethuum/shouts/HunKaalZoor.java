package io.github.dailystruggle.thethuum.shouts;


import io.github.dailystruggle.thethuum.Plugin;
import io.github.dailystruggle.thethuum.delays.RemoveEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class HunKaalZoor implements Shout {
    String[] words = new String[]{"hun", "kaal", "zoor", "Call of Valor", "Summons a hero of Sovngarde."};
    private final EntityType[] heroes = new EntityType[]{EntityType.SNOWMAN, EntityType.WOLF, EntityType.IRON_GOLEM};

    public HunKaalZoor() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dovahkiin, int level) {
        Player p = Bukkit.getPlayer(dovahkiin);
        if(p == null || !p.isOnline()) return;
        Location spawnHere = p.getLastTwoTargetBlocks(null, 30).get(0).getLocation();
        World world = spawnHere.getWorld();
        if(world == null) return;
        LivingEntity hero = (LivingEntity) world.spawnEntity(spawnHere, this.heroes[level - 1]);
        if (hero instanceof Tameable) {
            ((Tameable)hero).setOwner(p);
        } else if (hero instanceof IronGolem) {
            ((IronGolem)hero).setPlayerCreated(true);
        }

        hero.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 20));
        world.createExplosion(spawnHere, 0.0F, false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new RemoveEntity(hero, true), 1200L);
    }
}

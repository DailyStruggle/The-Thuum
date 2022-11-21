package io.github.dailystruggle.thethuum.shouts;

import org.bukkit.entity.Player;

public interface Shout {
    String[] words();

    void shout(Player var1, int var2);
}
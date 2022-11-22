package io.github.dailystruggle.thethuum.shouts;

import java.util.UUID;

public interface Shout {
    String[] words();

    void shout(UUID var1, int var2);
}
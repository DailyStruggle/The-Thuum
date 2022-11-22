package io.github.dailystruggle.thethuum.shouts;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomShout implements Shout {
    public HashMap<Integer, List<?>> playerCommands = new HashMap<>();
    public String[] words = new String[]{"", "", "", "", ""};

    public CustomShout() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(UUID dovahkiin, int level) {
        Player p = Bukkit.getPlayer(dovahkiin);
        if(p == null || !p.isOnline()) return;
        List<String> doThese = this.playerCommands.get(level).stream().map(Object::toString).collect(Collectors.toList());
        for(String doThis : doThese) {
            p.performCommand(doThis);
        }
    }
}

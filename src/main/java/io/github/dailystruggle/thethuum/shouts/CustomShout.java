package io.github.dailystruggle.thethuum.shouts;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class CustomShout implements Shout {
    public HashMap<Integer, List<String>> playerCommands = new HashMap<>();
    public String[] words = new String[]{"", "", "", "", ""};

    public CustomShout() {
    }

    public String[] words() {
        return this.words;
    }

    public void shout(Player dovahkiin, int level) {
        List<String> doThese = this.playerCommands.get(level);
        if (doThese != null) {
            for(String doThis : doThese) {
                dovahkiin.performCommand(doThis);
            }
        }
    }
}

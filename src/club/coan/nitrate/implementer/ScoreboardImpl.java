package club.coan.nitrate.implementer;

import club.coan.nitrate.Nitrate;
import club.coan.queue.QueuePlugin;
import club.coan.rinku.scoreboard.AssembleAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardImpl implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        return Nitrate.getLang().convert(Nitrate.getLang().getScoreboardTitle(), player);
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        if (QueuePlugin.getQueueManager().getPlayersQueue(player.getUniqueId()) == null) {
            Nitrate.getLang().getScoreboardNormal().forEach(str -> lines.add(Nitrate.getLang().convert(str, player)));
        } else {
            Nitrate.getLang().getScoreboardQueue().forEach(str -> lines.add(Nitrate.getLang().convert(str, player)));
        }
        return lines;
    }
}

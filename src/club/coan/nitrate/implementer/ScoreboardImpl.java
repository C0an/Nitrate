package club.coan.nitrate.implementer;

import club.coan.nitrate.Nitrate;
import org.bukkit.entity.Player;
import rip.protocol.plib.scoreboard.ScoreGetter;
import rip.protocol.plib.scoreboard.ScoreboardConfiguration;
import rip.protocol.plib.scoreboard.TitleGetter;
import rip.protocol.plib.util.LinkedList;
import rip.protocol.pqueue.pQueue;

public class ScoreboardImpl implements ScoreGetter {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration config = new ScoreboardConfiguration();
        config.setTitleGetter(TitleGetter.forStaticString(Nitrate.getLang().convert(Nitrate.getLang().getScoreboardTitle(), null)));
        config.setScoreGetter(new ScoreboardImpl());
        return config;
    }


    @Override
    public void getScores(LinkedList<String> list, Player player) {
        if (pQueue.getQueueManager().getPlayersQueue(player.getUniqueId()) == null) {
            Nitrate.getLang().getScoreboardNormal().forEach(str -> list.add(Nitrate.getLang().convert(str, player)));
        } else {
            Nitrate.getLang().getScoreboardQueue().forEach(str -> list.add(Nitrate.getLang().convert(str, player)));
        }
    }
}

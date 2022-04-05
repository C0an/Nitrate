package club.coan.nitrate.tab;

import club.coan.nitrate.Nitrate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.protocol.bridge.BridgeShared;
import rip.protocol.bridge.bukkit.BukkitAPI;
import rip.protocol.bridge.shared.profile.Profile;
import rip.protocol.bridge.shared.ranks.Rank;
import rip.protocol.plib.tab.LayoutProvider;
import rip.protocol.plib.tab.TabLayout;
import rip.protocol.plib.util.PlayerUtils;
import rip.protocol.plib.util.UUIDUtils;
import rip.protocol.pqueue.pQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class HubTab implements LayoutProvider {

    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);

        switch (Nitrate.getLang().getTabMode()) {
            case REGULAR: {
                Nitrate.getLang().getTabListOne().forEach((integer, s) -> layout.set(0, integer, Nitrate.getLang().convert(s, player)));
                Nitrate.getLang().getTabListTwo().forEach((integer, s) -> layout.set(1, integer, Nitrate.getLang().convert(s, player)));
                Nitrate.getLang().getTabListThree().forEach((integer, s) -> layout.set(2, integer, Nitrate.getLang().convert(s, player)));
                break;
            }
            case PLAYERLIST: {
                Nitrate.getLang().getTabListOne().forEach((integer, s) -> layout.set(0, integer, Nitrate.getLang().convert(s, player)));
                Nitrate.getLang().getTabListTwo().forEach((integer, s) -> layout.set(1, integer, Nitrate.getLang().convert(s, player)));
                Nitrate.getLang().getTabListThree().forEach((integer, s) -> layout.set(2, integer, Nitrate.getLang().convert(s, player)));

                int x = 0;
                int y = Nitrate.getLang().getTabCountPosition();

                for (Profile profile : orderedProfiles(player)) {
                    Player onlinePlayer = Bukkit.getPlayer(profile.getUuid());
                    int ping = PlayerUtils.getPing(onlinePlayer);

                    String displayName = onlinePlayer.getDisplayName();
                    if (onlinePlayer.hasMetadata("invisible")) {
                        displayName = ChatColor.GRAY + "*" + displayName;
                    }

                    layout.set(x++, y, displayName, ping);

                    if (x == 3 && y == 20) {
                        break;
                    }

                    if (x == 3) {
                        x = 0;
                        y++;
                    }
                }
                break;
            }
        }

        return layout;
    }

    private List<Profile> orderedProfiles(Player player) {
        List<Profile> profiles = new ArrayList<>();
        Bukkit.getOnlinePlayers().stream().filter(player::canSee).forEach(onlinePlayer -> profiles.add(BukkitAPI.getProfile(onlinePlayer)));
        profiles.sort(Comparator.comparingInt(profile -> BukkitAPI.getPlayerRank((Profile)profile).getPriority()).reversed());
        return profiles;
    }

    private String translate(Player player, String str) {
        String s = Nitrate.getLang().convert(str, player);
        if(s.startsWith("%queue")) {
            if(pQueue.getQueueManager().getPlayersQueue(player.getUniqueId()) == null) {
                return "";
            }else {
                s = s.replace("%queuehidden%", "");
            }
        }
        return s;
    }
}

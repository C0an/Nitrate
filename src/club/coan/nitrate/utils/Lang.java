package club.coan.nitrate.utils;

import club.coan.christian.bukkit.BukkitAPI;
import club.coan.christian.bukkit.status.ServerInfo;
import club.coan.christian.bukkit.status.property.ServerProperty;
import club.coan.nitrate.Nitrate;
import club.coan.nitrate.cache.CountCache;
import club.coan.queue.QueuePlugin;
import club.coan.rinku.other.BukkitUtils;
import club.coan.rinku.other.ItemUtils;
import club.coan.rinku.other.ServerConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Lang {

    private String scoreboardTitle, buildPermission, setSpawnCommand;
    private List<String> scoreboardNormal, scoreboardQueue, joinMessage;
    private Location spawnLocation;
    private boolean voidTP;
    private int voidTPPosition;

    public Lang() {
        update();
    }

    public void update() {
        NitrateItem.getItems().clear();
        FileConfiguration c = Nitrate.getInstance().getConfig();
        scoreboardTitle = ChatColor.translateAlternateColorCodes('&', c.getString("lang.scoreboard.title"));
        scoreboardNormal = c.getStringList("lang.scoreboard.normal");
        scoreboardQueue = c.getStringList("lang.scoreboard.queue");

        joinMessage = c.getStringList("lang.joinmessage");

        setSpawnCommand = ChatColor.translateAlternateColorCodes('&', c.getString("lang.commands.setspawn.callback"));


        spawnLocation = BukkitUtils.getLocation(c.getString("settings.spawnlocation"));
        buildPermission = c.getString("settings.buildpermission");
        voidTP = c.getBoolean("settings.voidtp.enabled");
        voidTPPosition = c.getInt("settings.voidtp.position");



        for(String i : c.getConfigurationSection("items").getKeys(false)) {
            new NitrateItem(i, c.getString("items." + i + ".name"), ItemUtils.get(c.getString("items." + i + ".material")), c.getInt("items." + i + ".amount"), c.getInt("items." + i + ".data"), c.getInt("items." + i + ".slot"), c.getStringList("items." + i + ".lore"), c.getString("items." + i + ".action"), false);
        }

        for(String i : c.getConfigurationSection("inventories").getKeys(false)) {
            String k = "inventories." + i + ".";
            List<NitrateItem> nitrateItems = new ArrayList<>();
            for(String it : c.getConfigurationSection("inventories." + i + ".items").getKeys(false)) {
                nitrateItems.add(new NitrateItem(i + "." + it, c.getString(k + "items." + it + ".name"), ItemUtils.get(c.getString(k + "items." + it+ ".material")), c.getInt(k + "items." + it + ".amount"), c.getInt(k + "items." + it + ".data"), c.getInt(k + "items." + it + ".slot"), c.getStringList(k + "items." + it+ ".lore"), c.getString(k + "items." + it + ".action"), true));
            }
            new NitrateInventory(i, nitrateItems, c.getString(k + "title"));
        }

        if(voidTP) Bukkit.getScheduler().runTaskTimerAsynchronously(Nitrate.getInstance(), () -> Bukkit.getOnlinePlayers().stream().filter(p -> p.getLocation().getBlockY() <= getVoidTPPosition()).forEach(p -> p.teleport(getSpawnLocation())), 0, 15);


    }


    public String convert(String m, Player p) {
        String conv;
        conv = m.replace("%online%", CountCache.GLOBAL_COUNT + "")
                .replace("%website%", ServerConfig.website)
                .replace("%twitter%", ServerConfig.twitter)
                .replace("%discord%", ServerConfig.discord)
                .replace("%store%", ServerConfig.store)
                .replace("%teamspeak%", ServerConfig.teamSpeak)
                .replace("%line%", BukkitAPI.LINE);

        if(conv.contains("%serverstatus:")) {
            String[] s = conv.split("%serverstatus:");
            conv = conv.replace("%serverstatus:" + s[1], "" + ServerInfo.getStatusFormatted(s[1].replaceAll("%", ""), true));
        }

        if(conv.contains("%onlineplayers:")) {
            String[] s = conv.split("%onlineplayers:");
            conv = conv.replace("%onlineplayers:" + s[1], "" + ServerInfo.getProperty(s[1].replaceAll("%", ""), ServerProperty.ONLINE) + " / " + ServerInfo.getProperty(s[1].replaceAll("%", ""), ServerProperty.MAXIMUM) );
        }

        if(p != null) conv = conv.replace("%rank%", BukkitAPI.getPlayerRank(p).getDisplayName())
                .replace("%rankcolor%", BukkitAPI.getPlayerRank(p).getColor())
                .replace("%username%", p.getName())
                .replace("%player%", p.getName());
        if(p != null && QueuePlugin.getQueueManager().getPlayersQueue(p.getUniqueId()) != null) {
            conv = conv.replace("%position%", "" + QueuePlugin.getQueueManager().getPlayersQueue(p.getUniqueId()).getPosition(p.getUniqueId()))
                    .replace("%queue%", QueuePlugin.getQueueManager().getPlayersQueue(p.getUniqueId()).getQueueName())
                    .replace("%est%", QueuePlugin.getQueueManager().getPlayersQueue(p.getUniqueId()).queueStatus(p.getUniqueId()))
                    .replace("%size%", "" + QueuePlugin.getQueueManager().getPlayersQueue(p.getUniqueId()).getPlayersInQueue().size());

        }
        return ChatColor.translateAlternateColorCodes('&', conv);
    }


}

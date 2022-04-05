package club.coan.nitrate.utils;

import club.coan.nitrate.Nitrate;
import club.coan.nitrate.cache.CountCache;
import club.coan.nitrate.tab.TabMode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.protocol.bridge.bukkit.BukkitAPI;
import rip.protocol.bridge.shared.status.ServerInfo;
import rip.protocol.bridge.shared.status.ServerProperty;
import rip.protocol.bridge.shared.status.impltype.FoxtrotHandler;
import rip.protocol.plib.configuration.serializers.LocationSerializer;
import rip.protocol.plib.util.ItemUtils;
import rip.protocol.plib.util.TimeUtil;
import rip.protocol.plib.util.TimeUtils;
import rip.protocol.pqueue.data.Queue;
import rip.protocol.pqueue.pQueue;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Lang {

    private String scoreboardTitle, setSpawnCommand;
    private String website, twitter, store, teamspeak, discord, loadingText = "Loading", toggledPlayersOn, toggledPlayersOff, queueNoLives, queueRevived;
    private List<String> scoreboardNormal, scoreboardQueue, joinMessage;
    private Location spawnLocation;
    private boolean voidTP;
    private int voidTPPosition, count = 4;
    private double doubleJumpMultiply, doubleJumpY;
    private Map<Integer, String> tabListOne = new HashMap<>(), tabListTwo = new HashMap<>(), tabListThree = new HashMap<>();
    private TabMode tabMode;
    private int tabCountPosition;
    private Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");


    public Lang() {
        update();
    }

    public void update() {
        NitrateItem.getItems().clear();
        NitrateInventory.getNitrateInventories().clear();
        FileConfiguration c = Nitrate.getInstance().getConfig();
        scoreboardTitle = ChatColor.translateAlternateColorCodes('&', c.getString("lang.scoreboard.title"));
        toggledPlayersOff = ChatColor.translateAlternateColorCodes('&', c.getString("lang.toggledplayersoff"));
        toggledPlayersOn = ChatColor.translateAlternateColorCodes('&', c.getString("lang.toggledplayerson"));
        scoreboardNormal = c.getStringList("lang.scoreboard.normal");
        scoreboardQueue = c.getStringList("lang.scoreboard.queue");

        joinMessage = c.getStringList("lang.joinmessage");

        setSpawnCommand = ChatColor.translateAlternateColorCodes('&', c.getString("lang.commands.setspawn.callback"));
        queueNoLives = ChatColor.translateAlternateColorCodes('&', c.getString("lang.queue.nolives"));
        queueRevived = ChatColor.translateAlternateColorCodes('&', c.getString("lang.queue.revived"));

        website = c.getString("config.website");
        discord = c.getString("config.discord");
        twitter = c.getString("config.twitter");
        store = c.getString("config.store");
        teamspeak = c.getString("config.teamspeak");


        Location l = new LocationSerializer().fromString(c.getString("settings.spawnlocation"));
        l.setYaw(Float.parseFloat(c.getString("settings.yaw")));
        l.setPitch(Float.parseFloat(c.getString("settings.pitch")));

        spawnLocation = l;

        voidTP = c.getBoolean("settings.voidtp.enabled");
        voidTPPosition = c.getInt("settings.voidtp.position");

        doubleJumpMultiply = c.getDouble("settings.doublejump.multiply");
        doubleJumpY = c.getDouble("settings.doublejump.y");



        for(String i : c.getConfigurationSection("items").getKeys(false)) {
            ArrayList<Integer> slots = new ArrayList<>();
            if(c.getString("items." + i + ".slot").contains("-")) IntStream.rangeClosed(Integer.parseInt(c.getString("items." + i + ".slot").split("-")[0]), Integer.parseInt(c.getString("items." + i + ".slot").split("-")[1])).forEach(slots::add);

            new NitrateItem(i, c.getString("items." + i + ".name"), ItemUtils.get(c.getString("items." + i + ".material")), c.getInt("items." + i + ".amount"), c.getInt("items." + i + ".data"),
                    (c.getString("items." + i + ".slot").contains("-") ? slots : Collections.singletonList(c.getInt("items." + i + ".slot"))),
                    c.getStringList("items." + i + ".lore"), c.getString("items." + i + ".action"), false);
        }

        for(String i : c.getConfigurationSection("inventories").getKeys(false)) {
            String k = "inventories." + i + ".";
            List<NitrateItem> nitrateItems = new ArrayList<>();
            for(String it : c.getConfigurationSection("inventories." + i + ".items").getKeys(false)) {
                ArrayList<Integer> slots = new ArrayList<>();
                if(c.getString(k + "items." + it + ".slot").contains("-")) IntStream.rangeClosed(Integer.parseInt(c.getString(k + "items." + it + ".slot").split("-")[0]), Integer.parseInt(c.getString(k + "items." + it + ".slot").split("-")[1])).forEach(slots::add);
                nitrateItems.add(new NitrateItem(i + "." + it, c.getString(k + "items." + it + ".name"), ItemUtils.get(c.getString(k + "items." + it+ ".material")), c.getInt(k + "items." + it + ".amount"), c.getInt(k + "items." + it + ".data"),
                        (c.getString(k + "items." + it + ".slot").contains("-") ?
                                slots : Collections.singletonList(c.getInt(k + "items." + it + ".slot")))
                        , c.getStringList(k + "items." + it+ ".lore"), c.getString(k + "items." + it + ".action"), true));
            }
            new NitrateInventory(i, nitrateItems, c.getString(k + "title"));
        }

        if(voidTP) Bukkit.getScheduler().runTaskTimerAsynchronously(Nitrate.getInstance(), () -> Bukkit.getOnlinePlayers().stream().filter(p -> p.getLocation().getBlockY() <= getVoidTPPosition()).forEach(p -> p.teleport(getSpawnLocation())), 0, 15);

        for(String i : c.getConfigurationSection("tab.0").getKeys(false)) {
            tabListOne.put(Integer.parseInt(i), c.getString("tab.0." + i));
        }

        for(String i : c.getConfigurationSection("tab.1").getKeys(false)) {
            tabListTwo.put(Integer.parseInt(i), c.getString("tab.1." + i));
        }

        for(String i : c.getConfigurationSection("tab.2").getKeys(false)) {
            tabListThree.put(Integer.parseInt(i), c.getString("tab.2." + i));
        }

        tabMode = TabMode.valueOf(c.getString("tab.mode", "REGULAR").toUpperCase());
        tabCountPosition = c.getInt("tab.startposition", 4);


        new BukkitRunnable() {
            public void run() {
                if(count == 3) {
                    loadingText = "Loading.";
                }else if(count == 2) {
                    loadingText = "Loading..";
                }else if(count == 1) {
                    loadingText = "Loading...";
                }else if(count == 0) {
                    loadingText = "Loading";
                    count = 4;
                }
                count--;

            }


        }.runTaskTimer(Nitrate.getInstance(), 0, 20);

    }


    public String convert(String m, Player p) {
        String conv;
        conv = m.replace("%online%", CountCache.GLOBAL_COUNT + "")
                .replace("%website%", website)
                .replace("%twitter%", twitter)
                .replace("%discord%", discord)
                .replace("%store%", store)
                .replace("%teamspeak%", teamspeak)
                .replace("%line%", BukkitAPI.LINE);

        if(conv.contains("%serverstatus:") || conv.contains("%onlineplayers:") || conv.contains("%deathban:") || conv.contains("%lives:") || conv.contains("%mapkit:") || conv.contains("%factionsize:")) {
            conv = handlePlaceholder(p, m);
        }




        if(conv.contains("%factionsize:")) {
            String[] c = conv.split("%");

            String[] s = conv.split("%factionsize:");
            String serv = s[1].replace("%", "");
            String o = FoxtrotHandler.getServerSetting(serv, "allySize");
            if(o == null) {
                conv = conv.replace("%factionsize:" + s[1] + "%", Nitrate.getLang().getLoadingText());
            }else {
                conv = conv.replace("%factionsize:" + s[1] + "%", FoxtrotHandler.getServerSetting(serv, "factionSize") + " man, " + FoxtrotHandler.getServerSetting(serv, "allySize") + " ally");
            }
        }

        if(p != null) conv = conv.replace("%rank%", BukkitAPI.getPlayerRank(p).getDisplayName())
                .replace("%rankcolor%", BukkitAPI.getPlayerRank(p).getColor())
                .replace("%username%", p.getName())
                .replace("%rankexpires%", (BukkitAPI.getProfile(p).getCurrentGrant().getActiveUntil() == 9223372036854775807L ? "Never" : TimeUtil.fromMillis(BukkitAPI.getProfile(p).getCurrentGrant().getActiveUntil() - System.currentTimeMillis()).toString()))
                .replace("%player%", p.getName());
        if(p != null && pQueue.getQueueManager().getPlayersQueue(p.getUniqueId()) != null) {
            Queue queue = pQueue.getQueueManager().getPlayersQueue(p.getUniqueId());
            conv = conv.replace("%queueposition%", "" + (queue == null ? "0" : queue.getPosition(p.getUniqueId())))
                    .replace("%queuename%", (queue == null ? "N/A" : queue.getQueueName()))
                    .replace("%queueest%", (queue == null ? "N/A" : queue.queueStatus(p.getUniqueId())))
                    .replace("%queuesize%", "" + (queue == null ? "0" : queue.getPlayersInQueue().size()));

        }
        return ChatColor.translateAlternateColorCodes('&', conv);
    }

    public String handlePlaceholder(Player player, String text) {
        if (text == null) {
            return null;
        }


        Matcher m = PLACEHOLDER_PATTERN.matcher(text);

        while (m.find()) {
            String format = m.group(1);
            int index = format.indexOf(":");

            if (index <= 0 || index >= format.length()) {
                continue;
            }

            String identifier = format.substring(0, index).toLowerCase();
            String params = format.substring(index + 1);
            String value = null;


            switch(identifier.toLowerCase()) {
                case "serverstatus": {
                    value = (ServerInfo.serverExists(params) ? ServerInfo.formattedStatus(params, true) : getLoadingText());
                    break;
                }
                case "onlineplayers": {
                    value = (ServerInfo.serverExists(params) && !ServerInfo.getProperty(params, ServerProperty.STATUS).equals("OFFLINE") ? ServerInfo.getProperty(params, ServerProperty.ONLINE) + " / " + ServerInfo.getProperty(params, ServerProperty.MAXIMUM) : getLoadingText());
                    break;
                }
                case "deathban": {
                    if(params.equals("newline")) {
                        value = null;
                    }
                    else if(FoxtrotHandler.isDeathbanned(params, player.getUniqueId())) {
                        value = TimeUtils.formatLongIntoHHMMSS(FoxtrotHandler.getDeathbanTime(params, player.getUniqueId()));
                    }else {
                        value = null;
                    }
                    break;
                }
                case "lives": {
                    Object o = "" + FoxtrotHandler.getSoulboundLives(params, player.getUniqueId());
                    value = (o == null ? Nitrate.getLang().getLoadingText() : o.toString() + ChatColor.RED + " ❤");
                    break;
                }
                case "mapkit": {
                    String o = FoxtrotHandler.getServerSetting(params, "protection");
                    if(o == null) {
                        value = Nitrate.getLang().getLoadingText();
                    }else {
                        value = "Protection " + FoxtrotHandler.getServerSetting(params, "protection") + ", Sharpness " + FoxtrotHandler.getServerSetting(params, "sharpness");
                    }
                    break;
                }
                case "factionsize": {
                    String o = FoxtrotHandler.getServerSetting(params, "allySize");
                    if(o == null) {
                        value = Nitrate.getLang().getLoadingText();
                    }else {
                        value = FoxtrotHandler.getServerSetting(params, "factionSize") + " man, " + FoxtrotHandler.getServerSetting(params, "allySize") + " ally";
                    }
                    break;
                }

            }

            if (value != null) {
                text = text.replaceAll(Pattern.quote(m.group()), Matcher.quoteReplacement(value));
            }

        }

        return text;
    }


}

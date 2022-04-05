package club.coan.nitrate.commands;

import club.coan.nitrate.Nitrate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.protocol.plib.command.Command;
import rip.protocol.plib.configuration.serializers.LocationSerializer;

public class HubCommands {

    @Command(names = {"setspawn"}, permission = "nitrate.command.setspawn")
    public static void setSpawn(Player p) {
        Nitrate.getInstance().getConfig().set("settings.spawnlocation", new LocationSerializer().toString(p.getLocation()));
        Nitrate.getInstance().getConfig().set("settings.yaw", p.getLocation().getYaw());
        Nitrate.getInstance().getConfig().set("settings.pitch", p.getLocation().getPitch());
        Nitrate.getInstance().saveConfig();
        Nitrate.getInstance().reloadConfig();
        p.sendMessage(Nitrate.getLang().convert(Nitrate.getLang().getSetSpawnCommand(), p));
    }

    @Command(names = {"spawn"}, permission = "nitrate.command.spawn")
    public static void spawn(Player p) {
        p.teleport(Nitrate.getLang().getSpawnLocation());
    }

    @Command(names = "reloadconfig", permission = "op", hidden = true)
    public static void reload(CommandSender s) {
        Nitrate.getInstance().reloadConfig();
        s.sendMessage(ChatColor.GREEN + "Reloaded Config.");
    }

}

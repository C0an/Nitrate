package club.coan.nitrate.commands;

import club.coan.nitrate.Nitrate;
import club.coan.rinku.command.Command;
import club.coan.rinku.other.BukkitUtils;
import org.bukkit.entity.Player;

public class HubCommands {

    @Command(names = {"setspawn"}, permissionNode = "nitrate.command.setspawn")
    public static void setSpawn(Player p) {
        Nitrate.getInstance().getConfig().set("settings.spawnlocation", BukkitUtils.getString(p.getLocation()));
        Nitrate.getInstance().saveConfig();
        Nitrate.getInstance().reloadConfig();
        p.sendMessage(Nitrate.getLang().convert(Nitrate.getLang().getSetSpawnCommand(), p));
    }

    @Command(names = {"spawn"})
    public static void spawn(Player p) {
        p.teleport(Nitrate.getLang().getSpawnLocation());
    }

}

package club.coan.nitrate.commands;

import club.coan.nitrate.Nitrate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.protocol.plib.command.Command;
import rip.protocol.plib.command.Param;

public class ModifyTabCommand {

    @Command(names = {"modifytab", "mt"}, permission = "nitrate.command..modifytab", hidden = true)
    public static void tab(Player p, @Param(name = "x") int x, @Param(name = "y") int y, @Param(name = "text", wildcard = true) String t) {
        if(x > 2 || y > 20) {
            p.sendMessage(ChatColor.RED + "The position you have entered is too high! Max X: 2 | Max Y: 20");
            return;
        }
        Nitrate.getInstance().getConfig().set("tab." + x + "." + y, t);
        Nitrate.getInstance().saveConfig();
        Nitrate.getInstance().reloadConfig();
        p.sendMessage(ChatColor.GREEN + "Success! Modified value (" + x + ", " + y + ") to say: " + t);
    }

}

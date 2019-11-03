package club.coan.nitrate;

import club.coan.nitrate.cache.CountCache;
import club.coan.nitrate.implementer.ScoreboardImpl;
import club.coan.nitrate.listeners.CoreListeners;
import club.coan.nitrate.utils.Lang;
import club.coan.rinku.command.RinkuCommandHandler;
import club.coan.rinku.scoreboard.Assemble;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Nitrate extends JavaPlugin implements PluginMessageListener {

    @Getter private static Nitrate instance;
    @Getter private static Lang lang;
    @Getter private Assemble assemble;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        lang = new Lang();
        assemble = new Assemble(this, new ScoreboardImpl());

        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        RinkuCommandHandler.loadCommandsFromPackage(this, "club.coan.nitrate.commands");
        Bukkit.getPluginManager().registerEvents(new CoreListeners(), this);


        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::updatePlayerCount, 0, 20);
    }

    @Override
    public void onDisable() {
        assemble.cleanup();
        Bukkit.getScheduler().cancelAllTasks();
        instance = this;
    }

    private void updatePlayerCount() {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("PlayerCount");
            out.writeUTF("ALL");

            Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord"))
            return;

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String command = in.readUTF();

            if (command.equals("PlayerCount")) {
                String server = in.readUTF();
                int amount = in.readInt();
                if (server.equalsIgnoreCase("ALL")) {
                    CountCache.GLOBAL_COUNT = amount;
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if(instance != null && getConfig() != null) lang = new Lang();
    }
}

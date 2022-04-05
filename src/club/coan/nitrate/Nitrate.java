package club.coan.nitrate;

import club.coan.nitrate.cache.CountCache;
import club.coan.nitrate.implementer.ScoreboardImpl;
import club.coan.nitrate.listeners.CoreListeners;
import club.coan.nitrate.tab.HubTab;
import club.coan.nitrate.utils.Lang;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import rip.protocol.bridge.shared.status.ServerInfo;
import rip.protocol.bridge.shared.status.ServerProperty;
import rip.protocol.plib.command.FrozenCommandHandler;
import rip.protocol.plib.scoreboard.FrozenScoreboardHandler;
import rip.protocol.plib.tab.FrozenTabHandler;

public class Nitrate extends JavaPlugin {

    @Getter private static Nitrate instance;
    @Getter private static Lang lang;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        lang = new Lang();

        FrozenCommandHandler.registerPackage(this, "club.coan.nitrate.commands");
        FrozenScoreboardHandler.setConfiguration(ScoreboardImpl.create());
        FrozenTabHandler.setLayoutProvider(new HubTab());

        Bukkit.getPluginManager().registerEvents(new CoreListeners(), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::updatePlayerCount, 0, 20);
    }


    @Override
    public void onDisable() {
        instance = this;
    }

    private void updatePlayerCount() {
        int count = ServerInfo.getBridgeServers().stream().filter(s -> ServerInfo.getProperty(s, ServerProperty.PROVIDERNAME).equalsIgnoreCase("BungeeCord")).mapToInt(s -> Integer.parseInt(ServerInfo.getProperty(s, ServerProperty.ONLINE))).sum();
            CountCache.GLOBAL_COUNT = (count == 0 ? getLang().getLoadingText() : "" + count);
    }


    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if(instance != null && getConfig() != null) lang = new Lang();
    }
}

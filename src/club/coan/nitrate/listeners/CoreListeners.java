package club.coan.nitrate.listeners;

import club.coan.nitrate.Nitrate;
import club.coan.nitrate.utils.NitrateItem;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.Vector;
import rip.protocol.bridge.bukkit.BukkitAPI;
import rip.protocol.bridge.shared.profile.Profile;
import rip.protocol.bridge.shared.utils.TimeUtil;
import rip.protocol.plib.util.PlayerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreListeners implements Listener {

    @Getter private static List<Player> hidingPlayers = new ArrayList<>();
    @Getter private static Map<Player, Long> lastDone = new HashMap<>();



    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Profile pf = BukkitAPI.getProfile(p);
        e.setJoinMessage(null);
        p.teleport(Nitrate.getLang().getSpawnLocation());
        PlayerUtils.resetInventory(p, GameMode.SURVIVAL);

        List<String> msg = new ArrayList<>();
        Nitrate.getLang().getJoinMessage().forEach(s -> msg.add(Nitrate.getLang().convert(s, p)));
        p.sendMessage(msg.toArray(new String[0]));

        NitrateItem.getItems().stream().filter(item -> !item.getSlots().isEmpty() && item.getSlots().stream().noneMatch(integer -> integer == 69420) && !item.isInventoryItem()).forEach(item -> {
            item.getSlots().forEach(integer -> {
                p.getInventory().setItem(integer, item.getItem(p));
            });
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        if(e.getPlayer().isFlying()){
            e.getPlayer().setFlying(false);
            e.getPlayer().setAllowFlight(false);
            Vector v = e.getPlayer().getLocation().getDirection().multiply(Nitrate.getLang().getDoubleJumpMultiply()).setY(Nitrate.getLang().getDoubleJumpMultiply());
            e.getPlayer().setVelocity(v);
            e.getPlayer().playEffect(e.getPlayer().getLocation(), Effect.MOBSPAWNER_FLAMES, 20);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.FIREWORK_BLAST, 20, 6F/63F);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.EXPLODE, 1, 126F/63F);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1, 126F/63F);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLAZE_HIT, 1, 63F/63F);

        }
        if(e.getPlayer().isOnGround() && !e.getPlayer().getAllowFlight()){
            e.getPlayer().setAllowFlight(true);
        }

    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(e.getItem() != null && e.getItem().getType() != Material.AIR && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
            NitrateItem nitrateItem = NitrateItem.isItem(e.getItem());
            if(nitrateItem == null) return;
            if(nitrateItem.hasAction()) nitrateItem.handleAction(p);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void mobSpawn(EntitySpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
        e.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(e.getClickedInventory() != null && e.getCurrentItem() != null && e.getClickedInventory().getType() == InventoryType.PLAYER && !canBuild(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        e.setCancelled(!canBuild(e.getPlayer()));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        e.setCancelled(!canBuild(e.getPlayer()));
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        e.setCancelled(!canBuild(e.getPlayer()));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(!canBuild(e.getPlayer()));
    }

    private boolean canBuild(Player p) {
        return (p.hasMetadata("build") && p.getMetadata("build").get(0).asBoolean()) && p.getGameMode() == GameMode.CREATIVE;
    }

    public static boolean canUseHide(Player p){
        if(getLastDone().get(p) == null){
            return true;
        }
        if(getLastDone().get(p) + 5000 <= System.currentTimeMillis())
            return true;

        p.sendMessage(ChatColor.RED + "Please wait " + (TimeUtil.millisToSeconds(getLastDone().get(p) + 5000 - System.currentTimeMillis())) + "s to toggle player visibility!");
        return false;
    }

}

package club.coan.nitrate.listeners;

import club.coan.christian.bukkit.BukkitAPI;
import club.coan.christian.shared.profile.Profile;
import club.coan.nitrate.Nitrate;
import club.coan.nitrate.utils.NitrateItem;
import club.coan.rinku.other.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.List;

public class CoreListeners implements Listener {

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

        NitrateItem.getItems().stream().filter(item -> item.getSlot() != -1 && !item.isInventoryItem()).forEach(item -> p.getInventory().setItem(item.getSlot(), item.getItem(p)));
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
        return p.hasPermission(Nitrate.getLang().getBuildPermission()) && p.getGameMode() == GameMode.CREATIVE;
    }


}

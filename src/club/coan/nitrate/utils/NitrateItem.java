package club.coan.nitrate.utils;

import club.coan.nitrate.Nitrate;
import club.coan.nitrate.listeners.CoreListeners;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.protocol.bridge.bukkit.utils.impl.FoxtrotRevivePacket;
import rip.protocol.bridge.shared.status.ServerInfo;
import rip.protocol.bridge.shared.status.ServerProperty;
import rip.protocol.bridge.shared.status.impltype.FoxtrotHandler;
import rip.protocol.plib.menu.Button;
import rip.protocol.plib.util.ItemBuilder;
import rip.protocol.plib.xpacket.FrozenXPacketHandler;
import rip.protocol.pqueue.pQueue;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class NitrateItem {

    @Getter private static Set<NitrateItem> items = new HashSet<>();

    private String name, displayName;
    private ItemStack material;
    private int amount, data;
    private List<Integer> slots;
    private List<String> lore;
    private String action;
    private boolean inventoryItem;

    public NitrateItem(String name, String displayName, ItemStack material, int amount, int data, List<Integer> slots, List<String> lore, String action, boolean inventoryItem) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.amount = amount;
        this.data = data;
        this.slots = slots;
        this.lore = lore;
        this.action = action;
        this.inventoryItem = inventoryItem;
        items.add(this);
    }

    public ItemStack getItem(Player p) {
        List<String> lre = new ArrayList<>();
        lore.forEach(str -> lre.add(Nitrate.getLang().convert(str, p)));
        return ItemBuilder.of(material.getType(), amount).name(Nitrate.getLang().convert(displayName, p)).setLore(lre).data((short)data).build();
    }


    public ItemStack getItem() {
        return getItem(null);
    }

    public static NitrateItem isItem(ItemStack is, Player p) {
        return getItems().stream().filter(nitrateItem -> nitrateItem.getItem(p).isSimilar(is)).findFirst().orElse(null);
    }

    public static NitrateItem isItem(ItemStack is) {
        return isItem(is, null);
    }

    public void handleAction(Player p) {
        String[] ac = action.split("/");
        switch(ac[0]) {
            case "openmenu": {
                NitrateInventory inv = NitrateInventory.getInventory(ac[1]);
                if(inv == null) {
                    p.sendMessage(ChatColor.RED + "There is no such inventory with the name \"" + ac[1] + "\".");
                    break;
                }
                inv.openMenu(p);
                break;
            }

            case "sendmessage": {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Nitrate.getLang().convert(action.substring(ac[0].length() + 1), p)));
                break;
            }

            case "queue": {
                String server = ac[1];

                String impl = ServerInfo.getProperty(server, ServerProperty.PROVIDERNAME);
                if(ServerInfo.serverExists(server) && impl.contains("Foxtrot") && !FoxtrotHandler.isKitmap(server)) {
                    if(FoxtrotHandler.isDeathbanned(server, p.getUniqueId())) {
                        int lives = FoxtrotHandler.getSoulboundLives(server, p.getUniqueId());
                        if(lives <= 0) {
                            p.sendMessage(Nitrate.getLang().getQueueNoLives());
                        } else {
                            FrozenXPacketHandler.sendToAll(new FoxtrotRevivePacket(server, p.getUniqueId(), (lives - 1)));
                            String lifeText =  (lives - 1) +  " li" + ((lives - 1) == 1 ? "fe" : "ves");
                            p.sendMessage(Nitrate.getLang().getQueueRevived().replace("%lives%", lifeText));
                        }
                        return;
                    }
                }

                pQueue.getQueueManager().addPlayer(p, ac[1]);
                break;
            }

            case "toggleplayers": {
                boolean status = Boolean.parseBoolean(ac[1]);
                NitrateItem nitrateItem = NitrateItem.getNitrateItem(ac[2]);
                if(nitrateItem == null) return;
                if(!CoreListeners.canUseHide(p)) return;
                p.sendMessage(status ? Nitrate.getLang().getToggledPlayersOff() : Nitrate.getLang().getToggledPlayersOn());
                p.setItemInHand(nitrateItem.getItem(p));

                if(status) {
                    CoreListeners.getHidingPlayers().add(p);
                }else {
                    CoreListeners.getHidingPlayers().remove(p);
                }

                CoreListeners.getLastDone().put(p, System.currentTimeMillis());
                if(status) {
                    for(Player t : Bukkit.getOnlinePlayers()){
                        if(p == t) continue;
                        p.hidePlayer(t);
                    }
                }else {
                    for(Player t : Bukkit.getOnlinePlayers()){
                        if(p == t) continue;
                        p.showPlayer(t);
                    }
                }


            }

        }

    }


    public Button getAsButton() {
        return new Button() {
            @Override
            public String getName(Player player) {
                return Nitrate.getLang().convert(displayName, player);
            }

            @Override
            public List<String> getDescription(Player player) {
                return lore.stream().filter(Objects::nonNull).map(s -> Nitrate.getLang().convert(s, player)).collect(Collectors.toList());
            }

            @Override
            public Material getMaterial(Player player) {
                return material.getType();
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) data;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if(hasAction()) handleAction(player);
            }
        };
    }

    public static NitrateItem getNitrateItem(String name) {
        return items.stream().filter(nitrateItem -> nitrateItem.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean hasAction() {
        return !action.equals("");
    }
}

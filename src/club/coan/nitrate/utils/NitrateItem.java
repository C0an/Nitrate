package club.coan.nitrate.utils;

import club.coan.nitrate.Nitrate;
import club.coan.queue.QueuePlugin;
import club.coan.queue.data.QueueManager;
import club.coan.rinku.items.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class NitrateItem {

    @Getter private static Set<NitrateItem> items = new HashSet<>();

    private String name, displayName;
    private ItemStack material;
    private int amount, slot, data;
    private List<String> lore;
    private String action;
    private boolean inventoryItem;

    public NitrateItem(String name, String displayName, ItemStack material, int amount, int data, int slot, List<String> lore, String action, boolean inventoryItem) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.amount = amount;
        this.data = data;
        this.slot = slot;
        this.lore = lore;
        this.action = action;
        this.inventoryItem = inventoryItem;
        items.add(this);
    }

    public ItemStack getItem(Player p) {
        List<String> lre = new ArrayList<>();
        lore.forEach(str -> lre.add(Nitrate.getLang().convert(str, p)));
        return new ItemBuilder(material.getType(), amount).displayName(Nitrate.getLang().convert(displayName, p)).lore(lre).data((short)data).build();
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
                inv.openInventory(p);
                break;
            }

            case "sendmessage": {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Nitrate.getLang().convert(action.substring(ac[0].length() + 1), p)));
                break;
            }

            case "queue": {
                QueuePlugin.getQueueManager().addPlayer(p, ac[1]);
                break;
            }

        }

    }


    public boolean hasAction() {
        return !action.equals("");
    }
}

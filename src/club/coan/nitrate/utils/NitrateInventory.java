package club.coan.nitrate.utils;

import club.coan.nitrate.Nitrate;
import lombok.Getter;
import org.bukkit.entity.Player;
import rip.protocol.plib.menu.Button;
import rip.protocol.plib.menu.Menu;

import java.util.*;

@Getter
public class NitrateInventory extends Menu {

    @Getter private static Set<NitrateInventory> nitrateInventories = new HashSet<>();

    private String name;
    private List<NitrateItem> nitrateItems;
    private String title;

    public NitrateInventory(String name, List<NitrateItem> nitrateItems, String title) {
        this.name = name;
        this.nitrateItems = nitrateItems;
        this.title = title;
        nitrateInventories.add(this);
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return Nitrate.getLang().convert(title, player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        nitrateItems.forEach(i -> i.getSlots().forEach(s -> buttons.put(s, i.getAsButton())));
        return buttons;
    }

    public static NitrateInventory getInventory(String name) {
        return nitrateInventories.stream().filter(nitrateInventory -> nitrateInventory.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}

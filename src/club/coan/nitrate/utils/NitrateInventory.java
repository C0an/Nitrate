package club.coan.nitrate.utils;

import club.coan.nitrate.Nitrate;
import club.coan.rinku.menu.buttons.Button;
import club.coan.rinku.menu.dynamicmenu.DynamicMenu;
import club.coan.rinku.menu.pagination.PaginableMenu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.*;

@Getter
public class NitrateInventory {

    @Getter private static Set<NitrateInventory> nitrateInventories = new HashSet<>();

    private String name;
    private List<NitrateItem> nitrateItems;
    private String title;

    public NitrateInventory(String name, List<NitrateItem> nitrateItems, String title) {
        this.name = name;
        this.nitrateItems = nitrateItems;
        this.title = title;
        nitrateInventories.add(this);
    }

    public void openInventory(Player p) {
        DynamicMenu dynamicMenu;



        dynamicMenu = new DynamicMenu() {
            @Override
            public HashMap<Integer, Button> getStaticItems(Player player) {
                HashMap<Integer, Button> items = new HashMap<>();
                nitrateItems.forEach(item -> items.put(item.getSlot(), new Button(item.getItem(p), e -> {
                    e.setCancelled(true);
                    e.setResult(Event.Result.DENY);
                    e.getWhoClicked().closeInventory();
                    item.handleAction(p);
                })));
                return items;
            }

            @Override
            public List<Button> getContent(Player player) {
                return null;
            }

            @Override
            public String getTitle(Player player) {
                return Nitrate.getLang().convert(title, p);
            }

            @Override
            public int getSize() {
                return 54;
            }

        };
        dynamicMenu.openMenu(p);
//
//        paginableMenu = new PaginableMenu(null, invButtons) {
//            @Override
//            public String getTitle(Player p) {
//                return Nitrate.getLang().convert(title, p);
//            }
//
//            @Override
//            public int getSize() {
//                return 54;
//            }
//        };
//
//        paginableMenu.openMenu(p);
    }

    public static NitrateInventory getInventory(String name) {
        return nitrateInventories.stream().filter(nitrateInventory -> nitrateInventory.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}

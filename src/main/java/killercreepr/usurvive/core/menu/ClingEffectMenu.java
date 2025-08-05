package killercreepr.usurvive.core.menu;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxmenus.core.menu.BukkitMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ClingEffectMenu extends BukkitMenu {
    protected final Player viewer;
    protected final int maxTimes;
    protected final Consumer<Entity> onComplete;
    public ClingEffectMenu(Player viewer, int maxTimes, Consumer<Entity> onComplete) {
        this.viewer = viewer;
        this.maxTimes = maxTimes;
        this.onComplete = onComplete;
    }
    protected int times = 0;

    @Override
    public void load() {
        super.load();
        inventory = MenuType.GENERIC_3X3.create(viewer, Component.text("ur in danger"))
            .getTopInventory();
        placeItemRandomly();
    }

    @Override
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        super.onMenuClick(event);
        ItemStack clicked = event.getCurrentItem();
        HumanEntity who = event.getWhoClicked();
        if(CruxItem.isEmpty(clicked)){
            who.closeInventory();
            CreateSound.sound(Sound.ENTITY_VILLAGER_NO, .35f, 1.2f).playAt(who);
            return;
        }

        times++;
        if(times >= maxTimes){
            who.closeInventory();
            onComplete.accept(who);
            return;
        }

        placeItemRandomly();
        CreateSound.sound(Sound.UI_BUTTON_CLICK, .35f, .8f).playAt(who);
    }

    protected static final List<Material> ITEM_TYPES = List.of(
        Material.WHITE_STAINED_GLASS_PANE,
        Material.ORANGE_STAINED_GLASS_PANE,
        Material.MAGENTA_STAINED_GLASS_PANE,
        Material.LIGHT_BLUE_STAINED_GLASS_PANE,
        Material.YELLOW_STAINED_GLASS_PANE,
        Material.LIME_STAINED_GLASS_PANE,
        Material.PINK_STAINED_GLASS_PANE,
        Material.GRAY_STAINED_GLASS_PANE,
        Material.LIGHT_GRAY_STAINED_GLASS_PANE,
        Material.CYAN_STAINED_GLASS_PANE,
        Material.PURPLE_STAINED_GLASS_PANE,
        Material.BLUE_STAINED_GLASS_PANE,
        Material.BROWN_STAINED_GLASS_PANE,
        Material.GREEN_STAINED_GLASS_PANE,
        Material.RED_STAINED_GLASS_PANE,
        Material.BLACK_STAINED_GLASS_PANE
    );

    public void placeItemRandomly(){
        getInventory().clear();
        int slot = CruxMath.random(0, getInventory().getSize()-1);
        setItem(slot, createClickItem());
    }

    public ItemStack createClickItem(){
        return CruxItem.create(CruxCollection.getRandom(ITEM_TYPES))
            .itemName("<white>Click here!")
            .item();
    }
}

package killercreepr.usurvive.core.menu;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.InvUtil;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.cruxmenus.core.menu.ConfigMenu;
import killercreepr.cruxmenus.core.menu.slot.SimpleFixedSlot;
import killercreepr.usurvive.core.data.Pack;
import killercreepr.usurvive.core.entity.memory.ItemPackHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemPackMenu extends ConfigMenu {
    public ItemPackMenu(@NotNull MenuHolder holder, @NotNull DataExchange info) {
        super(holder, info);
    }

    public ItemPackMenu(@NotNull MenuHolder holder, @NotNull DataExchange info, @Nullable MergedTagContainer tags) {
        super(holder, info, tags);
    }

    public void savePage(){
        ItemPackHolder pack = itemPack();
        pack.getPack().setPage(page, buildPage());
    }

    public Pack.Page buildPage(){
        var page = new Pack.Page();
        for(int i = 0; i < maxContent; i++){
            ItemStack item = inventory.getItem(i);
            page.set(i, item);
        }
        return page;
    }


    public ItemPackHolder itemPack(){
        var p = info.getOrThrow("viewer", Entity.class);
        return ItemPackHolder.itemPackHolder(p);
    }

    public static int getSize(@NotNull Player p){
        String check = "usurvive.itempack.size.";
        int max = 0;
        boolean found = false;
        for(PermissionAttachmentInfo i : p.getEffectivePermissions()){
            if(i.getPermission().startsWith(check)){
                found = true;
                try{
                    int x = Integer.parseInt(i.getPermission().replace(check, ""));
                    if(x > max) max = x;
                } catch (IllegalArgumentException e){
                    Crux.logError("Invalid permission, " + i.getPermission() + "! Cannot get number.");
                }
            }
        }
        return found ? max : 0;
    }

    private final Map<Integer, ItemStack> registered = new HashMap<>();
    private int page = 0;
    private int size;

    protected int maxContent;

    public int getMaxPages(int size) {
        if (size <= 0) return 0;
        return (int) Math.ceil((double) size / (double) MAX_MENU_SLOTS) - 1;
    }


    public static final int MAX_MENU_SLOTS = 45;
    public int calculateMaxContentInPage(int size) {
        int startIndex = page * MAX_MENU_SLOTS;
        if (startIndex >= size) {
            return 0;
        }

        int remainingItems = size - startIndex;
        return Math.min(remainingItems, MAX_MENU_SLOTS);
    }

    @Override
    protected void refreshReconstruct() {
        this.reconstruct(InvUtil.getInventorySize(maxContent+9), Component.text(
            "Item Pack " + (page+1) + "/" + (getMaxPages(size)+1)
        ), true, true);
    }

    protected boolean loaded = false;
    @Override
    public void load() {
        var p = info.getOrThrow("viewer", Player.class);

        this.size = getSize(p);
        page = CruxMath.clamp(page, 0, getMaxPages(size)); // ⬅ Clamp first

        ItemPackHolder pack = itemPack();
        Pack.Page packPack = pack.getPack().get(page);
        if(packPack != null) this.registered.putAll(packPack.get());

        maxContent = calculateMaxContentInPage(size); // ⬅ Then calculate content safely

        super.load();
        openPage(p, page);
        loaded = true;
    }

    @Override
    public void onClose(@NotNull HumanEntity p) {
        super.onClose(p);
        if(!loaded) return;
        savePage();
    }

    public ItemStack lockItem(){
        return CruxItem.create(Material.RED_STAINED_GLASS_PANE)
            .itemName("<red>Locked Slot")
            .editMeta(meta -> CruxTag.set(meta, "fixed", PersistentDataType.BOOLEAN, true))
            .item();
    }

    public void openPage(@NotNull Player p, int page){
        if(page < 0) return;
        Inventory menu = inventory;
        registered.forEach((slot, item) ->{
            if(slot >= menu.getSize()) return;
            setItem(slot, item);
        });

        int locked = maxContent;
        ItemStack lockItem = lockItem();
        int maxSize = inventory.getSize();
        int size = maxSize-9;
        while(locked < size){
            menu.setItem(locked, lockItem);
            locked++;
        }
        for(int i = size; i < maxSize; i++){
            menu.setItem(i, CruxItem.create(Material.BLACK_STAINED_GLASS_PANE).itemName("")
                .editMeta(meta -> CruxTag.set(meta, "fixed", PersistentDataType.BOOLEAN, true))
                .item());
        }

        setItem(maxSize-5, CruxItem.create(Material.BARRIER)
            .itemName("Close")
                .editMeta(meta -> CruxTag.set(meta, "fixed", PersistentDataType.BOOLEAN, true))
                .item(),
            new SimpleFixedSlot(this, maxSize-3){
                @Override
                public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                    super.onClick(p, event);
                    p.closeInventory();
                    CreateSound.sound(Sound.UI_BUTTON_CLICK, 0.2f, 1f).playFor(p);
                }
            });

        if(page < getMaxPages(this.size)){
            int index = maxSize-4;
            setItem(index, CruxItem.create(Material.ARROW)
                .itemName("Next Page")
                .editMeta(meta -> CruxTag.set(meta, "fixed", PersistentDataType.BOOLEAN, true))
                .item(), new SimpleFixedSlot(this, index){
                @Override
                public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                    super.onClick(p, event);
                    var menu = new ItemPackMenu(holder, info);
                    menu.page = page+1;
                    menu.load();
                    menu.open(p);
                    CreateSound.sound(Sound.UI_BUTTON_CLICK, 0.2f, 1f).playFor(p);
                }
            });
        }

        if(page > 0){
            int index = maxSize-6;
            setItem(index, CruxItem.create(Material.ARROW)
                .itemName("Previous Page")
                .editMeta(meta -> CruxTag.set(meta, "fixed", PersistentDataType.BOOLEAN, true))
                .item(), new SimpleFixedSlot(this, index){
                @Override
                public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                    super.onClick(p, event);
                    var menu = new ItemPackMenu(holder, info);
                    menu.page = page-1;
                    menu.load();
                    menu.open(p);
                    CreateSound.sound(Sound.UI_BUTTON_CLICK, 0.2f, 1f).playFor(p);
                }
            });
        }
    }

    public boolean isContentSlot(int slot){
        return slot < maxContent;
    }

    @Override
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        if(!loaded){
            event.setCancelled(true);
            return;
        }
        if(isContentSlot(event.getSlot())){
            event.setCancelled(false);
            return;
        }
        super.onMenuClick(event);
    }

    @Override
    public void onInvClick(@NotNull InventoryClickEvent event) {
        if(!loaded){
            event.setCancelled(true);
            return;
        }
        if(event.isShiftClick()){
            event.setCancelled(false);
            return;
        }
        super.onInvClick(event);
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        if(!loaded){
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
    }
}

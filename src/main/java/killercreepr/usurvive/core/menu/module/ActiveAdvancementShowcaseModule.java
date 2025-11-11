package killercreepr.usurvive.core.menu.module;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxString;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.advancement.progress.NumberAdvancementProgress;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxadvancements.crazy.advancement.CrazyAdvancementDisplay;
import killercreepr.cruxadvancements.crazy.advancement.CrazyAdvancementManager;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.contex.MenuContext;
import killercreepr.cruxmenus.api.menu.item.MenuItem;
import killercreepr.cruxmenus.api.menu.slot.Slot;
import killercreepr.cruxmenus.core.menu.module.SimpleActiveMenuModuled;
import killercreepr.cruxmenus.core.menu.slot.SimpleFixedSlot;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ActiveAdvancementShowcaseModule extends SimpleActiveMenuModuled {
    protected final @NotNull AdvancementShowcaseModule module;
    protected final CfgMenu menu;
    protected CruxAdvancementManager<?> selectedManager;
    public ActiveAdvancementShowcaseModule(@NotNull String id, @NotNull AdvancementShowcaseModule module, CfgMenu menu) {
        super(id, module);
        this.module = module;
        this.menu = menu;
        this.selectedManager = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(module.advancement_managers.getFirst());
        for (Number slot : module.advance_slots.sampleList()) {
            validSlots.add(slot.intValue());
        }
    }

    protected final Collection<Integer> validSlots = new ArrayList<>();

    @Override
    public void onLoaded(@NotNull Menu menu) {
        update();
    }

    public List<CruxAdvancementManager<?>> sortManagers() {
        List<CruxAdvancementManager<?>> list = new ArrayList<>();

        // Collect valid managers
        for (Key key : module.advancement_managers) {
            CruxAdvancementManager<?> got = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(key);
            if (got != null) {
                list.add(got);
            }
        }

        // Sort alphabetically by key
        list.sort(Comparator.comparing(Keyed::key));

        // Move selectedManager to front, if present
        if (selectedManager != null && list.contains(selectedManager)) {
            list.remove(selectedManager);
            list.addFirst(selectedManager);
        }

        return list;
    }


    @Override
    public @NotNull AdvancementShowcaseModule getModule() {
        return module;
    }

    public CruxAdvancementManager<?> advancementManager(){
        return selectedManager;
    }

    public int getGeneralSlotOffset(){
        return module.general_slot_offset.value().intValue();
    }

    public int getRawSlotFromCoords(int x, int y){
        return (x + (y * 9));
    }

    protected int x;
    protected int y;

    public void viewManagers(){
        var list = sortManagers();
        int index = -1;
        var inputCtx = TextParserContext.builder().tags(menu.buildTags()).build();
        for (Number slot : module.advance_manager_slots.sampleList()) {
            index++;
            var manager = index >= list.size() ? null : list.get(index);
            int slotIndex = slot.intValue();
            if(manager == null){
                menu.setItem(slotIndex, module.empty_manager_icon.buildItem(inputCtx), (Slot) null, true);
            }else{
                menu.setItem(slotIndex, buildManagerIcon(manager), new SimpleFixedSlot(menu, slotIndex){
                    @Override
                    public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                        super.onClick(p, event);
                        if(manager.key().equals(selectedManager.key()) && x == 0 && y == 0) return;
                        selectedManager = manager;
                        setXY(0, 0);
                        update();
                        CreateSound.sound(Sound.UI_BUTTON_CLICK, 0.2f,1f).playAt(p);
                    }
                }, true);
            }
        }
    }

    public ItemStack buildManagerIcon(CruxAdvancementManager<?> manager){
        CruxAdvancement root = manager.getAdvancement(Crux.key(manager.key().asString() + "/root"));
        return buildIcon(root);
    }

    public void update(){
        viewManagers();
        viewAdvancements();
    }

    protected final Map<Integer, MenuItem> changedItems = new HashMap<>();
    public void viewAdvancements() {
        CruxAdvancementManager<?> manager = advancementManager();
        if (manager == null) return;

        validSlots.forEach(slot ->{
            menu.setItem(slot, null, (Slot) null, true);
        });

        var inputCtx = TextParserContext.builder().tags(menu.buildTags()).build();
        var viewer = menu.info().get("viewer", Player.class);

        // Calculate absolute positions of all advancements
        Map<CruxAdvancement, float[]> absolutePositions = new HashMap<>();
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

        for (CruxAdvancement adv : manager) {
            CrazyAdvancementDisplay display = getDisplay(adv);
            if (display == null) continue;

            float[] pos = getAbsoluteDisplayPos(display);
            absolutePositions.put(adv, pos);

            minX = Math.min(minX, pos[0]);
            minY = Math.min(minY, pos[1]);
            maxX = Math.max(maxX, pos[0]);
            maxY = Math.max(maxY, pos[1]);
        }

        // Viewport dimensions (chest is 9 wide, maybe 6 tall)
        Inventory inv = menu.getInventory();
        int size = inv.getSize();

        int viewWidth = 9; // always 9 columns in a chest GUI
        int viewHeight = size / 9; // number of visible rows

        Map<Integer, MenuItem> changedItems = new HashMap<>();
        // The offset fields x/y now represent how far the player has scrolled
        // (positive x = move right, positive y = move down)
        for (Map.Entry<CruxAdvancement, float[]> entry : absolutePositions.entrySet()) {
            CruxAdvancement adv = entry.getKey();
            float[] pos = entry.getValue();

            // Translate advancement position into grid coordinates
            int gridX = (int) Math.floor(pos[0]);
            int gridY = (int) Math.floor(pos[1]);

            int finalX = gridX - this.x - module.center_offset_x.value().intValue();
            int finalY = gridY - this.y - module.center_offset_y.value().intValue();

            // Skip anything outside the visible GUI viewport
            if (finalX < 0 || finalX >= viewWidth || finalY < 0 || finalY >= viewHeight)
                continue;

            int slot = getRawSlotFromCoords(finalX, finalY) + getGeneralSlotOffset();


            if (!validSlots.contains(slot)){
                var got = menu.getMenuItem(slot);
                if(got == null || viewer == null) continue;
                changedItems.put(slot, got);
                MenuContext ctx = MenuContext.context(menu, DataExchange.empty(), TagContainer.merged(menu.getHolder().getRegistry().getFormat().tags()));
                menu.setItem(slot, CruxItem.wrap(got.getBase().getDisplayItem(viewer, ctx).buildItem(viewer))
                        .editThis(crux ->{
                            ItemStack item = !isVisible(adv, viewer) ? module.unknown_icon.buildItem(inputCtx) : buildIcon(adv);
                            if(CruxItem.isEmpty(item) || !item.hasItemMeta()) return;
                            Component name = null;
                            ItemMeta meta = item.getItemMeta();
                            if(meta.hasDisplayName()) name = meta.displayName();
                            else if(meta.hasItemName()) name = meta.itemName();

                            crux.addLoreFromString(
                                ""
                            );
                            if(name != null){
                                crux.addLore(Component.empty().color(NamedTextColor.WHITE).append(name));
                            }
                            if(meta.hasLore()){
                                crux.addLore(meta.lore());
                            }
                        })
                    .item(), true);
                continue;
            }

            if(viewer != null){
                if(!isVisible(adv, viewer)){
                    menu.setItem(slot, module.unknown_icon.buildItem(inputCtx), true);
                    continue;
                }
            }

            menu.setItem(slot, buildIcon(adv), true);
        }
        this.changedItems.forEach((slot, menuItem) ->{
            if(changedItems.containsKey(slot)) return;
            menu.setItem(slot, menuItem, viewer, true);
        });
        this.changedItems.clear();
        this.changedItems.putAll(changedItems);
    }

    // Gets absolute coordinates of a display (sum of its parents)
    public float[] getAbsoluteDisplayPos(CrazyAdvancementDisplay display) {
        float x = display.getX();
        float y = display.getY();

        CrazyAdvancementDisplay parent = getDisplay(display.getPositionOrigin());
        while (parent != null) {
            x += parent.getX();
            y += parent.getY();
            parent = getDisplay(parent.getPositionOrigin());
        }
        return new float[]{x, y};
    }

    public void navigate(int x, int y){
        this.x += x;
        this.y += y;
    }

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }

    public ItemStack buildIcon(CruxAdvancement adv){
        ItemStack item = adv.getIcon().getItem().clone();
        return CruxItem.wrap(item)
            .lore(null)
            .editThis(crux ->{
                var viewer = menu.info().get("viewer", Player.class);
                if(adv.getIcon() instanceof CrazyAdvancementDisplay display){
                    crux.addFlags(ItemFlag.values());
                    String desc = display.getDescription();
                    String nameAddon = "";
                    if(viewer != null){
                        if(adv.isGranted(viewer)){
                            crux.addLoreFromString(module.completed_lore);
                        }
                        if(adv.getProgress(viewer) instanceof NumberAdvancementProgress progress && progress.getCriteriaMaxProgress() > 1){
                            nameAddon = CruxMath.format(progress.getProgress()) + "/" + CruxMath.format(progress.getCriteriaMaxProgress());
                        }
                    }

                    crux.customName(
                        "<white><!italic>" + Crux.format().deserializeString(display.getTitle() + " " + nameAddon)
                    );

                    if(adv.parent() != null){
                        CruxAdvancement parent = advancementManager().getAdvancement(adv.parent());
                        if(parent != null && parent.getIcon() instanceof CrazyAdvancementDisplay parentDisplay && !parent.key().value().endsWith("root")){
                            crux.addLoreFromString(
                                "<dark_gray>Parent: " + parentDisplay.getTitle(),
                                ""
                            );
                        }
                    }
                    if(desc != null){
                        crux.addLoreFromString(CruxString.buildDescription(
                            desc, module.description_length.value().intValue(),
                            s -> module.description_color.getOrDefault(display.getFrame().toString().toLowerCase(), "<green>") + s
                        ));
                    }
                }
            })
            .item();
    }

    public boolean isVisible(CruxAdvancement adv, Player p){
        if(adv.key().value().endsWith("/root")) return true;
        if(!(adv.getIcon() instanceof CrazyAdvancementDisplay display)) return true;
        CrazyAdvancementManager<?> crazy = (CrazyAdvancementManager<?>) selectedManager;
        if(display.getVisibility().isVisible(p, crazy.getCrazyAdvancement(adv.key()))) return true;

        CruxAdvancement child = findFirstChildIfOnlyOne(adv, selectedManager);
        if(child == null) return false;
        return isVisible(child, p);
    }

    public CruxAdvancement findFirstChildIfOnlyOne(CruxAdvancement adv, CruxAdvancementManager<?> manager) {
        CruxAdvancement singleChild = null;
        for (CruxAdvancement check : manager) {
            if (adv.key().equals(check.parent())) {
                if (singleChild != null) return null; // more than one child
                singleChild = check;
            }
        }
        return singleChild;
    }

    public CrazyAdvancementDisplay getDisplay(CruxAdvancement advance){
        if(advance == null) return null;
        if(!(advance.getIcon() instanceof CrazyAdvancementDisplay display)) return null;
        return display;
    }

    public CruxAdvancement getAdvancement(Key key){
        return advancementManager().getAdvancement(key);
    }

    public CrazyAdvancementDisplay getDisplay(Key key){
        return getDisplay(getAdvancement(key));
    }
}

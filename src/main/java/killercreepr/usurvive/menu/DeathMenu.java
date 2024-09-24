package killercreepr.usurvive.menu;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxItem;
import killercreepr.cruxmenus.core.menu.BukkitMenu;
import killercreepr.usurvive.death.PlayerDeath;
import killercreepr.usurvive.entity.mob.goals.EchoGoal;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class DeathMenu extends BukkitMenu {
    protected final @NotNull EchoGoal echo;
    protected final @NotNull PlayerDeath death;
    public DeathMenu(@NotNull EchoGoal echo, @NotNull PlayerDeath death) {
        super();
        this.echo = echo;
        this.death = death;
        reconstruct(54, Component.text(""));
        load();
    }

    public DeathMenu(@NotNull UUID uuid, @NotNull EchoGoal echo, @NotNull PlayerDeath death) {
        super(uuid);
        this.echo = echo;
        this.death = death;
    }

    public DeathMenu(@NotNull UUID uuid, @NotNull Inventory inventory, @NotNull EchoGoal echo, @NotNull PlayerDeath death) {
        super(uuid, inventory);
        this.echo = echo;
        this.death = death;
    }

    @Override
    public void load() {
        super.load();
        inventory.setContents(death.getDrops().toArray(new ItemStack[0]));
    }

    public @NotNull Collection<ItemStack> getItems(){
        Collection<ItemStack> list = new ArrayList<>();
        for(ItemStack i : getInventory()){
            if(CruxItem.isEmpty(i)) continue;
            list.add(i);
        }
        return list;
    }

    public void onClick(){
        Crux.getServer().getScheduler().runTask(Crux.getMainPlugin(), task ->{
            Collection<ItemStack> list = getItems();
            death.setDrops(list);
            echo.inventoryClicked(list);
            if(list.isEmpty()){
                for(HumanEntity e : getViewers()){
                    if(!CruxItem.isEmpty(e.getItemOnCursor())) return;
                }
                close();
            }
        });
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(false);
        super.onDrag(event);
    }

    @Override
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(false);
        super.onMenuClick(event);
        onClick();
    }

    @Override
    public void onInvClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(false);
        super.onInvClick(event);
        onClick();
    }
}

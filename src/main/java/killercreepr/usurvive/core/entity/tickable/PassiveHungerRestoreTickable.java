package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxcharms.api.charm.Charm;
import killercreepr.cruxcharms.api.charm.CharmData;
import killercreepr.cruxcharms.api.charm.CharmNameMutable;
import killercreepr.cruxcharms.api.charm.IdentifiedCharm;
import killercreepr.cruxcharms.core.component.CharmComponents;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickableModifier;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickablesContainer;
import killercreepr.cruxtickables.core.component.CruxTickableComponents;
import killercreepr.cruxtickables.core.entity.tickable.SimpleActiveEntityTickable;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PassiveHungerRestoreTickable extends SimpleActiveEntityTickable {
    protected final HumanEntity p;
    protected final int tickRate;
    protected final int nutrition;
    protected final float saturation;
    public PassiveHungerRestoreTickable(Entity entity, EntityTickable tickable, CruxSlot slot, int tickRate, int nutrition, float saturation) {
        super(entity, tickable, slot);
        p = (HumanEntity) entity;
        this.tickRate = tickRate;
        this.nutrition = nutrition;
        this.saturation = saturation;
    }

    public ItemStack getItem(){
        if(slot == null) return null;
        var bukkit = AttributeBukkitAdaptor.adapt(slot);
        if(bukkit == null) return null;
        return p.getInventory().getItem(bukkit);
    }

    public EntityTickableModifier findModifier(Collection<EntityTickableModifier> tickables){
        for (EntityTickableModifier mod : tickables) {
            if(mod.getSlotGroup().test(slot) && mod.getTickable().compare(this.tickable)) return mod;
        }
        return null;
    }

    public void foodTick(){
        if(p.getFoodLevel() > 19) return;

        ItemStack item = getItem();
        if(!CruxItem.isEmpty(item)){
            itemUseLogic(CruxItem.wrap(item));
        }

        p.setFoodLevel(Math.min(p.getFoodLevel()+nutrition, 20));
        p.setSaturation(p.getSaturation()+saturation);
    }

    public void itemUseLogic(CruxItem item){
        var tickables = item.getOrDefaultData(CruxTickableComponents.ENTITY_TICKABLES);
        if(tickables == null) return;
        EntityTickableModifier mod = findModifier(tickables.getTickableModifiers());
        if(mod == null) return;
        if(!(mod.getData() instanceof Map<?,?> map)) return;
        if(!(map.get("uses") instanceof Number n)) return;
        int uses = n.intValue()+1;

        int maxUses;
        if(map.get("max_uses") instanceof Number max) maxUses = max.intValue();
        else maxUses = -1;

        Collection<EntityTickableModifier> newMods = new ArrayList<>(tickables.getTickableModifiers());
        if(!newMods.remove(mod)){
            Crux.logError("THIS SHOULD NOT HAPPEN AHHHHHHH " + entity.getName() + " PASSIVE HUNGER RESTORE TICKABLE");
            return;
        }

        if(maxUses != -1 && uses >= maxUses){
            item.set(CruxTickableComponents.ENTITY_TICKABLES, newMods.isEmpty() ? null : EntityTickablesContainer.container(newMods));

            if(!(map.get("charm_id") instanceof Key key)) return;
            CharmData data = item.getOrDefaultData(CharmComponents.CHARM_DATA);
            if(data == null) return;
            int slot = -1;
            for (Charm charm : data.charms()) {
                slot++;
                if(!(charm instanceof IdentifiedCharm identified)) continue;
                if(!identified.getCharmID().equals(key)) continue;
                CharmData newData = data.removeCharm(slot);
                item.set(CharmComponents.CHARM_DATA, newData);
                break;
            }
            return;
        }

        Map newMap = new HashMap<>(map);
        newMap.put("uses", uses);
        newMods.add(mod.withData(newMap));
        item.set(CruxTickableComponents.ENTITY_TICKABLES, EntityTickablesContainer.container(newMods));

        if(!(map.get("charm_id") instanceof Key key)) return;
        if(!(map.get("charm_name_format") instanceof String nameFormat)) return;
        CharmData data = item.getOrDefaultData(CharmComponents.CHARM_DATA);
        if(data == null) return;
        int slot = -1;
        for (Charm charm : data.charms()) {
            slot++;
            if(!(charm instanceof IdentifiedCharm identified)) continue;
            if(!(charm instanceof CharmNameMutable mutable)) continue;
            if(!identified.getCharmID().equals(key)) continue;

            String newName = Crux.format().deserializeString(nameFormat,
                TagContainer.string(
                    Tag.parsed("uses", uses + ""),
                    Tag.parsed("max_uses", maxUses + "")
                ));

            CharmData newData = data.setCharm(slot, mutable.withName(newName));
            item.set(CharmComponents.CHARM_DATA, newData);
            break;
        }
    }

    @Override
    public void tick() {
        if(Crux.getCurrentTick() % tickRate != 0) return;
        foodTick();
    }
}

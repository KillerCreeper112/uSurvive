package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.core.data.Pack;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ItemPackHolder extends EntityDataHolder implements Loadable {
    public static ItemPackHolder itemPackHolder(Entity e){
        return EntityMemory.getOrCreateDataHolder(e, ItemPackHolder.class, ItemPackHolder::new);
    }

    public static final Key KEY = Crux.key("item_pack");
    public ItemPackHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
        load();
    }
    public ItemPackHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        save();
    }

    protected final Pack pack = new Pack();

    public ItemPackHolder setPack(@Nullable Pack map){
        pack.get().clear();
        if(map == null) return this;
        pack.get().putAll(map.get());
        return this;
    }

    public Pack getPack() {
        return pack;
    }

    public DataFile file(boolean createIfNeed){
        return BukkitDataFile.parseFromGeneralPath(CruxFolder.file(Crux.getMainPlugin(), "data/usurvive/player/item_pack/" + parent.getUUID() + ".json"), createIfNeed);
    }

    @Override
    public void save() {
        if(pack.isEmpty()){
            var file = file(false);
            if(file != null) file.delete();
            return;
        }
        var file = file(true);
        FileObject o = new FileObject();
        pack.get().forEach((page, items) ->{
            FileObject inside = new FileObject();
            items.get().forEach((slot, item) ->{
                inside.addProperty(slot + "", Base64.getEncoder().encodeToString(item.serializeAsBytes()));
            });
            o.add(page + "", inside);
        });
        file.serialize("items", o);
        file.save();
    }

    @Override
    public void load() {
        var file = file(false);
        if(file == null) return;
        if(!(file.getElement("items") instanceof FileObject o)){
            file.close();
            return;
        }
        o.forEach((s, ele) ->{
            if(!(ele instanceof FileObject inside)) return;
            int index;
            try{ index = Integer.parseInt(s); }
            catch (IllegalArgumentException ignored){ return; }
            Pack.Page page = new Pack.Page();
            inside.forEach((ss, element) ->{
                int slot;
                try{ slot = Integer.parseInt(ss); }
                catch (IllegalArgumentException ignored){ return; }
                ItemStack item = ItemStack.deserializeBytes(Base64.getDecoder().decode(element.getAsString()));
                page.set(slot, item);
            });
            pack.setPage(index, page);
        });
        file.close();
    }
}

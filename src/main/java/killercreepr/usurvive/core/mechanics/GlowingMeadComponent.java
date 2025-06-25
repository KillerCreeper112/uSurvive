package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.context.PlayerItemedContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GlowingMeadComponent implements InteractableComponent, ConsumableComponent {
    protected final float range;
    protected final List<PotionEffect> nearbyPotions;
    protected final List<StoredPotion> cruxPotions;

    public GlowingMeadComponent(float range, List<PotionEffect> nearbyPotions, List<StoredPotion> cruxPotions) {
        this.range = range;
        this.nearbyPotions = nearbyPotions;
        this.cruxPotions = cruxPotions;
    }

    public float getRange() {
        return range;
    }

    public List<PotionEffect> getNearbyPotions() {
        return nearbyPotions;
    }

    public List<StoredPotion> getCruxPotions() {
        return cruxPotions;
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        if(ctx.getEntityClicked() != null && ctx.getAction().isLeftClick()) return ItemUseResult.empty();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        spit(p, item, ctx);
        return ItemUseResult.cancelled();
    }

    public boolean spit(Player p, CruxItem item,
                        PlayerItemedContext ctx){

        new GetEntityNear<>(LivingEntity.class)
            .center(p)
            .range(range)
            .filter(hit -> isValidHit(hit, p))
            .find().forEach(hit ->{
                hit.addPotionEffects(nearbyPotions);
            });


        if(cruxPotions == null) return true;
        PotionHolder holder = EntityMemory.getOrCreateDataHolder(p, SimplePotionHolder.class, SimplePotionHolder::new);
        if(holder == null) return true;
        cruxPotions.forEach(stored ->{
            var active = holder.getPotion(stored.getPotion());
            if(active != null){
                if(active.getAmplifier() > stored.getAmplifier()) return;
                stored = stored.withDuration(active.getDuration()+stored.getDuration());
            }
            holder.addPotion(stored.create(p));
        });
        return true;
    }

    public boolean isValidHit(Entity e, Entity owner){
        return !e.equals(owner) && CruxEntityUtil.isRealisticallyVisibleTo(e, owner);
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        if(ctx.getAction() == Action.PHYSICAL) return false;
        CruxItem item = ctx.getItem();
        if(item.item().getData(DataComponentTypes.CONSUMABLE) != null) return false;
        return true;
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        spit(p, item, ctx);
        return ItemConsumeResult.empty();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }
}

package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.*;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.core.mechanics.blast_tempest.BlastTempestPlayer;
import killercreepr.usurvive.core.mechanics.bone_flute.FluteSoundSequence;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class BlastTempestComponent implements ConsumableComponent, InteractableComponent {
    protected final List<RayData> rays;
    protected final double range;
    protected final double yawDegrees;
    protected final double pitchDegrees;
    protected final int itemDamage;

    public BlastTempestComponent(List<RayData> rays, double range, double yawDegrees, double pitchDegrees, int itemDamage) {
        this.rays = rays;
        this.range = range;
        this.yawDegrees = yawDegrees;
        this.pitchDegrees = pitchDegrees;
        this.itemDamage = itemDamage;
    }

    public double getRange() {
        return range;
    }

    public double getYawDegrees() {
        return yawDegrees;
    }

    public double getPitchDegrees() {
        return pitchDegrees;
    }

    public int getItemDamage() {
        return itemDamage;
    }

    public List<RayData> getRays() {
        return rays;
    }

    @NotNull
    @Override
    public ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        var p = ctx.getPlayer();

        CruxItem item = ctx.getItem();
        if(!CruxEntityUtil.isNonSurvival(p)) p.damageItemStack(ctx.getSlot(), itemDamage);
        var useCooldown = item.item().getData(DataComponentTypes.USE_COOLDOWN);
        if(useCooldown != null){
            p.setCooldown(item.item(), (int) (useCooldown.seconds() * 20f));
        }
        return ItemConsumeResult.cancelled();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }

    public static Consumer<FluteSoundSequence.FluteSound> genericPlaySound(Entity p){
        return sound ->{
            sound.sound().playAt(p);
            Location loc = CruxLoc.shift(CruxEntityUtil.getEyeOrLocation(p), .5,
                (p.getPitch() > 50 ? .2 : (p.getPitch() > 12 ? -.1 : -.5)), .2);

            double note = CruxMath.random(1,24) / 24D;
            loc.getWorld().spawnParticle(Particle.NOTE, loc, 0, note, 0, 0, 1);
        };
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        var p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        if(p.hasCooldown(item.item())) return ItemUseResult.cancelled();

        int consumeTime;
        var data =  item.item().getData(DataComponentTypes.CONSUMABLE);
        if(data == null) consumeTime = 100;
        else{
            consumeTime = (int) (data.consumeSeconds() * 20f);
        }

        new BlastTempestPlayer(
            rays, null,
            () -> p.hasActiveItem() && p.getActiveItem().equals(item.item()),
            consumeTime,
            () -> p.getEyeLocation(),
            e ->{
                if(e.equals(p)) return false;
                return true;
            },
            range,
            yawDegrees,
            pitchDegrees
        ).play();
        return ItemUseResult.empty();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick();
    }

    public static class RayData{
        public final CruxAttributeContainer attributes;
        public final double circleRadius;
        public final boolean homing;

        public RayData(CruxAttributeContainer attributes, double circleRadius, boolean homing) {
            this.attributes = attributes;
            this.circleRadius = circleRadius;
            this.homing = homing;
        }

        public CruxAttributeContainer getAttributes() {
            return attributes;
        }
    }
}

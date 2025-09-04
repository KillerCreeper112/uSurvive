package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxTeamComparer;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.core.mechanics.blast_tempest.BlastTempestPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlastTempestComponent implements ConsumableComponent, InteractableComponent {
    protected final List<RayData> rays;
    protected final double range;
    protected final double yawDegrees;
    protected final double pitchDegrees;
    protected final int itemDamage;
    protected final CreateSound launchSound;
    protected final CreateSound lockOnSound;

    public BlastTempestComponent(List<RayData> rays, double range, double yawDegrees, double pitchDegrees, int itemDamage, CreateSound launchSound, CreateSound lockOnSound) {
        this.rays = rays;
        this.range = range;
        this.yawDegrees = yawDegrees;
        this.pitchDegrees = pitchDegrees;
        this.itemDamage = itemDamage;
        this.launchSound = launchSound;
        this.lockOnSound = lockOnSound;
        new ArrayList<>(rays).forEach(data ->{
            if(data.dupeAmount < 1) return;
            for(byte i = 0; i < data.dupeAmount; i++){
                rays.add(data);
            }
        });
    }

    public CreateSound getLockOnSound() {
        return lockOnSound;
    }

    public CreateSound getLaunchSound() {
        return launchSound;
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

        CruxTeamComparer teamComparer = CruxTeamComparer.teamComparer(false, team -> !team.allowFriendlyFire());
        new BlastTempestPlayer(
            rays,
            player ->{
                var useCooldown = item.item().getData(DataComponentTypes.USE_COOLDOWN);
                if(useCooldown != null){
                    p.setCooldown(item.item(), (int) (useCooldown.seconds() * 20f));
                }
            },
            player ->{
                var useCooldown = item.item().getData(DataComponentTypes.USE_COOLDOWN);
                if(useCooldown != null){
                    p.setCooldown(item.item(), (int) (useCooldown.seconds() * 20f));
                }
            },
            () -> CruxEntityUtil.isValid(p) && p.hasActiveItem() && p.getActiveItem().equals(item.item()),
            consumeTime,
            () -> p.getEyeLocation(),
            e ->{
                if(e.equals(p)) return false;
                if(!CruxMobGoal.UNDESIRED_BEHAVIOR.test(e)) return false;
                if(teamComparer.areOnSameTeam(p, e)) return false;
                return true;
            },
            range,
            yawDegrees,
            pitchDegrees,
            launchSound,
            lockOnSound
        ).play();
        return ItemUseResult.empty();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick();
    }

    public static class RayData{
        public final CruxAttributeContainer attributes;
        public final String type;
        public final double circleRadius;
        public final boolean homing;
        public final byte dupeAmount;

        public RayData(CruxAttributeContainer attributes, String type, double circleRadius, boolean homing, byte dupeAmount) {
            this.attributes = attributes;
            this.type = type;
            this.circleRadius = circleRadius;
            this.homing = homing;
            this.dupeAmount = dupeAmount;
        }

        public CruxAttributeContainer getAttributes() {
            return attributes;
        }
    }
}

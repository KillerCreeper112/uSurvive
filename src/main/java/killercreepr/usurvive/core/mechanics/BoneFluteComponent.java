package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.*;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.combat.EntityHit;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateLine;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.core.mechanics.bone_flute.FlutePlayer;
import killercreepr.usurvive.core.mechanics.bone_flute.FluteSoundSequence;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BoneFluteComponent implements ConsumableComponent, InteractableComponent {
    public static final List<FluteSoundSequence> SEQUENCES = List.of(
        // Rising Scale (creepy build-up)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.1f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.4f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.5f), 5)
        )),

        // Falling Scale (descending eerie echo)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.5f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.1f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.9f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.7f), 6)
        )),

        // Pulse Beat (repeated eerie two-note pulse)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 8),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 12)
        )),

        // Dissonant Chime (skips pitches for unsettling vibe)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.4f), 7),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.9f), 10),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.1f), 8)
        )),

        // Echo Chant (long gaps, ritual-like)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 12),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.8f), 14),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.1f), 16),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.9f), 14)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.1f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 1.3f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.4f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f), 6)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.8f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.2f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.9f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.1f), 5)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.4f), 3),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.6f), 3),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.8f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.2f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.5f), 6)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 0.9f), 10),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 12),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.1f), 14),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f), 16),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.8f), 12)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 1.2f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.4f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.6f), 4),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.8f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 2.0f), 6)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.4f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.6f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.8f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.7f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 2.0f), 2)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.5f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.7f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.6f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.8f), 2),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 2.0f), 2)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.4f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.6f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.8f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_HARP, 2.0f), 2)
        )),

        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.4f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 1),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.6f), 1)
        ))
    );

    public static CreateSound sound(Sound sound){
        return sound(sound, 1f);
    }

    public static CreateSound sound(Sound sound, float pitch){
        return sound(sound, 2.5f, pitch);
    }

    public static CreateSound sound(Sound sound, float volume, float pitch){
        return CreateSound.sound(sound, volume, pitch);
    }

    protected final FluteSoundSequence sequence;
    protected final List<RayData> rays;
    protected final int itemDamage;

    public FluteSoundSequence getSequence() {
        return sequence;
    }

    public BoneFluteComponent(FluteSoundSequence sequence, List<RayData> rays, int itemDamage) {
        this.sequence = sequence;
        this.rays = rays;
        this.itemDamage = itemDamage;
    }


    public int getItemDamage() {
        return itemDamage;
    }

    public List<RayData> getRays() {
        return rays;
    }

    public void launch(Entity user, Location loc){
        List<Ray> rays = new ArrayList<>();
        Vector dir = loc.getDirection();

        Map<UUID, Long> alreadyHit = new HashMap<>();
        CruxTeamComparer teamComparer = CruxTeamComparer.teamComparer(false, team -> !team.allowFriendlyFire());
        Predicate<Entity> filter = e -> CruxMob.UNDESIRED_BEHAVIOR.test(e) && !e.equals(user) &&
            !CruxMath.hasOccurredWithin(alreadyHit.getOrDefault(e.getUniqueId(), 0L), 5) &&
            !teamComparer.areOnSameTeam(user, e);

        this.rays.forEach(rayData ->{
            Location l = loc.clone();
            l.setDirection(rayData.rotateDirection(dir));

            Ray ray = new Ray(
                l, (int) rayData.attributes.getValue(CruxAttribute.ATTACK_RANGE),
                rayData.attributes.getValue(CruxAttribute.ATTACK_SPEED),
                rayData.attributes, filter,
                (hit, progress) ->{
                    float potency = Math.max(0.2f, 1f - (.7f * progress));

                    EntityDamager.entityDamager(
                        hit, user
                    ).attack(
                        CruxMath.hasOccurredWithin(alreadyHit.getOrDefault(hit.getUniqueId(), 0L), 10) ?
                            0D : rayData.attributes.getValue(CruxAttribute.ATTACK_DAMAGE) * potency,
                        rayData.attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK) * potency,
                        rayData.attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP) * potency
                    );
                    alreadyHit.put(hit.getUniqueId(), System.currentTimeMillis());
                }
            );
            rays.add(ray);
        });
        new ShotRay(rays).start();
        CreateSound.sound(Sound.ENTITY_BREEZE_WIND_BURST,
                2.5f, 1.5f)
            .playAt(loc);
    }

    @NotNull
    @Override
    public ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        var p = ctx.getPlayer();

        launch(p, p.getEyeLocation());

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

    public FluteSoundSequence sequenceOrDefault(){
        return this.sequence == null ? CruxCollection.getRandom(SEQUENCES) : this.sequence;
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

        FluteSoundSequence sequence = sequenceOrDefault();

        new FlutePlayer(
            sequence,
            genericPlaySound(p),
            null,
            () -> CruxEntityUtil.isValid(p) && p.hasActiveItem() && p.getActiveItem().equals(item.item())
        ).play(consumeTime);
        return ItemUseResult.empty();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick();
    }

    public static class RayData{
        protected final NumberProvider yaw;
        protected final NumberProvider pitch;
        protected final CruxAttributeContainer attributes;

        public RayData(NumberProvider yaw, NumberProvider pitch, CruxAttributeContainer attributes) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.attributes = attributes;
        }

        @Contract(pure = true)
        public Vector rotateDirection(Vector dir){
            if(yaw == null && pitch == null) return dir;
            return CruxMath.rotateDirection(dir, yaw == null ? 0f : yaw.value().floatValue(), pitch == null ? 0f : pitch.value().floatValue());
        }

        public NumberProvider getYaw() {
            return yaw;
        }

        public NumberProvider getPitch() {
            return pitch;
        }

        public CruxAttributeContainer getAttributes() {
            return attributes;
        }
    }

    public static class Ray{
        protected final Location loc;
        protected final int maxRange;
        protected final double speed;
        protected final CruxAttributeContainer attributes;
        protected final Predicate<Entity> entityFilter;
        protected final BiConsumer<Entity, Float> entityHit;

        public Ray(Location loc, int maxRange, double speed, CruxAttributeContainer attributes, Predicate<Entity> entityFilter, BiConsumer<Entity, Float> entityHit) {
            this.loc = loc;
            this.maxRange = maxRange;
            this.speed = speed;
            this.attributes = attributes;
            this.entityFilter = entityFilter;
            this.entityHit = entityHit;
        }

        public float getProgress(){
            return (float) range / (float) maxRange;
        }

        public RayTraceResult hasHitBlock(EntityHit.Result result){
            for (RayTraceResult ray : result.getResults()) {
                if(ray.getHitBlock() != null) return ray;
            }
            return null;
        }

        protected Location lastLoc;

        protected int range = 0;
        public boolean tick(){
            range++;
            if(range >= maxRange){
                return true;
            }

            if(lastLoc == null) lastLoc = loc;
            Location l = CruxLoc.shift(lastLoc, speed, 0D, 0D);

            float progress = getProgress();
            float potency = Math.max(0.2f, 1f - (.7f * progress));
            var result = new EntityHit(
                lastLoc, speed+.05,
                attributes.getValue(CruxAttribute.ATTACK_AOE) * potency,
                (int) attributes.getValue(CruxAttribute.ATTACK_PIERCE) + 1,
                false
            ).getHitEntities(entityFilter);

            boolean stop = false;
            RayTraceResult hitBlock = hasHitBlock(result);
            if(hitBlock != null){
                stop = true;
                l = hitBlock.getHitPosition().toLocation(l.getWorld());
            }
            result.getHit().forEach(e ->{
                entityHit.accept(e, progress);
            });

            ShapeScheduler.builder()
                .shape(CreateLine.builder()
                    .spacing(1)
                    .start(CruxPosition.precise(lastLoc))
                    .end(CruxPosition.precise(l))
                    .build())
                .locationTick(ctx ->{
                    Location parL = ctx.getLocation().toLocation(loc.getWorld());
                    parL.getWorld().spawnParticle(
                        Particle.SWEEP_ATTACK, parL, 0
                    );
                })
                .buildCached().scheduleAsync(0);
            CreateSound.sound(Sound.ENTITY_SHULKER_BULLET_HIT,
                net.kyori.adventure.sound.Sound.Source.AMBIENT,
                1.5f, 1.5f).playAt(l);
            lastLoc = l;
            return stop;
        }
    }

    public static class ShotRay{
        protected final List<Ray> rays;

        public ShotRay(List<Ray> rays) {
            this.rays = rays;
        }

        public void start(){
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    tick();
                }
            };
            runnable.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
        }

        protected BukkitRunnable runnable;
        public void tick(){
            rays.removeIf(Ray::tick);
            if(rays.isEmpty()){
                stop();
            }
        }

        public void stop(){
            try{
                runnable.cancel();
                runnable = null;
            } catch (NullPointerException | IllegalStateException e) {
            }
        }
    }
}

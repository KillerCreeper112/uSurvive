package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.combat.EntityHit;
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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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

        // Ritual Echo (flute + ambient cave + click)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.AMBIENT_CAVE), 12),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.9f), 8),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_AMETHYST_BLOCK_CHIME), 10)
        )),

        // Ghostly Whispers (flute + warden/ambient)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.2f), 8),
            FluteSoundSequence.fluteSound(sound(Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD), 14),
            FluteSoundSequence.fluteSound(sound(Sound.ENTITY_WARDEN_AMBIENT, 0.5f), 10),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 6)
        )),

        // Jungle Spirits (flute + parrot + bamboo)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.ENTITY_PARROT_AMBIENT, 0.8f), 12),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_BAMBOO_HIT, 1.1f), 7),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.4f), 5),
            FluteSoundSequence.fluteSound(sound(Sound.ENTITY_PARROT_FLY, 0.8f), 8)
        )),

        // Haunted Chant (flute + ghast + amethyst)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.0f), 6),
            FluteSoundSequence.fluteSound(sound(Sound.ENTITY_GHAST_AMBIENT, 0.6f), 16),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.2f), 10),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.3f), 8)
        )),

        // Abyssal Pulse (flute + sculk + warden thud)
        FluteSoundSequence.fluteSoundSequence(List.of(
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_SCULK_SENSOR_CLICKING), 5),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.9f), 7),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 0.6f), 14),
            FluteSoundSequence.fluteSound(sound(Sound.ENTITY_WARDEN_HEARTBEAT, 0.7f), 10),
            FluteSoundSequence.fluteSound(sound(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.1f), 8)
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

    public void launch(Entity user, Location loc, CruxAttributeContainer attributes){
        List<Ray> rays = new ArrayList<>();
        Vector dir = loc.getDirection();

        Predicate<Entity> filter = e -> !e.equals(user);

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
                        attributes.getValue(CruxAttribute.ATTACK_DAMAGE) * potency,
                        attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK) * potency,
                        attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP) * potency
                    );
                }
            );
            rays.add(ray);
        });
        new ShotRay(rays).start();
    }

    @NotNull
    @Override
    public ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        var p = ctx.getPlayer();
        var crux = ctx.getItem();

        launch(p, p.getEyeLocation(), crux.getOrDefaultData(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES));

        if(!CruxEntityUtil.isNonSurvival(p)) p.damageItemStack(ctx.getSlot(), itemDamage);
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

        FluteSoundSequence sequence = this.sequence == null ? CruxCollection.getRandom(SEQUENCES) : this.sequence;

        new FlutePlayer(
            sequence,
            sound ->{
                sound.sound().playAt(p);
            },
            () ->{
                return p.hasActiveItem() && p.getActiveItem().equals(item.item());
            }
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

            var result = new EntityHit(
                lastLoc, speed+.05,
                attributes.getValue(CruxAttribute.ATTACK_AOE),
                (int) attributes.getValue(CruxAttribute.ATTACK_PIERCE) + 1,
                false
            ).getHitEntities(entityFilter);

            boolean stop = false;
            RayTraceResult hitBlock = hasHitBlock(result);
            if(hitBlock != null){
                stop = true;
                l = hitBlock.getHitPosition().toLocation(l.getWorld());
            }
            float progress = getProgress();
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

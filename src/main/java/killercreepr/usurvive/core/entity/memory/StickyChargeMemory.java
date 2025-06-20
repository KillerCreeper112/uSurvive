package killercreepr.usurvive.core.entity.memory;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.core.entity.memory.DataHolderRegistry;
import killercreepr.crux.core.entity.memory.SimpleEntityMemoryMainThread;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StickyChargeMemory extends SimpleEntityMemoryMainThread {
    protected int fuseTicks;
    public StickyChargeMemory(@NotNull Entity e, DataHolderRegistry dataHolders) {
        super(e, dataHolders);
    }

    public StickyChargeMemory(DataHolderRegistry dataHolders, @NotNull UUID uuid, @NotNull Holder<? extends Entity> holder) {
        super(dataHolders, uuid, holder);
    }

    public StickyChargeMemory(@NotNull Entity e) {
        super(e);
    }

    public StickyChargeMemory(@NotNull UUID uuid, @NotNull Holder<? extends Entity> holder) {
        super(uuid, holder);
    }

    public int getFuseTicks() {
        return fuseTicks;
    }

    public void setFuseTicks(int fuseTicks) {
        this.fuseTicks = fuseTicks;
    }

    public void explode(){
        Entity entity = value();
        double range = CruxAttribute.get(entity, CruxAttribute.ATTACK_RANGE);
        new GetEntityNear<>(Entity.class)
            .filter(e -> !e.equals(value()) && CruxMobGoal.UNDESIRED_BEHAVIOR.test(e))
            .center(entity.getLocation())
            .range(range)
            .find().forEach(hit ->{
                EntityDamager.entityDamager(hit, entity)
                    .attackWithMultiplier(
                        CruxMath.distanceFalloff(hit, entity, range, 1.75f)
                    );
            });
        entity.remove();

        new ParticleBuilder(Particle.EXPLOSION)
            .count(CruxMath.random(4,6))
            .location(entity.getLocation())
            .offset(range/3, range/3, range/3)
            .extra(.1)
            .spawn()
            ;
        CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.4f).playAt(entity);
    }

    protected byte fullCooldown = 5;
    protected byte cooldown = 0;
    @Override
    public boolean tick() {
        if(super.tick()) return true;
        onTick();
        return false;
    }

    public byte getFullCooldown() {
        return fullCooldown;
    }

    public void setFullCooldown(byte fullCooldown) {
        this.fullCooldown = fullCooldown;
    }

    public byte getCooldown() {
        return cooldown;
    }

    public void setCooldown(byte cooldown) {
        this.cooldown = cooldown;
    }

    public Entity getHitEntity() {
        return hitEntity;
    }

    public void setHitEntity(Entity hitEntity) {
        this.hitEntity = hitEntity;
    }

    public float getHitEntityRotation() {
        return hitEntityRotation;
    }

    public void setHitEntityRotation(float hitEntityRotation) {
        this.hitEntityRotation = hitEntityRotation;
    }

    public Vector getHitEntityOffset() {
        return hitEntityOffset;
    }

    public void setHitEntityOffset(Vector hitEntityOffset) {
        this.hitEntityOffset = hitEntityOffset;
    }

    public Block getHitBlock() {
        return hitBlock;
    }

    public void setHitBlock(Block hitBlock) {
        this.hitBlock = hitBlock;
    }

    protected byte hitTimes;
    public void onDamage(EntityDamageEvent event){
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.SUICIDE){
            return;
        }
        if(!(event instanceof EntityDamageByEntityEvent e)){
            event.setCancelled(true);
            return;
        }
        if(CruxEntityUtil.isNonSurvival(e.getDamager())){
            return;
        }
        hitTimes++;
        if(hitTimes > 5){
            hitTimes = 0;
            cooldown = 10;
            reset(event.getEntity());
        }
    }

    public void onTickEntity(Entity e){
        if(!isHitEntityValid(e)){
            reset(e);
            cooldown = 3;
            return;
        }

        // Stick to the original offset position relative to the hit entity
        // Calculate rotated offset
        float yawDiff = ((LivingEntity) hitEntity).getBodyYaw() - hitEntityRotation;
        Vector rotatedOffset = rotateYaw(hitEntityOffset.clone(), yawDiff);
        e.teleport(hitEntity.getLocation().toVector().subtract(rotatedOffset).toLocation(e.getWorld()));
    }
    public static Vector rotateYaw(Vector vec, float degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = vec.getX() * cos - vec.getZ() * sin;
        double z = vec.getX() * sin + vec.getZ() * cos;
        return new Vector(x, vec.getY(), z);
    }

    public void onHit(Entity e){
        e.setGravity(false);
        e.setVelocity(new Vector());
        if(e instanceof LivingEntity d){
            d.setCollidable(false);
        }
        if(e instanceof Mob m){
            m.setAI(false);
        }
    }

    public void reset(Entity e){
        hitBlock = null;
        hitEntity = null;
        hitTimes = 0;
        onUnhit(e);
    }

    public void onUnhit(Entity e){
        e.setGravity(true);
        if(e instanceof LivingEntity d){
            d.setCollidable(true);
        }
        if(e instanceof Mob m){
            m.setAI(true);
        }
    }

    public void onTickBlock(Entity e){
        if(!isHitBlockValid(hitBlock)){
            reset(e);
            cooldown = 3;
        }
    }

    public boolean isHitEntityValid(Entity e){
        return CruxEntityUtil.isValid(e);
    }

    public boolean isHitBlockValid(Block b){
        if(!b.isSolid() || b.isLiquid()) return false;
        return true;
    }

    public Block getHitBlock(World world,BoundingBox box) {
        int minX = (int) Math.floor(box.getMinX());
        int maxX = (int) Math.floor(box.getMaxX());
        int minY = (int) Math.floor(box.getMinY());
        int maxY = (int) Math.ceil(box.getMaxY()); // Use ceil to catch top edge
        int minZ = (int) Math.floor(box.getMinZ());
        int maxZ = (int) Math.floor(box.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (block.isSolid() && !block.isLiquid()) {
                        BoundingBox blockBox = block.getBoundingBox();
                        if (box.overlaps(blockBox)) {
                            return block;
                        }
                    }
                }
            }
        }

        return null;
    }

    public void playAnimation(){
        new ModelEntity(value(), CruxMob.getKey(value()).value())
            .playAnimation("explode", 0D, 0D, calculateAnimationSpeed(), true);
    }

    public double calculateAnimationSpeed(){
        return 60D / fuseTicks;
    }

    protected Entity hitEntity;
    protected float hitEntityRotation;
    protected Vector hitEntityOffset; //Maybe need?
    protected Block hitBlock;
    public void onTick(){
        if(fullCooldown > 0){
            fullCooldown--;
            if(fullCooldown < 1){
                playAnimation();
            }
            return;
        }

        fuseTicks--;
        if(fuseTicks < 1){
            explode();
            //return;
        }
        if(cooldown > 0){
            cooldown--;
            return;
        }

        Entity e = value();
        World world = e.getWorld();
        BoundingBox box = e.getBoundingBox().expand(.25, .25, .25);
        if(CruxEntityUtil.isValid(hitEntity)){
            onTickEntity(e);
            return;
        }
        if(hitBlock != null){
            onTickBlock(e);
            return;
        }

        if(world.hasCollisionsIn(box)){
            hitBlock = getHitBlock(world, box);
            hitEntity = null;
            onHit(e);
            return;
        }

        for (Entity hit : world.getNearbyEntities(box, hit -> {
            return !hit.equals(e) && hit instanceof LivingEntity;
        })) {
            hitBlock = null;
            hitEntity = hit;
            hitEntityRotation = ((LivingEntity) hit).getBodyYaw();
            hitEntityOffset = hit.getLocation().toVector().subtract(e.getLocation().toVector());
            onHit(e);
            break;
        }

    }
}

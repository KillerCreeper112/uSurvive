package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.text.context.InputContext;
import killercreepr.crux.api.text.provider.StringTagProvider;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.death.PlayerDeath;
import killercreepr.usurvive.core.entity.mob.Echo;
import killercreepr.usurvive.core.menu.DeathMenu;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.logging.Level;

public class EchoGoal extends CruxMobModeledGoal implements Listener {
    public EchoGoal(@NotNull Mob mob) {
        this(CruxMobGoal.defaultKey(), mob);
    }

    public EchoGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
        exp = CruxTag.get(mob, "exp", PersistentDataType.INTEGER, null);

        sounds(new CruxGoalSounds(mob) {
            @Override
            public @Nullable CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_VEX_CHARGE, 0.4f,2f);
            }

            @Override
            public @Nullable CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_VEX_HURT, 0.4f,2f);
            }

            @Override
            public @Nullable CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_VEX_AMBIENT, 0.4f,2f);
            }
        });

        PlayerDeath death = getPlayerDeathOrNull();
        if(death==null) return;
        firstSetup(death);
    }

    protected Integer exp;
    protected Integer expToDropEachHit;
    protected State state;

    public void transformToEcho(){
        ModeledEntity modeled = getModeledEntity();
        modeled.removeModel("grave");
        applyModel(ActiveModel::destroy);
        setModel(new ModelEntity(mob, Echo.KEY.value()).getModel());

        playAnimation("wings_flap", true);
        playAnimation("hover", true);
        playAnimation("spawn", true);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
            CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED)
        );
        CreateSound.sound(Sound.BLOCK_SOUL_SOIL_BREAK, .7f).playAt(mob);
        CreateSound.sound(Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1.5f).playAt(mob);
        new ParticleBuilder(Particle.SOUL_FIRE_FLAME)
            .extra(.3)
            .offset(1, 1, 1)
            .count(CruxMath.random(10, 20))
            .location(mob.getLocation().add(0, mob.getHeight()/2, 0))
            .spawn()
        ;
    }

    public void transformToGrave(){
        ModeledEntity modeled = getModeledEntity();
        new ModelEntity(mob, "grave");
        modeled.removeModel(Echo.KEY.value());
    }

    public boolean isInGraveTransformation(){
        return getModeledEntity().getModel("grave").isPresent();
    }

    protected int checkNearbyPlayersCooldown = 100;

    @Override
    public void tick() {
        //this.checkTargetLogic();
        //this.checkTargetLogic(); is not aggressive without
        if(target != null && !isValidTarget(target)){
            setTarget(null);
        }
        this.targetLogic();
        this.sound();
        deathTick();

        if(target == null){
            if(mob.getTarget() != null) mob.setTarget(null);
        }
        checkNearbyPlayersTick();
    }

    public void checkNearbyPlayersTick(){
        if(checkNearbyPlayersCooldown > 0){
            checkNearbyPlayersCooldown--;
            return;
        }
        checkNearbyPlayersCooldown = 100;
        if(!isInGraveTransformation()) return;
        if(new GetEntityNear<>(Player.class).center(mob).range(8D).find().isEmpty()) return;
        transformToEcho();
    }

    protected int deathLeaving = 200;
    public void deathTick(){
        if(state != State.LEAVING) return;
        if(target != null) return;
        deathLeaving--;
        if(deathLeaving < 1){
            //todo animations and stuff
            mob.remove();
            return;
        }
        mob.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,
            mob.getEyeLocation(), 0);
    }

    public static int calculateExpToDropEachHit(double damageDealt, double mobHealth, int totalExp) {
        // Calculate the hitPercentage based on damage dealt
        double hitPercentage = damageDealt / mobHealth;

        // Calculate expToDropEachHit based on hitPercentage and totalExp
        int expToDropEachHit = (int) Math.ceil(hitPercentage * totalExp); // Use Math.ceil to ensure we don't drop zero experience

        // Ensure expToDropEachHit does not exceed remaining totalExp
        expToDropEachHit = Math.min(expToDropEachHit, totalExp);

        return expToDropEachHit;
    }

    public Menu buildInventory(){
        PlayerDeath death = getPlayerDeathOrNull();
        if(death==null) return null;
        return new DeathMenu(this, death);
    }

    public void inventoryClicked(@NotNull Collection<ItemStack> newDeathItems){
        if(!newDeathItems.isEmpty()) return;
        state = State.LEAVING;
    }

    protected Menu inventory;
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if(!event.getRightClicked().equals(mob)) return;
        if(target != null) return;
        Player p = event.getPlayer();
        if(inventory == null) inventory = buildInventory();

        if(inventory == null || inventory.getInventory().isEmpty()) return;
        inventory.open(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void entityDamage(CruxEntityDamageEvent event){
        if(mob.equals(event.getDamager())){
            playAnimation("attack", true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(mob.equals(event.getDamager())){
            playAnimation("attack", true);
            return;
        }
    }

    /*@EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if(!event.getEntity().equals(mob)) return;
        Entity target = event.getTarget();
        if(target==null) return;
        if(!target.equals(this.target)) event.setCancelled(true);
    }*/

    public void firstSetup(PlayerDeath death){
        exp = death.getExperience();

        ValuesProvider cfg = USurvivePlugin.inst().values();
        mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
            cfg.ECHO_MAX_HEALTH().sample(InputContext.simple(StringTagProvider.build(
                TagContainer.string(
                    Tag.parsed("exp", exp + ""),
                    Tag.parsed("level", CruxMath.calculateLevelsForExperiencePoints(exp) + "")
                )
            ))).doubleValue()
        );
        mob.setHealth(mob.getAttribute(Attribute.MAX_HEALTH).getValue());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(event.getDamageSource().getDamageType() == DamageType.GENERIC_KILL) return;
        if(isInGraveTransformation()){
            event.setCancelled(true);
            transformToEcho();
            return;
        }
        if(exp == null){
            Crux.log(Level.WARNING, "Echo was damaged without first setup! " + mob.getUniqueId() + ", " + mob.getWorld());
            return;
        }

        switch (event.getCause()){
            /*case FIRE, FIRE_TICK, FLY_INTO_WALL, HOT_FLOOR, LAVA, FALL, FALLING_BLOCK, LIGHTNING, DROWNING,
                 DRYOUT, SUFFOCATION ->{
                event.setCancelled(true);
                mob.setFireTicks(0);
                return;
            }*/
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, ENTITY_EXPLOSION, SUICIDE, KILL -> {}
            default ->{
                event.setCancelled(true);
                mob.setFireTicks(0);
                return;
            }
        }

        if(exp < 1) return;

        expToDropEachHit = calculateExpToDropEachHit(event.getFinalDamage(), mob.getAttribute(Attribute.MAX_HEALTH).getValue(), exp);

        exp = Math.max(exp - expToDropEachHit, 0);
        spawnExp(expToDropEachHit);
        CruxTag.set(mob, "exp", PersistentDataType.INTEGER, exp);
    }

    public PlayerDeath getPlayerDeathOrNull(){
        DeathManager deathManager = USurvivePlugin.inst().getDeathManager();
        return deathManager.getEchoToPlayerDeath().get(mob.getUniqueId());
    }

    public PlayerDeath getPlayerDeath(){
        PlayerDeath death = getPlayerDeathOrNull();
        if(death == null) throw new UnsupportedOperationException(mob.getUniqueId() + " does not have a set player death for it!");
        return death;
    }

    public void removed(){
        if(state == State.LEAVING){
            removeDeath();
        }

        if(inventory==null) return;
        inventory.close();
        inventory = null;
    }

    public void removeDeath(){
        PlayerDeath death = getPlayerDeathOrNull();
        if(death==null) return;
        DeathManager deathManager = USurvivePlugin.inst().getDeathManager();
        deathManager.removeDeath(death);

        /*deathManager.getDeaths().remove(
            death.getWorldUUID(),
            Chunk.getChunkKey(death.getPosition().toLocation(null)),
            death.getPosition()
        );*/
    }

    @Override
    protected void onRemovalOrDeath(boolean died) {
        super.onRemovalOrDeath(died);
        PlayerDeath leavingDeath = null;
        if(state == State.LEAVING){
            leavingDeath = getPlayerDeathOrNull();
        }
        removed();
        if(!died) return;
        PlayerDeath death = leavingDeath == null ? getPlayerDeath() : leavingDeath;
        if(leavingDeath == null) removeDeath();

        death.getDrops().forEach(drop ->{
            mob.getWorld().dropItem(mob.getLocation(), drop);
        });
        spawnExp(exp==null?death.getExperience():exp);
    }
    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    public void death(EntityDeathEvent event) {
        if (!event.getEntity().equals(this.mob)) return;
        event.getDrops().clear();
        event.setDroppedExp(0);

        CreateSound.sound(Sound.ENTITY_VEX_DEATH, 1.5f).playAt(mob);
        CreateSound.sound(Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 2f).playAt(mob);
        new ParticleBuilder(Particle.SOUL_FIRE_FLAME)
            .extra(.5)
            .offset(.5, .5, .5)
            .count(CruxMath.random(8, 15))
            .location(mob.getLocation().add(0, mob.getHeight()/2, 0))
            .spawn()

            .particle(Particle.SOUL)
            .count(0)
            .extra(0)
            .offset(0, 0, 0)
            .spawn()
        ;
    }

    public void spawnExp(int value){
        if(value < 1) return;
        mob.getWorld().spawn(mob.getLocation(), ExperienceOrb.class, e ->{
            e.setCount(1);
            e.setExperience(value);
        });
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getExpToDropEachHit() {
        return expToDropEachHit;
    }

    public void setExpToDropEachHit(Integer expToDropEachHit) {
        this.expToDropEachHit = expToDropEachHit;
    }

    @NotNull
    public Menu getInventory() {
        return inventory;
    }

    public void setInventory(Menu inventory) {
        this.inventory = inventory;
    }

    public enum State{
        LEAVING
    }
}

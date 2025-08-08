package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ActiveChainshockTickable extends ListenerActiveEntityTickable {
    protected final int maxLinks;
    protected final double linkRange;
    protected final Key displayTypeKey;
    protected final int level;
    protected final int linkDelay;

    public ActiveChainshockTickable(Entity entity, EntityTickable tickable, CruxSlot slot, int maxLinks, double linkRange, Key displayTypeKey, int level, int linkDelay) {
        super(entity, tickable, slot);
        this.maxLinks = maxLinks;
        this.linkRange = linkRange;
        this.displayTypeKey = displayTypeKey;
        this.level = level;
        this.linkDelay = linkDelay;
    }

    public boolean hasLinkedEntity(Entity e){
        LinkedEntity current = link;
        while(current != null){
            if(current.getEntity().equals(e)) return true;
            current = current.getHolding();
        }
        return false;
    }

    protected LinkedEntity link;
    public void updateDisplays() {
        LinkedEntity current = link;
        while (current != null && current.getDisplay() != null) {
            Entity first = current.getEntity();
            Entity second = current.getHolding().getEntity();
            updateDisplay(
                current.getDisplay(),
                first.getLocation().add(0, current == link ? first.getHeight() * .3 : first.getHeight() * .5, 0),
                second.getLocation().add(0, second.getHeight() * .5, 0)
            );
            current = current.getHolding();
        }
    }

    public boolean hasValidLink(){
        return link != null && link.getHolding() != null;
    }

    public LinkedEntity getLink() {
        return link;
    }

    public static @NotNull Matrix4f centerPivot(@NotNull Matrix4f addon, @NotNull Vector pivot) {
        float x = (float) pivot.getX();
        float y = (float) pivot.getY();
        float z = (float) pivot.getZ();
        Matrix4f center = new Matrix4f().translation(x, y, z);
        // Create a matrix for the reset translation (back to the original position)
        Matrix4f reset = new Matrix4f().translation(x * -1f, y * -1f, z * -1f);

        // Combine the transformations
        Matrix4f combined = new Matrix4f();
        center.mul(addon, combined);
        combined.mul(reset);

        // Now, 'combined' matrix represents the transformation
        // Apply this matrix to your object's vertices or points.
        Vector3f originalPoint = new Vector3f(0, 0, 0);
        Vector3f transformedPoint = new Vector3f();
        combined.transformPosition(originalPoint, transformedPoint);

        return combined;
    }

    private Quaternionf getQuaternionFromDirection(Vector direction) {
        // Forward vector in Minecraft (default facing negative Z)
        Vector forward = new Vector(0, 1, 0);

        // Get the rotation axis and angle needed to rotate forward to target direction
        Vector axis = forward.getCrossProduct(direction).normalize();
        double angle = Math.acos(forward.dot(direction));

        // Convert to JOML Quaternionf
        return new Quaternionf(new AxisAngle4f((float) angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ()));
    }

    private Vector getCenterOffset(Display display) {
        if (display instanceof BlockDisplay) return new Vector(.5, 0, .5);
        return new Vector(0, -.5, 0);
    }

    public void updateDisplay(Display display, Location start, Location end) {
        updateDisplay(display, start, end, 1);
    }

    public void updateDisplay(Display display, Location start, Location end, int interpolate) {
        Vector direction = end.toVector().subtract(start.toVector());
        float distance = (float) direction.length();
        Location tp;
        if (display instanceof BlockDisplay) {
            tp = start.clone().add(-.5, .5, -.5);
        } else tp = start.clone().add(0, .8, 0);
        tp.setPitch(0f);
        tp.setYaw(0f);
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(interpolate);
        direction.normalize();
        Quaternionf rotation = getQuaternionFromDirection(direction);

        float scale = CruxMath.clamp(distance, 1f, 50f);

        Vector offset = tp.toVector().subtract(display.getLocation().toVector());
        display.setTransformationMatrix(
            new Matrix4f()
                .translate((float) offset.getX(), (float) offset.getY(), (float) offset.getZ())
                .mul(centerPivot(new Matrix4f().rotation(rotation).scale(1, scale, 1),
                    getCenterOffset(display)))
        );
    }

    public Display createDisplay(Entity first, Entity second) {
        Location loc = first.getLocation().add(0, first.getHeight() * .5, 0);
        loc.setPitch(0f);
        loc.setYaw(0f);
        return first.getWorld().spawn(
            loc, BlockDisplay.class, e -> {
                e.setPersistent(false);
                if (e instanceof BlockDisplay b) {
                    b.setBlock(Registry.BLOCK.get(displayTypeKey).createBlockData());
                } else if (e instanceof ItemDisplay b) {
                    b.setItemStack(Crux.handlers().item().getItem(displayTypeKey).value());
                }
                e.setBrightness(new Display.Brightness(15, 15));
            }
        );
    }

    public void chainLinkEntities(Entity start) {
        link = new LinkedEntity(entity, createDisplay(entity, start), null);

        LinkedEntity startingLink = new LinkedEntity(start);
        link.setHolding(startingLink);

        LinkedEntity previous = startingLink;
        Collection<Entity> targets = new ArrayList<>();
        targets.add(start);
        for (int i = 0; i < maxLinks; i++) {
            Entity e = new GetEntityNear<>(LivingEntity.class)
                .center(previous.getEntity())
                .filter(check -> !check.equals(entity) && !targets.contains(check))
                .operation(GetNear.Operation.NEAREST)
                .amount(1)
                .range(linkRange)
                .findFirst();
            if (e == null) break;
            targets.add(e);

            LinkedEntity currentLink = new LinkedEntity(e);
            previous.setHolding(currentLink);
            previous.setDisplay(createDisplay(previous.getEntity(), currentLink.getEntity()));
            previous = currentLink;
        }
        //updateDisplays();
    }

    protected Entity scheduledStarterLink;

    public Entity getScheduledStarterLink() {
        return scheduledStarterLink;
    }

    public void setScheduledStarterLink(Entity scheduledStarterLink) {
        this.scheduledStarterLink = scheduledStarterLink;
    }
    public void updateDisplay(LinkedEntity linked){
        updateDisplay(linked, 1);
    }

    public void updateDisplay(LinkedEntity linked, int interpolation){
        if(linked.getHolding() == null) return;
        Display display = linked.getDisplay();
        if(display == null) return;
        Entity first = linked.getEntity();
        Entity second = linked.getHolding().getEntity();

        updateDisplay(
            display,
            first.getLocation().add(0, linked == link ? first.getHeight() * .3 : first.getHeight() * .5, 0),
            second.getLocation().add(0, second.getHeight() * .5, 0),
            interpolation
        );
    }

    public void deleteChainsFrom(LinkedEntity link, boolean playEffect){
        LinkedEntity current = link;
        while (current != null){
            Display display = current.getDisplay();
            if(display != null){
                display.remove();
                current.setDisplay(null);
            }
            LinkedEntity unlink = current;
            current = current.getHolding();

            if(playEffect) breakChainEffect(unlink);

            unlink.setHolding(null);
        }
    }

    public void updateChainLinks(boolean updateDisplays) {
        Map<Entity, Vector> velocityChanges = new HashMap<>();

        LinkedEntity previous = null;
        LinkedEntity current = link;
        while (current != null && current.getHolding() != null) {
            Entity from = current.getEntity();

            if(!current.isValid()){
                if(previous == null) break;
                LinkedEntity finalCurrent = previous;
                Crux.scheduler().runTask(() ->{
                    deleteChainsFrom(finalCurrent, true);
                });
                break;
            }
            previous = current;

            if(updateDisplays){
                updateDisplay(current);
            }

            current = current.getHolding();
            Entity to = current.getEntity();

            if (to == null || from.isDead() || to.isDead()) continue;
            if (!from.getWorld().equals(to.getWorld())) continue;

            double maxDistance = 4 * 4;
            double distance = from.getLocation().distanceSquared(to.getLocation());

            if (distance > maxDistance) {
                Vector direction = from.getLocation().toVector().subtract(to.getLocation().toVector()).normalize();
                //double tension = (distance - maxDistance);
                double tension = Math.pow(distance - maxDistance, 0.6); // or 0.75

                double strength = 0.1;

                Vector pull = direction.multiply(tension * strength);
                velocityChanges.merge(to, pull, Vector::add); // accumulate force
            }
        }

        // Now apply all forces after calculation
        for (Map.Entry<Entity, Vector> entry : velocityChanges.entrySet()) {
            Entity entity = entry.getKey();
            Vector pull = entry.getValue();

            Vector currentVel = entity.getVelocity();
            entity.setVelocity(currentVel.multiply(0.8).add(pull));
        }
    }

    @Override
    public void stopped() {
        super.stopped();
        Crux.scheduler().runTask(() ->{
            LinkedEntity current = link;
            while (current != null) {
                if(current.getDisplay() != null){
                    current.getDisplay().remove();
                    breakChainEffect(current);
                }
                current = current.getHolding();
            }
            link = null;
        });
    }

    public LinkedEntity nextChainLink(LinkedEntity previous){
        Location loc = previous.getEntity().getLocation().add(0,
            previous.getEntity().getHeight()*.5, 0);
        Entity e = new GetEntityNear<>(LivingEntity.class)
            .center(previous.getEntity())
            .filter(check ->{
                if(check.equals(entity)) return false;
                if(hasLinkedEntity(check)) return false;
                Vector dir = check.getLocation().add(0, check.getHeight()*.5,0).toVector().subtract(loc.toVector());
                RayTraceResult result = loc.getWorld().rayTrace(loc,
                    dir, linkRange, FluidCollisionMode.NEVER, true, .2D, rayCheck -> rayCheck.equals(check));
                return result != null && result.getHitEntity() != null;
            })
            .operation(GetNear.Operation.NEAREST)
            .amount(1)
            .range(linkRange)
            .findFirst();
        if (e == null) return null;
        LinkedEntity currentLink = new LinkedEntity(e);
        previous.setHolding(currentLink);
        previous.setDisplay(createDisplay(previous.getEntity(), currentLink.getEntity()));
        return currentLink;
    }

    public void playLinkSound(LinkedEntity link){
        if(link.getHolding() == null) return;
        Location center = getCenter(link);
        center.getWorld().playSound(center, Sound.BLOCK_CHAIN_PLACE, 1f, 0.8f + (float)Math.random() * 0.4f);
    }

    public void chainLinkStart(Entity start){
        link = new LinkedEntity(entity, createDisplay(entity, start), null);

        LinkedEntity startingLink = new LinkedEntity(start);
        link.setHolding(startingLink);
        currentLink = startingLink;
        playLinkSound(link);
    }

    public void chainLinkTick(){
        if(currentLink == null) return;

        LinkedEntity next = nextChainLink(currentLink);
        if(next == null){
            currentLinkAmount = maxLinks;
            return;
        }
        currentLink.setHolding(next);
        playLinkSound(currentLink);
        currentLink = next;
    }

    protected LinkedEntity currentLink;
    protected int currentLinkAmount = 0;
    protected int linkCooldown = 0;
    protected boolean tickingLinks = false;
    @Override
    public void tick() {
        if(scheduledStarterLink != null){
            if(!tickingLinks){
                tickingLinks = true;
                Crux.scheduler().runTask(() ->{
                    chainLinkStart(scheduledStarterLink);
                    scheduledStarterLink = null;
                    tickingLinks = false;
                });
            }
            return;
        }
        if(currentLinkAmount < maxLinks && !tickingLinks){
            if(linkCooldown < 1){
                linkCooldown = linkDelay;
                tickingLinks = true;
                Crux.scheduler().runTask(() ->{
                    chainLinkTick();
                    tickingLinks = false;
                });
            }else{
                linkCooldown--;
            }
        }
        updateChainLinks(true);
        //updateDisplays();
    }

    public void breakChainEffect(LinkedEntity linked){
        if(linked.getHolding() == null) return;
        Entity first = linked.getEntity();
        Entity second = linked.getHolding().getEntity();

        Location center = first.getLocation().add(0, first.getHeight()*.5, 0)
            .add(second.getLocation().add(0, second.getHeight()*.5,0)).multiply(0.5);

        center.getWorld().playSound(center, Sound.BLOCK_CHAIN_BREAK, 1f, 0.8f + (float)Math.random() * 0.4f);

        center.getWorld().spawnParticle(Particle.ITEM, center, 20, 0.2, 0.2, 0.2, 0.05, new ItemStack(Material.CHAIN));
        center.getWorld().spawnParticle(Particle.SMOKE, center, 10, 0.2, 0.2, 0.2, 0.01);
    }

    public Location getCenter(LinkedEntity linked){
        Entity first = linked.getEntity();
        Entity second = linked.getHolding().getEntity();
        return first.getLocation().add(0, first.getHeight()*.5, 0)
            .add(second.getLocation().add(0, second.getHeight()*.5,0)).multiply(0.5);
    }

    public void triggerLaunchOnLinkedEntities(Player player, Vector direction, double power) {
        if(link == null) return;
        LinkedEntity current = link.getHolding();
        breakChainEffect(link);
        if(link.getDisplay() != null){
            link.getDisplay().remove();
            link.setDisplay(null);
        }
        this.link = current;
        while (current != null) {
            Entity e = current.getEntity();
            Vector finalVector = direction.clone().multiply(power);
            e.setVelocity(finalVector);

            current = current.getHolding();
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if(!event.getPlayer().equals(entity)) return;
        if (!event.getAction().isLeftClick()) return;

        Player player = event.getPlayer();
        LookData last = lastLookData;
        if (last == null) return;

        float currentPitch = player.getLocation().getPitch();
        long now = System.currentTimeMillis();
        long deltaTime = now - last.lastTime;
        float deltaPitch = Math.abs(currentPitch - last.lastPitch);

        double pitchSpeed = deltaPitch / deltaTime;

        // Check for sharp upward flick
        Vector launchDirection = player.getLocation().getDirection().normalize();
        double launchPower = Math.min(1.5 * (level+1), (pitchSpeed * 5) * level); // Cap to avoid insanity

        triggerLaunchOnLinkedEntities(player, launchDirection, launchPower);
    }


    protected LookData lastLookData;
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        float currentYaw = player.getLocation().getYaw();
        float currentPitch = player.getLocation().getPitch();
        long now = System.currentTimeMillis();

        LookData last = lastLookData;
        if (last != null) {
            float deltaYaw = Math.abs(currentYaw - last.lastYaw);
            float deltaPitch = Math.abs(currentPitch - last.lastPitch);
            long deltaTime = now - last.lastTime;

            if (deltaTime > 0) {
                double pitchSpeed = deltaPitch / deltaTime; // degrees per millisecond
                double yawSpeed = deltaYaw / deltaTime;

                // Optional: check if this was a sharp "look up" flick
                /*if (deltaPitch > 40 && pitchSpeed > 0.4) {
                }*/
            }
        }

        lastLookData = new LookData(currentYaw, currentPitch, now);
    }

    public static class LinkedEntity{
        protected final Entity entity;
        protected Display display;
        protected LinkedEntity holding;
        protected long displaySet;

        public LinkedEntity(Entity entity){
            this.entity = entity;
        }

        public LinkedEntity(Entity entity, Display display, LinkedEntity holding) {
            this.entity = entity;
            this.display = display;
            this.holding = holding;
            if(display != null){
                displaySet = System.currentTimeMillis();
            }
        }

        public boolean isValid(){
            return CruxEntityUtil.isValid(entity);
        }

        public void setDisplay(Display display) {
            this.display = display;
            this.displaySet = System.currentTimeMillis();
        }

        public boolean ifDisplayWasSetWithin(int ticks){
            return CruxMath.hasOccurredWithin(displaySet, ticks);
        }

        public void setHolding(LinkedEntity holding) {
            this.holding = holding;
        }

        public Entity getEntity() {
            return entity;
        }

        public Display getDisplay() {
            return display;
        }

        public LinkedEntity getHolding() {
            return holding;
        }
    }

    public static class LookData {
        public float lastYaw;
        public float lastPitch;
        public long lastTime;

        public LookData(float yaw, float pitch, long time) {
            this.lastYaw = yaw;
            this.lastPitch = pitch;
            this.lastTime = time;
        }
    }
}

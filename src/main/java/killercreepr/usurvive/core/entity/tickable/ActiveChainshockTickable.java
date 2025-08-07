package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ActiveChainshockTickable extends ListenerActiveEntityTickable {
    protected final int maxLinks;
    protected final double linkRange;
    protected final Key displayTypeKey;
    public ActiveChainshockTickable(Entity entity, EntityTickable tickable, CruxSlot slot, int maxLinks, double linkRange, Key displayTypeKey) {
        super(entity, tickable, slot);
        this.maxLinks = maxLinks;
        this.linkRange = linkRange;
        this.displayTypeKey = displayTypeKey;
    }

    protected final List<Entity> targets = new ArrayList<>();
    protected final List<Display> displays = new ArrayList<>();

    public void updateDisplays(){
        for(int i = 0; i < targets.size(); i += 2){
            Entity first;
            Entity second;
            Display display;
            if(i == 0){
                first = entity;
                display = displays.getFirst();
            }else{
                first = targets.get(i-1);
                display = displays.get(i-1);
            }
            second = targets.get(i);
            updateDisplay(
                display,
                first.getLocation().add(0, i == 0 ? first.getHeight()*.3 : first.getHeight()*.5, 0),
                second.getLocation().add(0, second.getHeight()*.5, 0)
            );
        }
    }

    public static @NotNull Matrix4f centerPivot(@NotNull Matrix4f addon, @NotNull Vector pivot){
        float x = (float) pivot.getX();
        float y = (float) pivot.getY();
        float z = (float) pivot.getZ();
        Matrix4f center = new Matrix4f().translation(x, y, z);
        // Create a matrix for the reset translation (back to the original position)
        Matrix4f reset = new Matrix4f().translation(x*-1f, y*-1f, z*-1f);

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

    private Vector getCenterOffset(Display display){
        if(display instanceof BlockDisplay) return new Vector(.5, 0, .5);
        return new Vector(0, -.5, 0);
    }

    public void updateDisplay(Display display, Location start, Location end){
        updateDisplay(display, start, end, 1);
    }

    public void updateDisplay(Display display, Location start, Location end, int interpolate) {
        Vector direction = end.toVector().subtract(start.toVector());
        float distance = (float) direction.length();
        Location tp;
        if(display instanceof BlockDisplay){
            tp = start.clone().add(-.5,.5,-.5);
        }else tp = start.clone().add(0, .8, 0);
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

    public Display createDisplay(Entity first, Entity second){
        Location loc = first.getLocation().add(0, first.getHeight()*.5, 0);
        loc.setPitch(0f);
        loc.setYaw(0f);
        return first.getWorld().spawn(
            loc, BlockDisplay.class, e ->{
                e.setPersistent(false);
                if(e instanceof BlockDisplay b){
                    b.setBlock(Registry.BLOCK.get(displayTypeKey).createBlockData());
                }else if(e instanceof ItemDisplay b){
                    b.setItemStack(Crux.handlers().item().getItem(displayTypeKey).value());
                }
                e.setBrightness(new Display.Brightness(15, 15));
            }
        );
    }

    public void chainLinkEntities(Entity start){
        targets.add(start);
        displays.add(createDisplay(entity, start));
        Entity e = start;
        int index = 0;
        for(int i = 0; i < maxLinks; i++){
            index++;
            e = new GetEntityNear<>(LivingEntity.class)
                .center(e)
                .filter(check -> !check.equals(entity) && !targets.contains(check))
                .operation(GetNear.Operation.NEAREST)
                .amount(1)
                .range(linkRange)
                .findFirst();
            if(e == null) break;
            targets.add(e);

            Entity previous = targets.get(index-1);
            displays.add(createDisplay(e, previous));
        }
        updateDisplays();
    }

    protected Entity scheduledStarterLink;

    public Entity getScheduledStarterLink() {
        return scheduledStarterLink;
    }

    public void setScheduledStarterLink(Entity scheduledStarterLink) {
        this.scheduledStarterLink = scheduledStarterLink;
    }

    @Override
    public void tick() {
        if(scheduledStarterLink != null){
            chainLinkEntities(scheduledStarterLink);
            scheduledStarterLink = null;
            return;
        }
        updateDisplays();
    }
}

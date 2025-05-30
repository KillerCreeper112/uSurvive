package killercreepr.usurvive.core.mechanics.grapple.standard;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.context.DisplayGrappleContext;
import killercreepr.usurvive.api.mechanics.grapple.context.GrappleContext;
import killercreepr.usurvive.core.mechanics.grapple.impl.*;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public class StandardGrappleHooks {
    public static GrappleHook DEFAULT = new DefaultGrappleHook(Crux.key("default"));
    public static GrappleHook CONTROLLED = new ControlledGrappleHook(Crux.key("controlled"));
    public static GrappleHook PUFFERFISH = new PufferFishGrappleHook(Crux.key("pufferfish"));
    public static GrappleHook CRIT = new GenericGrappleHook(Crux.key("crit")) {
        @NotNull
        @Override
        public ActiveGrappleHook throwNew(@NotNull GrappleContext ctx) {
            return new ActiveParticleGrappleHook(
                ctx.getLocation(), ctx,
                12, ctx.getThrowForce(), .3D, .5D,
                new ParticleBuilder(Particle.CRIT).extra(0D)
            );
        }
    };
    public static GrappleHook DISPLAY = new GenericGrappleHook(Crux.key("display")) {
        @NotNull
        @Override
        public ActiveGrappleHook throwNew(@NotNull GrappleContext ctx) {
            Class<? extends Display> displayType;
            Key typeKey;

            int maxDistance;
            double throwSpeed = ctx.getThrowForce();
            double retractSpeed;

            if(ctx instanceof DisplayGrappleContext ct){
                displayType = ct.getDisplayType();
                typeKey = ct.getTypeKey();
                maxDistance = ct.getMaxDistance();
                retractSpeed = ct.getRetractSpeed();
            }else{
                displayType = BlockDisplay.class;
                typeKey = Key.key("chain");
                maxDistance = 15;
                retractSpeed = .3D;
            }
            Location loc = ctx.getLocation().clone();
            loc.setPitch(0f);
            loc.setYaw(0f);
            Display display = loc.getWorld().spawn(
                loc, displayType, e ->{
                    if(e instanceof BlockDisplay b){
                        b.setBlock(Registry.BLOCK.get(typeKey).createBlockData());
                    }else if(e instanceof ItemDisplay b){
                        b.setItemStack(Crux.handlers().item().getItem(typeKey).value());
                    }
                    e.setBrightness(new Display.Brightness(15, 15));
                }
            );
            return new ActiveDisplayGrappleHook(
                ctx.getLocation(), ctx,
                maxDistance, throwSpeed, retractSpeed,
                display
            );
        }
    };
    public static void register(@NotNull KeyedRegistry<GrappleHook> registry){
        registry.register(PUFFERFISH);
        registry.register(CRIT);
        registry.register(DEFAULT);
        registry.register(CONTROLLED);
        registry.register(DISPLAY);
    }
}

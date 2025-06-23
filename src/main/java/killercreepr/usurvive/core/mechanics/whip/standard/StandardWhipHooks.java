package killercreepr.usurvive.core.mechanics.whip.standard;

import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.mechanics.whip.ActiveWhip;
import killercreepr.usurvive.api.mechanics.whip.WhipHook;
import killercreepr.usurvive.api.mechanics.whip.context.DisplayWhipContext;
import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import killercreepr.usurvive.core.mechanics.whip.SimpleWhipHook;
import killercreepr.usurvive.core.mechanics.whip.active.ActiveDisplayWhip;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public class StandardWhipHooks {
    public static WhipHook DISPLAY = new SimpleWhipHook(Crux.key("display")) {
        @NotNull
        @Override
        public ActiveWhip throwNew(@NotNull WhipContext ctx) {
            Class<? extends Display> displayType;
            Key typeKey;

            int maxDistance;
            double throwSpeed = ctx.getThrowForce();
            double retractSpeed;

            if(ctx instanceof DisplayWhipContext ct){
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
                    e.setPersistent(false);
                    if(e instanceof BlockDisplay b){
                        b.setBlock(Registry.BLOCK.get(typeKey).createBlockData());
                    }else if(e instanceof ItemDisplay b){
                        b.setItemStack(Crux.handlers().item().getItem(typeKey).value());
                    }
                    e.setBrightness(new Display.Brightness(15, 15));
                }
            );
            return new ActiveDisplayWhip(
                ctx.getLocation(), ctx.key(),
                maxDistance, throwSpeed, retractSpeed, ctx.getPierce(),
                display, ctx.getAttributes()
            );
        }
    };
    public static void register(@NotNull KeyedRegistry<WhipHook> registry){
        registry.register(DISPLAY);
    }
}

package killercreepr.usurvive.core.mechanics.whip;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxitems.api.item.context.PlayerItemedContext;
import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import killercreepr.usurvive.core.mechanics.whip.context.DisplaySimpleWhipContext;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SimpleDisplayWhip extends SimpleWhip {
    protected final Key typeKey;
    protected final String displayType;

    public SimpleDisplayWhip(@NotNull Key hook, int maxHooks, int cooldown,
                             int itemDamage,
                             CreateSound throwSound,
                             Key typeKey, String displayType) {
        super(hook, maxHooks, cooldown, itemDamage, throwSound);
        this.typeKey = typeKey;
        this.displayType = displayType;
    }

    @Override
    public WhipContext buildWhipContext(PlayerItemedContext ctx, int id) {
        Player p = ctx.getPlayer();
        return new DisplaySimpleWhipContext(
            p.getEyeLocation(), p, id,
            CruxAttribute.get(p, USurviveAttribute.WHIP_THROW_FORCE),
            (int) CruxAttribute.get(p, USurviveAttribute.WHIP_PIERCE),
            typeKey,
            "item".equalsIgnoreCase(displayType) ? ItemDisplay.class : BlockDisplay.class,
            (int) CruxAttribute.get(p, USurviveAttribute.WHIP_RANGE),
            CruxAttribute.get(p, USurviveAttribute.WHIP_RETRACT_SPEED)
        );
    }

    public Key getTypeKey() {
        return typeKey;
    }

    public String getDisplayType() {
        return displayType;
    }
}

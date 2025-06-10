package killercreepr.usurvive.core.entity.tickable;

import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.SimpleActiveEntityTickable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class AttributesTickable extends SimpleActiveEntityTickable {
    protected final Map<Attribute, AttributeModifier> attributes;
    public AttributesTickable(Entity entity, EntityTickable tickable, CruxSlot slot, Map<Attribute, AttributeModifier> attributes) {
        super(entity, tickable, slot);
        this.attributes = attributes;
    }

    @Override
    public void started() {
        super.started();
        if(!(entity instanceof LivingEntity li)) return;

        attributes.forEach((att, mod) ->{
            var instance = li.getAttribute(att);
            if(instance == null) return;
            instance.addTransientModifier(mod);
        });
    }

    @Override
    public void stopped() {
        super.stopped();
        if(!(entity instanceof LivingEntity li)) return;

        attributes.forEach((att, mod) ->{
            var instance = li.getAttribute(att);
            if(instance == null) return;
            instance.removeModifier(mod.key());
        });
    }

    @Override
    public void tick() {

    }
}

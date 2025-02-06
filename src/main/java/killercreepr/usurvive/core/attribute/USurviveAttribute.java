package killercreepr.usurvive.core.attribute;

import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.core.attribute.GenericAttribute;
import killercreepr.cruxattributes.core.registries.CruxAttributeRegistries;

public class USurviveAttribute {
    public static void register(){}
    public static final CruxAttribute GRAPPLE_DAMAGE = register(new GenericAttribute(Crux.key("grapple_damage")));
    public static final CruxAttribute GRAPPLE_KNOCKBACK = register(new GenericAttribute(Crux.key("grapple_knockback")));
    public static final CruxAttribute GRAPPLE_KNOCKBACK_UP = register(new GenericAttribute(Crux.key("grapple_upwards_knockback")));

    private static CruxAttribute register(CruxAttribute a){
        return CruxAttributeRegistries.ATTRIBUTES.register(a);
    }
}
